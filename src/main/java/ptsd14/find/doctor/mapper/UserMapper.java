package ptsd14.find.doctor.mapper;

import org.mapstruct.*;
import ptsd14.find.doctor.dto.UserDto;
import ptsd14.find.doctor.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roleId", expression = "java(user.getRole() != null ? user.getRole().getId() : null)")
    @Mapping(target = "role", expression = "java(user.getRole() != null ? user.getRole().getName() : null)")
    @Mapping(target = "profilePhotoUrl", source = "profilePhotoUrl")
    UserDto toDto(User user);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "profilePhotoUrl", source = "profilePhotoUrl")
    User toEntity(UserDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "profilePhotoUrl", source = "profilePhotoUrl")
    void updateFromDto(UserDto dto, @MappingTarget User user);
}

