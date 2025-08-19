package ptsd14.find.doctor.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import ptsd14.find.doctor.dto.HospitalDto;
import ptsd14.find.doctor.model.Hospital;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface HospitalMapper {

    // ENTITY -> DTO
    HospitalDto toDto(Hospital hospital);

    // DTO -> ENTITY
    @Mapping(target = "doctors", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Hospital toEntity(HospitalDto dto);

    // UPDATE EXISTING ENTITY FROM DTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "doctors", ignore = true)
    void updateFromDto(HospitalDto dto, @MappingTarget Hospital entity);
}
