package ptsd14.find.doctor.mapper;

import org.mapstruct.*;
import ptsd14.find.doctor.dto.FeedbackDto;
import ptsd14.find.doctor.model.Appointment;
import ptsd14.find.doctor.model.Feedback;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface FeedbackMapper {

    // ENTITY -> DTO
    @Mapping(target = "appointmentId", source = "appointment.id")
    FeedbackDto toDto(Feedback feedback);

    // DTO -> ENTITY
    @Mapping(target = "appointment", expression = "java(mapAppointment(feedbackDto.getAppointmentId()))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Feedback toEntity(FeedbackDto feedbackDto);

    // UPDATE EXISTING ENTITY FROM DTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFromDto(FeedbackDto dto, @MappingTarget Feedback feedback);
    
    default Appointment mapAppointment(Long id) {
        if (id == null) return null;
        Appointment appointment = new Appointment();
        appointment.setId(id);
        return appointment;
    }
}
