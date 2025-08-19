package ptsd14.find.doctor.mapper;

import org.mapstruct.*;
import ptsd14.find.doctor.dto.AppointmentTypeDto;
import ptsd14.find.doctor.model.AppointmentType;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AppointmentTypeMapper {

    AppointmentTypeDto toDto(AppointmentType entity);

    @Mapping(target = "id", ignore = true)  
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AppointmentType toEntity(AppointmentTypeDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFromDto(AppointmentTypeDto dto, @MappingTarget AppointmentType entity);
}
