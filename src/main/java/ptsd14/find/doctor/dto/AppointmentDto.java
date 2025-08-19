package ptsd14.find.doctor.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AppointmentDto {

    private Long id;
    private Long doctorId;
    private String doctorName;
    private Long patientId;
    private String patientName;
    private Long appointmentTypeId;
    private String appointmentTypeName;
    private String doctorHospitalName;
    private String doctorHospitalPhone;
    private LocalDateTime dateTime;
    private String note;
    private String status;
    private BigDecimal amount;
    private String paymentStatus;
    private boolean feedbackGiven;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
