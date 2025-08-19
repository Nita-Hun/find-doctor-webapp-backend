package ptsd14.find.doctor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import ptsd14.find.doctor.dto.AppointmentDto;
import ptsd14.find.doctor.exception.ResourceNotFoundException;
import ptsd14.find.doctor.mapper.AppointmentMapper;
import ptsd14.find.doctor.model.*;
import ptsd14.find.doctor.repository.*;
import ptsd14.find.doctor.service.AppointmentService;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private AppointmentMapper appointmentMapper;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private AppointmentTypeRepository appointmentTypeRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ShouldCreateAppointment_WhenValid() {
        AppointmentDto dto = new AppointmentDto();
        dto.setDateTime(LocalDateTime.now().plusDays(1));
        dto.setDoctorId(1L);
        dto.setPatientId(2L);
        dto.setAppointmentTypeId(3L);

        Appointment appointment = new Appointment();
        Doctor doctor = new Doctor();
        Patient patient = new Patient();
        AppointmentType appointmentType = new AppointmentType();
        Appointment savedAppointment = new Appointment();

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(2L)).thenReturn(Optional.of(patient));
        when(appointmentTypeRepository.findById(3L)).thenReturn(Optional.of(appointmentType));
        when(appointmentMapper.toEntity(dto)).thenReturn(appointment);
        when(appointmentRepository.save(appointment)).thenReturn(savedAppointment);
        when(appointmentMapper.toDto(savedAppointment)).thenReturn(dto);

        AppointmentDto result = appointmentService.create(dto);

        assertNotNull(result);
        verify(doctorRepository).findById(1L);
        verify(patientRepository).findById(2L);
        verify(appointmentTypeRepository).findById(3L);
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void create_ShouldThrowException_WhenDateTimeInPast() {
        AppointmentDto dto = new AppointmentDto();
        dto.setDateTime(LocalDateTime.now().minusDays(1));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            appointmentService.create(dto);
        });

        assertEquals("Appointment date and time must be in the future.", ex.getMessage());
    }

    @Test
    void getById_ShouldReturnDto_WhenFound() {
        Appointment appointment = new Appointment();
        AppointmentDto dto = new AppointmentDto();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toDto(appointment)).thenReturn(dto);

        Optional<AppointmentDto> result = appointmentService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(dto, result.get());
    }

    @Test
    void getById_ShouldReturnEmpty_WhenNotFound() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<AppointmentDto> result = appointmentService.getById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void update_ShouldUpdateAppointment_WhenFound() {
        Long id = 1L;
        AppointmentDto dto = new AppointmentDto();
        dto.setDateTime(LocalDateTime.now().plusDays(2));
        dto.setDoctorId(1L);
        dto.setPatientId(2L);
        dto.setAppointmentTypeId(3L);
        dto.setNote("Updated note");

        Appointment existing = new Appointment();
        Doctor doctor = new Doctor();
        Patient patient = new Patient();
        AppointmentType appointmentType = new AppointmentType();
        Appointment updated = new Appointment();

        when(appointmentRepository.findById(id)).thenReturn(Optional.of(existing));
        when(doctorRepository.findById(dto.getDoctorId())).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(dto.getPatientId())).thenReturn(Optional.of(patient));
        when(appointmentTypeRepository.findById(dto.getAppointmentTypeId())).thenReturn(Optional.of(appointmentType));
        when(appointmentRepository.save(existing)).thenReturn(updated);
        when(appointmentMapper.toDto(updated)).thenReturn(dto);

        AppointmentDto result = appointmentService.update(id, dto);

        assertNotNull(result);
        assertEquals(dto.getNote(), result.getNote());
        verify(appointmentRepository).save(existing);
    }

    @Test
    void update_ShouldThrowNotFound_WhenAppointmentNotFound() {
        Long id = 1L;
        AppointmentDto dto = new AppointmentDto();
        when(appointmentRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            appointmentService.update(id, dto);
        });

        assertEquals("Appointment not found", ex.getMessage());
    }

    @Test
    void delete_ShouldDelete_WhenExists() {
        Long id = 1L;
        when(appointmentRepository.existsById(id)).thenReturn(true);

        appointmentService.delete(id);

        verify(appointmentRepository).deleteById(id);
    }

    @Test
    void delete_ShouldThrowNotFound_WhenNotExists() {
        Long id = 1L;
        when(appointmentRepository.existsById(id)).thenReturn(false);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            appointmentService.delete(id);
        });

        assertEquals("Appointment not found", ex.getMessage());
    }

    @Test
    void updateStatus_ShouldUpdateStatus_WhenFound() {
        Long id = 1L;
        Appointment appointment = new Appointment();
        AppointmentDto dto = new AppointmentDto();

        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(appointmentMapper.toDto(appointment)).thenReturn(dto);

        AppointmentDto result = appointmentService.updateStatus(id, AppointmentStatus.COMPLETED);

        assertNotNull(result);
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void getAll_ShouldReturnPage_WhenNoSearch() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Appointment> page = new PageImpl<>(List.of(new Appointment()));
        when(appointmentRepository.findAll(pageable)).thenReturn(page);
        when(appointmentMapper.toDto(any())).thenReturn(new AppointmentDto());

        Page<AppointmentDto> result = appointmentService.getAll(pageable, null);

        assertEquals(1, result.getTotalElements());
        verify(appointmentRepository).findAll(pageable);
    }

    @Test
    void getAll_ShouldReturnPage_WhenSearchProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Appointment> page = new PageImpl<>(List.of(new Appointment()));
        when(appointmentRepository.findByDoctorNameContainingIgnoreCase("nita", pageable)).thenReturn(page);
        when(appointmentMapper.toDto(any())).thenReturn(new AppointmentDto());

        Page<AppointmentDto> result = appointmentService.getAll(pageable, "nita");

        assertEquals(1, result.getTotalElements());
        verify(appointmentRepository).findByDoctorNameContainingIgnoreCase("nita", pageable);
    }
}

