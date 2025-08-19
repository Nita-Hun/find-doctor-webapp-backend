package ptsd14.find.doctor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateUserRequest {
    private String email;
    private String password;
    private Long roleId;
    private String profilePhotoUrl;
}

