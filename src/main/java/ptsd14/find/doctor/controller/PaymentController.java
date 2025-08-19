package ptsd14.find.doctor.controller;

import com.stripe.model.PaymentIntent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ptsd14.find.doctor.dto.AppointmentDto;
import ptsd14.find.doctor.dto.PaymentDto;
import ptsd14.find.doctor.dto.PaymentRequest;
import ptsd14.find.doctor.service.PaymentService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Get all payments
    @GetMapping
    public ResponseEntity<Page<PaymentDto>> getAllPayments(
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String status
    ) {
        int pageNumber = (page != null && page >= 0) ? page : 0;
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<PaymentDto> paymentsPage = paymentService.getAll(pageable, search, status);
        return ResponseEntity.ok(paymentsPage);
    }

    // Get payment by ID
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable Long id) {
        PaymentDto payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    // Create Stripe PaymentIntent securely using DTO
    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(
            @Valid @RequestBody PaymentRequest request) {
        try {
            PaymentIntent paymentIntent = paymentService.createPaymentIntent(
                    request.getAppointmentId(),
                    request.getAmountInCents(),
                    request.getCurrency()
            );

            // Return client_secret only
            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to create payment intent: " + e.getMessage());
        }
    }

    @GetMapping("/unpaid-appointments")
    public ResponseEntity<List<AppointmentDto>> getUnpaidAppointments() {
        List<AppointmentDto> appointments = paymentService.getUnpaidAppointments();
        return ResponseEntity.ok(appointments);
    }
    @PostMapping("/{id}/refund")
    public ResponseEntity<?> refundPayment(@PathVariable Long id) {
    paymentService.refundPayment(id);
    return ResponseEntity.ok("Payment refunded successfully.");
    }

    @PostMapping("/pay-cash")
    public ResponseEntity<String> payByCash(
            @RequestParam Long appointmentId,
            @RequestParam BigDecimal amount) {

        paymentService.markPaidCash(appointmentId, amount);
        return ResponseEntity.ok("Payment by cash recorded successfully");
    }


}
