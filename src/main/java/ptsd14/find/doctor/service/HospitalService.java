package ptsd14.find.doctor.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ptsd14.find.doctor.dto.HospitalDto;
import ptsd14.find.doctor.exception.DuplicateResourceException;
import ptsd14.find.doctor.exception.ResourceNotFoundException;
import ptsd14.find.doctor.mapper.HospitalMapper;
import ptsd14.find.doctor.model.Hospital;
import ptsd14.find.doctor.repository.HospitalRepository;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final HospitalMapper hospitalMapper;

    public Optional<HospitalDto> getById(Long id) {
        return hospitalRepository.findById(id)
                .map(hospitalMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public Page<HospitalDto> getAll(Pageable pageable, String search) {
        Page<Hospital> hospitals;

        if (search != null && !search.trim().isEmpty()) {
            String trimmedSearch = search.trim();
            hospitals = hospitalRepository.findByNameContainingIgnoreCase(trimmedSearch, pageable);
        } else {
            hospitals = hospitalRepository.findAll(pageable);
        }

        return hospitals.map(hospitalMapper::toDto);
    }

    public HospitalDto create(HospitalDto dto) {
        validateNameUniqueness(dto.getName(), null);
        Hospital hospital = hospitalMapper.toEntity(dto);
        hospital.setId(null); 
        Hospital savedHospital = hospitalRepository.save(hospital);
        return hospitalMapper.toDto(savedHospital);
    }

    private void validateNameUniqueness(String name, Long excludeId) {
        if(!isNameUnique(name, excludeId)) { 
            throw new DuplicateResourceException(String.format("Hospital with name %s already exists", name));
        }
    }

    public boolean isNameUnique(String name, Long excludeId) {
        if(excludeId == null) {
            return !hospitalRepository.existsByName(name);  
        }
        return !hospitalRepository.existsByNameAndIdNot(name, excludeId);  
    }

    public HospitalDto update(Long id, HospitalDto dto) {
        Hospital existingHospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));

        if (dto.getName() != null && !dto.getName().equals(existingHospital.getName())) {
            validateNameUniqueness(dto.getName(), id);
            existingHospital.setName(dto.getName());
        }
        if (dto.getAddress() != null) {
            existingHospital.setAddress(dto.getAddress());
        }
        if (dto.getPhone() != null) {
            existingHospital.setPhone(dto.getPhone());
        }
        Hospital updatedHospital = hospitalRepository.save(existingHospital);
        return hospitalMapper.toDto(updatedHospital);
    }

    public void delete(Long id) {
        if (!hospitalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Hospital not found");
        }
        hospitalRepository.deleteById(id);
    }
}