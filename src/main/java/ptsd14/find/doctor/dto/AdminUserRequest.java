package ptsd14.find.doctor.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminUserRequest {
    @NotNull
    private String roleName;
    
    private boolean enabled;
}

