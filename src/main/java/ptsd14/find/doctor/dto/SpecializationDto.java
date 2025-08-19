package ptsd14.find.doctor.dto;

import java.time.LocalDateTime;

import lombok.Data;


@Data
public class SpecializationDto {
    private Long id;
    private String name;
    private String iconUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
