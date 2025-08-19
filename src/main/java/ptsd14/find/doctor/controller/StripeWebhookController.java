package ptsd14.find.doctor.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import ptsd14.find.doctor.model.Payment;
import ptsd14.find.doctor.model.Appointment;
import ptsd14.find.doctor.repository.PaymentRepository;
import ptsd14.find.doctor.repository.AppointmentRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;

    @PostConstruct
    public void init() {
        log.info("Loaded Stripe webhook secret: {}", endpointSecret);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(HttpServletRequest request,
                                                      @RequestHeader("Stripe-Signature") String sigHeader) {
        String payload;

        try {
            payload = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error reading webhook request body", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to read payload");
        }

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Webhook signature verification failed.", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            log.error("Webhook error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error");
        }

            log.info("Received Stripe event: type={}, id={}", event.getType(), event.getId());

        if ("payment_intent.succeeded".equals(event.getType())) {
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();

            Optional<PaymentIntent> paymentIntentOptional = dataObjectDeserializer.getObject()
                    .map(obj -> (PaymentIntent) obj);

            if (paymentIntentOptional.isEmpty()) {
                log.error("Could not deserialize PaymentIntent from event data");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to deserialize payment intent");
            }

            PaymentIntent paymentIntent = paymentIntentOptional.get();

            log.info("PaymentIntent succeeded: id={}, amount={}, currency={}",
                    paymentIntent.getId(), paymentIntent.getAmount(), paymentIntent.getCurrency());

            // Extract appointmentId from metadata
            String appointmentIdStr = paymentIntent.getMetadata().get("appointmentId");
            if (appointmentIdStr == null || appointmentIdStr.isEmpty()) {
                log.warn("No appointmentId metadata found in PaymentIntent {}", paymentIntent.getId());
                return ResponseEntity.ok("");
            }

            Long appointmentId;
            try {
                appointmentId = Long.parseLong(appointmentIdStr);
            } catch (NumberFormatException ex) {
                log.error("Invalid appointmentId in metadata: {}", appointmentIdStr);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid appointmentId metadata");
            }

            Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);

            if (appointment == null) {
                log.warn("Appointment not found for ID: {}", appointmentId);
                return ResponseEntity.ok(""); 
            }

            // Save payment record
            Payment payment = new Payment();
            payment.setAmount(BigDecimal.valueOf(paymentIntent.getAmount()).movePointLeft(2));
            payment.setStripePaymentIntentId(paymentIntent.getId()); 
            payment.setPaymentStatus("PAID");
            payment.setPaymentMethod(paymentIntent.getPaymentMethod() != null ? paymentIntent.getPaymentMethod() : "unknown");
            payment.setPaidAt(LocalDateTime.now());
            payment.setAppointment(appointment);

            paymentRepository.save(payment);


            log.info("Payment saved for appointmentId {}", appointmentId);
        } else {
            log.info("Unhandled event type: {}", event.getType());
        }

        return ResponseEntity.ok("");
    }
}
