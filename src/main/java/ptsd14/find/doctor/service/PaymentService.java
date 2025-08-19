package ptsd14.find.doctor.service;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptsd14.find.doctor.dto.AppointmentDto;
import ptsd14.find.doctor.dto.PaymentDto;
import ptsd14.find.doctor.exception.AppointmentNotFoundException;
import ptsd14.find.doctor.exception.ResourceNotFoundException;
import ptsd14.find.doctor.mapper.AppointmentMapper;
import ptsd14.find.doctor.mapper.PaymentMapper;
import ptsd14.find.doctor.model.Appointment;
import ptsd14.find.doctor.model.Payment;
import ptsd14.find.doctor.repository.AppointmentRepository;
import ptsd14.find.doctor.repository.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentMapper paymentMapper;
    private final AppointmentMapper appointmentMapper;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public PaymentIntent createPaymentIntent(Long appointmentId, Long amountInCents, String currency) throws Exception {
        Stripe.apiKey = stripeSecretKey;

        appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(currency)
                .addPaymentMethodType("card")
                .putMetadata("appointmentId", appointmentId.toString())
                .build();
                
        return PaymentIntent.create(params);
        
    }

    public void savePaymentFromIntent(PaymentIntent paymentIntent) {
        Long appointmentId = Long.valueOf(paymentIntent.getMetadata().get("appointmentId"));

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));

        Payment payment = new Payment();
        payment.setAmount(BigDecimal.valueOf(paymentIntent.getAmount()).movePointLeft(2)); 
        payment.setPaymentStatus("COMPLETED");
        payment.setPaymentMethod(paymentIntent.getPaymentMethod() != null ? paymentIntent.getPaymentMethod() : "unknown");
        payment.setStripePaymentIntentId(paymentIntent.getId());
        payment.setPaidAt(LocalDateTime.now());
        payment.setAppointment(appointment);

        paymentRepository.save(payment);
        log.info("Payment saved for appointmentId: {}", appointmentId);
    }

    public Page<PaymentDto> getAll(Pageable pageable, String search, String status) {
    boolean hasSearch = search != null && !search.trim().isEmpty();
    boolean hasStatus = status != null && !status.trim().isEmpty();

    Page<Payment> payments;

    if (hasSearch && hasStatus) {
        payments = paymentRepository.findByPaymentStatusIgnoreCaseAndAppointment_Patient_FirstnameContainingIgnoreCaseOrPaymentStatusIgnoreCaseAndAppointment_Doctor_FirstnameContainingIgnoreCase(
                status.trim(), search.trim(),
                status.trim(), search.trim(),
                pageable
        );
    } else if (hasStatus) {
        payments = paymentRepository.findByPaymentStatusIgnoreCase(status.trim(), pageable);
    } else if (hasSearch) {
        payments = paymentRepository.findByAppointment_Patient_FirstnameContainingIgnoreCaseOrAppointment_Doctor_FirstnameContainingIgnoreCase(
                search.trim(), search.trim(), pageable
        );
    } else {
        payments = paymentRepository.findAll(pageable);
    }

    return payments.map(paymentMapper::toDto);
}


    public PaymentDto getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id " + id));
        return paymentMapper.toDto(payment);
    }

    public List<AppointmentDto> getUnpaidAppointments() {
        return appointmentRepository.findByPaymentIsNull()
                .stream()
                .map(appointmentMapper::toDto)
                .toList();
    }

    public void refundPayment(Long id) {
    Stripe.apiKey = stripeSecretKey;

    Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id " + id));

    if (payment.getStripePaymentIntentId() == null) {
        throw new IllegalStateException("No Stripe PaymentIntent ID associated with this payment.");
    }

    try {
        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(payment.getStripePaymentIntentId())
                .build();

        Refund.create(params); 

        payment.setPaymentStatus("REFUNDED");
        paymentRepository.save(payment);

        log.info("Refund created successfully for paymentId {}", id);
    } catch (Exception e) {
        log.error("Failed to refund paymentId {}: {}", id, e.getMessage());
        throw new RuntimeException("Refund failed: " + e.getMessage());
    }
}

    @Transactional
    public void markPaidCash(Long appointmentId, BigDecimal amount) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Payment payment = new Payment();
        payment.setAppointment(appointment);
        payment.setAmount(amount);
        payment.setPaymentMethod("CASH");
        payment.setPaymentStatus("PAID");
        payment.setPaidAt(LocalDateTime.now());

        paymentRepository.save(payment);
    }



}
