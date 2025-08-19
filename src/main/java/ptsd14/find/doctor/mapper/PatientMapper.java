package ptsd14.find.doctor.mapper;

import org.mapstruct.*;
import ptsd14.find.doctor.dto.PatientDto;
import ptsd14.find.doctor.model.Patient;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PatientMapper {

    // ENTITY -> DTO
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    PatientDto toDto(Patient patient);

    // DTO -> ENTITY
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Patient toEntity(PatientDto dto);

    // UPDATE EXISTING ENTITY FROM DTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFromDto(PatientDto dto, @MappingTarget Patient entity);
}
