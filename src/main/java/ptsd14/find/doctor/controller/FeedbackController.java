package ptsd14.find.doctor.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ptsd14.find.doctor.dto.FeedbackDto;
import ptsd14.find.doctor.dto.FeedbackDto.FeedbackSummaryDto;
import ptsd14.find.doctor.service.DoctorService;
import ptsd14.find.doctor.service.FeedbackService;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
@CrossOrigin
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final DoctorService doctorService;

    @GetMapping
    // @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<Page<FeedbackDto>> getAll(
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) Integer rating
    ) {
        int pageNumber = (page != null && page >= 0) ? page : 0;

        var pageable = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<FeedbackDto> feedbacksPage = feedbackService.getAll(pageable, search, rating);

        return ResponseEntity.ok(feedbacksPage);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeedbackDto> getById(@PathVariable Long id) {
        return feedbackService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<FeedbackDto> create(@RequestBody FeedbackDto dto) {
        return ResponseEntity.ok(feedbackService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeedbackDto> update(@PathVariable Long id, @RequestBody FeedbackDto dto) {
        return ResponseEntity.ok(feedbackService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        feedbackService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/summary")
    public FeedbackSummaryDto getFeedbackSummary() {
        return doctorService.getFeedbackSummary();
    }


    
}
