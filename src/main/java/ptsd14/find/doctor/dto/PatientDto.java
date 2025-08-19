package ptsd14.find.doctor.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PatientDto {
    private Long id;
    private String firstname;
    private String lastname;
    private String status;
    private String gender;
    private LocalDate dateOfBirth;
    private String address;
    private Long userId;
    private String userEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
