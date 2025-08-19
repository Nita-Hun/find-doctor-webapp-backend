package ptsd14.find.doctor.mapper;

import org.mapstruct.*;
import ptsd14.find.doctor.dto.RoleDto;
import ptsd14.find.doctor.dto.RoleFormDto;
import ptsd14.find.doctor.model.UserRole;

@Mapper(componentModel = "spring")
public interface UserRoleMapper {

  // Map entity to DTO (list)
  RoleDto toDto(UserRole role);

  // Map DTO to entity for update/create (full permissions)
  UserRole toEntity(RoleFormDto formDto);

  // Update entity from form DTO (for PATCH/PUT)
  void updateFromFormDto(RoleFormDto formDto, @MappingTarget UserRole role);
}

