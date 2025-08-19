package ptsd14.find.doctor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ptsd14.find.doctor.dto.DoctorDashboardDto;
import ptsd14.find.doctor.exception.ResourceNotFoundException;
import ptsd14.find.doctor.model.Doctor;
import ptsd14.find.doctor.model.User;
import ptsd14.find.doctor.repository.DoctorRepository;
import ptsd14.find.doctor.repository.UserRepo;
import ptsd14.find.doctor.service.DoctorService;

@RestController
@RequestMapping("/api/doctor/dashboard")
@RequiredArgsConstructor
public class DoctorDashboardController {

    private final DoctorService doctorService;
    private final UserRepo userRepository;      
    private final DoctorRepository doctorRepository;

    @GetMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorDashboardDto> getMyDashboard(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Doctor doctor = doctorRepository.findByUser(user)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        DoctorDashboardDto dto = doctorService.getDashboard(doctor.getId());
        return ResponseEntity.ok(dto);
    }
}

