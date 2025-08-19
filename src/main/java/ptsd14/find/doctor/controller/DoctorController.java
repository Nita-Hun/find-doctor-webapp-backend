package ptsd14.find.doctor.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ptsd14.find.doctor.dto.DoctorDto;
import ptsd14.find.doctor.service.DoctorService;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@CrossOrigin
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<Page<DoctorDto>> getAllDoctors(
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String status
    ) {
        int pageNumber = (page != null && page >= 0) ? page : 0;

        var pageable = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<DoctorDto> doctorsPage = doctorService.getAll(pageable, search, status);

        return ResponseEntity.ok(doctorsPage);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorDto> getById(@PathVariable Long id) {
        return doctorService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorDto> create(@RequestBody DoctorDto dto) {
        return ResponseEntity.ok(doctorService.create(dto));
    }

    @PutMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAuthority('DOCTOR:edit')")
    public ResponseEntity<DoctorDto> update(@PathVariable Long id, @RequestBody DoctorDto dto) {
        return ResponseEntity.ok(doctorService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        doctorService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/top-rated")
    public ResponseEntity<List<DoctorDto>> getTopRatedDoctors(
            @RequestParam(defaultValue = "6") int limit
    ) {
        List<DoctorDto> topDoctors = doctorService.getTopRatedDoctors(limit);
        return ResponseEntity.ok(topDoctors);
    }


}
