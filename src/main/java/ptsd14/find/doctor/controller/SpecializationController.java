package ptsd14.find.doctor.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import ptsd14.find.doctor.dto.SpecializationDto;
import ptsd14.find.doctor.service.SpecializationService;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/specializations")
@RequiredArgsConstructor
public class SpecializationController {

    private final SpecializationService specializationService;

    @GetMapping
    public ResponseEntity<Page<SpecializationDto>> getAll(
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search
    ) {
        int pageNumber = (page != null && page >= 0) ? page : 0;

        var pageable = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.DESC, "id"));
    
        Page<SpecializationDto> spcializationsPage = specializationService.getAll(pageable, search);

        return ResponseEntity.ok(spcializationsPage);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SpecializationDto> getById(@PathVariable Long id) {
        Optional<SpecializationDto> dto = specializationService.getById(id);
        return dto.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound(). build());
    }

    @GetMapping("/check-name")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> checkNameUnique(
            @RequestParam String name,
            @RequestParam(required = false) Long excludeId) {
        boolean isUnique = specializationService.isNameUnique(name, excludeId);
        return ResponseEntity.ok(Map.of("isUnique", isUnique));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpecializationDto> create(@RequestBody SpecializationDto dto) {
        SpecializationDto created = specializationService.create(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpecializationDto> update(
            @PathVariable Long id,
            @RequestBody SpecializationDto dto) {
        SpecializationDto updated = specializationService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        specializationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}