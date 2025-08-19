package ptsd14.find.doctor.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DoctorDto {
    private Long id;
    private String firstname;
    private String lastname;
    private Long specializationId;
    private String specializationName;
    private Long hospitalId;
    private String hospitalName;
    private Long userId;
    private String userEmail;
    private String status;
    private int totalFeedbacks;    
    private double averageRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}