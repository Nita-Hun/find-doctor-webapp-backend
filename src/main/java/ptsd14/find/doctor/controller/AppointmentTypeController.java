package ptsd14.find.doctor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ptsd14.find.doctor.dto.AppointmentTypeDto;
import ptsd14.find.doctor.service.AppointmentTypeService;
import java.util.Map;

@RestController
@RequestMapping("/api/appointment-types")
@RequiredArgsConstructor
public class AppointmentTypeController {

    private final AppointmentTypeService appointmentTypeService;

    /**
     * ADMIN: List all appointment types (with optional search).
     * PATIENT: List all appointment types (drop down in form book appointment).
     */
    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<Page<AppointmentTypeDto>> getAllTypes(
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search
    ) {
        int pageNumber = (page != null && page >= 0) ? page : 0;

        var pageable = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<AppointmentTypeDto> appointmentTypesPage = appointmentTypeService.getAll(pageable, search);

        return ResponseEntity.ok(appointmentTypesPage);
    }

    /**
     * ADMIN: Get a single appointment type by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppointmentTypeDto> getById(@PathVariable Long id) {
        return appointmentTypeService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * ADMIN: check Unique appointment type name.
     */
    @GetMapping("/check-name")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> checkNameUnique(
        @RequestParam String name,
        @RequestParam(required = false) Long excludeId
    ) {
        boolean isUnique = appointmentTypeService.isNameUnique(name, excludeId);
        return ResponseEntity.ok(Map.of("isUnique", isUnique));
    }

    /**
     * ADMIN: Create a new appointment type.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppointmentTypeDto> create(@RequestBody AppointmentTypeDto dto) {
        AppointmentTypeDto created = appointmentTypeService.create(dto);
        return ResponseEntity.ok(created);
    }
    /**
     * ADMIN: Update appointment type.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppointmentTypeDto> update(@PathVariable Long id, @RequestBody AppointmentTypeDto dto) {
        AppointmentTypeDto updated = appointmentTypeService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * ADMIN: Delete a new appointment type.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appointmentTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
