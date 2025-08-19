package ptsd14.find.doctor.mapper;

import org.mapstruct.*;
import ptsd14.find.doctor.dto.PaymentDto;
import ptsd14.find.doctor.model.Payment;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentMapper {

    @Mapping(source = "appointment.id", target = "appointmentId")
    @Mapping(target = "patientName", expression = "java(payment.getAppointment().getPatient().getFirstname() + \" \" + payment.getAppointment().getPatient().getLastname())")
    PaymentDto toDto(Payment payment);

    @Mapping(target = "appointment", expression = "java(mapAppointment(paymentDto.getAppointmentId()))")
    Payment toEntity(PaymentDto paymentDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    void updateFromDto(PaymentDto dto, @MappingTarget Payment entity);

    default ptsd14.find.doctor.model.Appointment mapAppointment(Long id) {
        if (id == null) return null;
        ptsd14.find.doctor.model.Appointment appointment = new ptsd14.find.doctor.model.Appointment();
        appointment.setId(id);
        return appointment;
    }
}
