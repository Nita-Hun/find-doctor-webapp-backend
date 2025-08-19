package ptsd14.find.doctor.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import ptsd14.find.doctor.dto.HospitalDto;
import ptsd14.find.doctor.service.HospitalService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<HospitalDto>> getAll(
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search
    ) {
        int pageNumber = (page != null && page >= 0) ? page : 0;

        var pageable = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<HospitalDto> hospitalsPage = hospitalService.getAll(pageable, search);

        return ResponseEntity.ok(hospitalsPage);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HospitalDto> getById(@PathVariable Long id) {
        Optional<HospitalDto> dto = hospitalService.getById(id);
        return dto.map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/check-name")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> checkNameUnique(
            @RequestParam String name,
            @RequestParam(required = false) Long excludeId) {
        boolean isUnique = hospitalService.isNameUnique(name, excludeId);
        return ResponseEntity.ok(Map.of("isUnique", isUnique));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HospitalDto> create(@RequestBody HospitalDto dto) {
        HospitalDto created = hospitalService.create(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HospitalDto> update(
            @PathVariable Long id,
            @RequestBody HospitalDto dto) {
        HospitalDto updated = hospitalService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteHospital(@PathVariable Long id) {
        hospitalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}