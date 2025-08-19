package ptsd14.find.doctor.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be at least 1 cent")
    private Long amountInCents;

    private String currency = "usd";
}