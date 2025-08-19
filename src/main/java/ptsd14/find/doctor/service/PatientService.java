package ptsd14.find.doctor.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ptsd14.find.doctor.dto.PatientDto;
import ptsd14.find.doctor.exception.ResourceNotFoundException;
import ptsd14.find.doctor.mapper.PatientMapper;
import ptsd14.find.doctor.model.Patient;
import ptsd14.find.doctor.model.User;
import ptsd14.find.doctor.repository.PatientRepository;
import ptsd14.find.doctor.repository.UserRepo;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final UserRepo userRepository;

    @Transactional(readOnly = true)
    public Page<PatientDto> getAll(Pageable pageable, String search, String status) {
        Page<Patient> patients;

        boolean hasSearch = search != null && !search.trim().isEmpty();
        boolean hasStatus = status != null && !status.trim().isEmpty();

        if (hasStatus && hasSearch) {
    
            String trimmedSearch = search.trim();
            String trimmedStatus = status.trim();
            patients = patientRepository.findByStatusIgnoreCaseAndFirstnameContainingIgnoreCaseOrStatusIgnoreCaseAndLastnameContainingIgnoreCase(
                trimmedStatus, trimmedSearch,
                trimmedStatus, trimmedSearch,
                pageable
            );
        } else if (hasStatus) {
            
            patients = patientRepository.findByStatusIgnoreCase(status.trim(), pageable);
        } else if (hasSearch) {
            
            String trimmed = search.trim();
            patients = patientRepository.findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(
                trimmed,
                trimmed,
                pageable
            );
        } else {
            
            patients = patientRepository.findAll(pageable);
        }

        return patients.map(patientMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<PatientDto> getById(Long id) {
    return patientRepository.findWithUserById(id)
            .map(patientMapper::toDto);
    }
    
    @Transactional
    public PatientDto create(PatientDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
        
        Optional<Patient> existingPatient = patientRepository.findByUserId(user.getId());
        if (existingPatient.isPresent()) {
            throw new IllegalStateException("You already have a patient profile");
        }

        Patient patient = patientMapper.toEntity(dto);
        patient.setId(null);
        patient.setUser(user);

        if (patient.getStatus() == null || patient.getStatus().isEmpty()) {
            patient.setStatus("INACTIVE"); 
        }

        return patientMapper.toDto(patientRepository.save(patient));
    }

    @Transactional
    public PatientDto update(Long id, PatientDto dto) {
    Patient existing = patientRepository.findWithUserById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

    if (dto.getUserId() != null && dto.getUserId() > 0) {
        User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        existing.setUser(user);
    } else {
        existing.setUser(null);
    }

    patientMapper.updateFromDto(dto, existing);

    return patientMapper.toDto(patientRepository.save(existing));
    }

    public void delete(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found");
        }
        patientRepository.deleteById(id);
    }

    public PatientDto getPatientByUserEmail(String email) {
    Patient patient = patientRepository.findByUserEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("Patient not found for user email: " + email));
    return patientMapper.toDto(patient);
    }

}

