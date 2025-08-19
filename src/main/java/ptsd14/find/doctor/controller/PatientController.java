package ptsd14.find.doctor.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import ptsd14.find.doctor.dto.PatientDto;
import ptsd14.find.doctor.service.PatientService;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@CrossOrigin
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PatientDto>> getAllPatients(
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String status
    ) {
        int pageNumber = (page != null && page >= 0) ? page : 0;

        var pageable = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<PatientDto> patientsPage = patientService.getAll(pageable, search, status);

        return ResponseEntity.ok(patientsPage);
    }
    @GetMapping("/my")
    public ResponseEntity<PatientDto> getMyPatientInfo(Authentication authentication) {
    String email = authentication.getName(); // or use another identifier based on your auth
    PatientDto patient = patientService.getPatientByUserEmail(email);
    return ResponseEntity.ok(patient);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PatientDto> getById(@PathVariable Long id) {
        return patientService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<PatientDto> createPatient(@RequestBody PatientDto dto) {
        PatientDto createdPatient = patientService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PatientDto> update(@PathVariable Long id, @RequestBody PatientDto dto) {
        PatientDto updatedPatient = patientService.update(id, dto);
        return ResponseEntity.ok(updatedPatient);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        patientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    

}
