package ptsd14.find.doctor.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String email;
    private String password;
}
