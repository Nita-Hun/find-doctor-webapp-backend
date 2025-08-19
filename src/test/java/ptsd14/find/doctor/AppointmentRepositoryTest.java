package ptsd14.find.doctor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;

import ptsd14.find.doctor.model.*;
import ptsd14.find.doctor.repository.AppointmentRepository;
import ptsd14.find.doctor.repository.AppointmentTypeRepository;
import ptsd14.find.doctor.repository.DoctorRepository;
import ptsd14.find.doctor.repository.PatientRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentTypeRepository appointmentTypeRepository;

    private Doctor doctor;
    private Patient patient;
    private AppointmentType appointmentType;

    @BeforeEach
    void setup() {
        
        doctor = new Doctor();
        doctor.setFirstname("nita");
        doctor.setLastname("hun");
       
        doctorRepository.save(doctor);

        patient = new Patient();
        patient.setFirstname("sok");
        patient.setLastname("leng");
        patient.setGender("female");
        
        patientRepository.save(patient);

        appointmentType = new AppointmentType();
        appointmentType.setName("Consultation");
        appointmentTypeRepository.save(appointmentType);

        
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentType(appointmentType);
        appointment.setDateTime(LocalDateTime.now().plusDays(1));
        appointment.setNote("Test appointment");
        appointmentRepository.save(appointment);
    }

    @Test
    void findByDoctorNameContainingIgnoreCase_shouldReturnAppointments() {
        var page = appointmentRepository.findByDoctorNameContainingIgnoreCase("nita", Pageable.unpaged());
        assertThat(page.getTotalElements()).isGreaterThan(0);
        assertThat(page.getContent().get(0).getDoctor().getFirstname()).isEqualToIgnoringCase("nita");
    }

    @Test
    void findByDateTimeAfterOrderByDateTimeAsc_shouldReturnFutureAppointments() {
        var appointments = appointmentRepository.findByDateTimeAfterOrderByDateTimeAsc(LocalDateTime.now());
        assertThat(appointments).isNotEmpty();
    }

    @Test
    void findByPatientFirstnameContainingIgnoreCaseOrPatientLastnameContainingIgnoreCase_shouldReturnAppointments() {
        var page = appointmentRepository.findByPatientFirstnameContainingIgnoreCaseOrPatientLastnameContainingIgnoreCase(
            "sok", "Sorin", Pageable.unpaged());
        assertThat(page.getTotalElements()).isGreaterThan(0);
    }

    @Test
    void countDistinctPatients_shouldReturnCorrectCount() {
        Long count = appointmentRepository.countDistinctPatients(doctor.getId());
        assertThat(count).isGreaterThanOrEqualTo(1L);
    }

    @Test
    void findRecentPatients_shouldReturnPatients() {
        List<Object[]> recentPatients = appointmentRepository.findRecentPatients(doctor.getId());
        assertThat(recentPatients).isNotEmpty();
    }
}

