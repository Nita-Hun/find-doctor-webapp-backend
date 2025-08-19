package ptsd14.find.doctor.mapper;

import org.mapstruct.*;
import ptsd14.find.doctor.dto.DoctorDto;
import ptsd14.find.doctor.model.Doctor;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DoctorMapper {

    // ENTITY -> DTO
    @Mapping(target = "specializationId", source = "specialization.id")
    @Mapping(target = "specializationName", source = "specialization.name")
    @Mapping(target = "hospitalId", source = "hospital.id")
    @Mapping(target = "hospitalName", source = "hospital.name")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    DoctorDto toDto(Doctor doctor);

    // DTO -> ENTITY
    @Mapping(target = "specialization", ignore = true)
    @Mapping(target = "hospital", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Doctor toEntity(DoctorDto doctorDto);

    // UPDATE EXISTING ENTITY FROM DTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "specialization", ignore = true)
    @Mapping(target = "hospital", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFromDto(DoctorDto dto, @MappingTarget Doctor entity);
}
