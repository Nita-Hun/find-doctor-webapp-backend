package ptsd14.find.doctor.dto;

import java.util.Set;

import lombok.Data;
@Data
public class RoleFormDto {
    private String name;
    private String description;
    private Set<String> permissions;
    private String status;
}


