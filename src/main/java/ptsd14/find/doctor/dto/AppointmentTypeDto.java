package ptsd14.find.doctor.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AppointmentTypeDto {

    private Long id;
    private String name;
    private BigDecimal price;
    private Integer duration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
}
