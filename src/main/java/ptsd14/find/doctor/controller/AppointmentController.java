package ptsd14.find.doctor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ptsd14.find.doctor.dto.AppointmentDto;
import ptsd14.find.doctor.model.AppointmentStatus;
import ptsd14.find.doctor.service.AppointmentService;
import ptsd14.find.doctor.service.UserService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@CrossOrigin
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserService userService;

    /**
     * ADMIN: List all appointments (with optional search).
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AppointmentDto>> getAllAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<AppointmentDto> appointmentsPage = appointmentService.getAll(pageable, search);
        return ResponseEntity.ok(appointmentsPage);
    }

     /**
     * PATIENT: List appointments history of who had booked appointment.
     */

    @GetMapping("/my/history")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Page<AppointmentDto>> getPatientHistoryAppointments(
            @RequestParam int page,
            @RequestParam int size,
            Authentication authentication
    ) {
        String email = authentication.getName();
        Long userId = userService.findByEmail(email).getId();
        Page<AppointmentDto> appointments = appointmentService.getAppointmentsForPatient(
                userId, PageRequest.of(page, size));
        return ResponseEntity.ok(appointments);
    }

    /**
     * ADMIN: Get a single appointment by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentDto> getById(@PathVariable Long id) {
        return appointmentService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * ADMIN or PATIENT: Create a new appointment.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<AppointmentDto> create(
            @RequestParam Long doctorId,
            @RequestParam Long patientId,
            @RequestParam Long appointmentTypeId,
            @RequestParam LocalDateTime dateTime,
            @RequestParam String note
    ) {
        AppointmentDto dto = new AppointmentDto();
        dto.setDoctorId(doctorId);
        dto.setPatientId(patientId);
        dto.setAppointmentTypeId(appointmentTypeId);
        dto.setDateTime(dateTime);
        dto.setNote(note);

        AppointmentDto created = appointmentService.create(dto);
        return ResponseEntity.ok(created);
    }

    /**
     * ADMIN: Update an appointment.
     */
    @PutMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppointmentDto> update(
            @PathVariable Long id,
            @RequestParam Long doctorId,
            @RequestParam Long patientId,
            @RequestParam Long appointmentTypeId,
            @RequestParam LocalDateTime dateTime,
            @RequestParam String note
    ) {
        AppointmentDto dto = new AppointmentDto();
        dto.setDoctorId(doctorId);
        dto.setPatientId(patientId);
        dto.setAppointmentTypeId(appointmentTypeId);
        dto.setDateTime(dateTime);
        dto.setNote(note);

        AppointmentDto updated = appointmentService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * ADMIN: Delete an appointment.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * DOCTOR: Mark appointment as confirmed.
     */
    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<AppointmentDto> confirm(@PathVariable Long id) {
        AppointmentDto updated = appointmentService.updateStatus(id, AppointmentStatus.CONFIRMED);
        return ResponseEntity.ok(updated);
    }

    /**
     * DOCTOR or PATIENT: Cancel appointment.
     */
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('PATIENT')")
    public ResponseEntity<AppointmentDto> cancel(@PathVariable Long id) {
        AppointmentDto updated = appointmentService.updateStatus(id, AppointmentStatus.CANCELED);
        return ResponseEntity.ok(updated);
    }

    /**
     * DOCTOR: Mark appointment as completed.
     */
    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<AppointmentDto> complete(@PathVariable Long id) {
        AppointmentDto updated = appointmentService.updateStatus(id, AppointmentStatus.COMPLETED);
        return ResponseEntity.ok(updated);
    }

    /**
     * DOCTOR: List appointments for the logged-in doctor.
     */
    @GetMapping("/doctor")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Page<AppointmentDto>> getDoctorAppointments(
            @RequestParam int page,
            @RequestParam int size,
            Authentication authentication
    ) {
        String email = authentication.getName();
        Long userId = userService.findByEmail(email).getId();
        Page<AppointmentDto> appointments = appointmentService.getAppointmentsForDoctor(
                userId, PageRequest.of(page, size));
        return ResponseEntity.ok(appointments);
    }
    /**
     * PATIENT: List appointments for the logged-in patient.
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Page<AppointmentDto>> getPatientAppointments(
            @RequestParam int page,
            @RequestParam int size,
            Authentication authentication
    ) {
        String email = authentication.getName();
        Long userId = userService.findByEmail(email).getId();
        Page<AppointmentDto> appointments = appointmentService.getAppointmentsForPatient(
                userId, PageRequest.of(page, size));
        return ResponseEntity.ok(appointments);
    }    

}
