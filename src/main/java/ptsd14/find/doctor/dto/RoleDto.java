package ptsd14.find.doctor.dto;


import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {
  private Long id;
  private String name;
  private String description;
  private String status;
  private Set<String> permissions;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}

