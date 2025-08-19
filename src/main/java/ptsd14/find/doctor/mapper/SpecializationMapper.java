package ptsd14.find.doctor.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ptsd14.find.doctor.dto.SpecializationDto;
import ptsd14.find.doctor.model.Specialization;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
        )
public interface SpecializationMapper {
    SpecializationDto toDto(Specialization entity);
    Specialization toEntity(SpecializationDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFromDto(SpecializationDto dto, @MappingTarget Specialization entity);

}
