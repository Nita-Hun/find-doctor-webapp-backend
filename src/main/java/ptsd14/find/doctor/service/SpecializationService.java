package ptsd14.find.doctor.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ptsd14.find.doctor.dto.SpecializationDto;
import ptsd14.find.doctor.exception.DuplicateResourceException;
import ptsd14.find.doctor.exception.ResourceNotFoundException;
import ptsd14.find.doctor.mapper.SpecializationMapper;
import ptsd14.find.doctor.model.Specialization;
import ptsd14.find.doctor.repository.SpecializationRepos;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpecializationService {

    private final SpecializationRepos specializationRepository;
    private final SpecializationMapper specializationMapper;

    @Transactional(readOnly = true)
    public Page<SpecializationDto> getAll(Pageable pageable, String search) {
        Page<Specialization> specs;

        if (search != null && !search.trim().isEmpty()) {
            String trimmedSearch = search.trim();
            specs = specializationRepository.findByNameContainingIgnoreCase(trimmedSearch, pageable);
        } else {
            specs = specializationRepository.findAll(pageable);
        }

        return specs.map(specializationMapper::toDto);
    }

    public Optional<SpecializationDto> getById(Long id) {
        return specializationRepository.findById(id)
                .map(specializationMapper::toDto);
        
    }

    public SpecializationDto create(SpecializationDto dto) {
        validateNameUniqueness(dto.getName(), null);

        Specialization specialization = specializationMapper.toEntity(dto);
        specialization.setId(null); 

        if (dto.getIconUrl() != null) {
        specialization.setIconUrl(dto.getIconUrl());
    }
        Specialization saved = specializationRepository.save(specialization);
        return specializationMapper.toDto(saved);
    }

    private void validateNameUniqueness(String name, Long excludeId) {
    if (!isNameUnique(name, excludeId)) {
        throw new DuplicateResourceException(String.format("%s already exists", name));
    }
}

    public boolean isNameUnique(String name, Long excludeId) {
       if(excludeId == null) {
           return !specializationRepository.existsByName(name);
       } else {
           return !specializationRepository.existsByNameAndIdNot(name, excludeId);
       }
    }
    public SpecializationDto update(Long id, SpecializationDto dto) {
        Specialization existing = specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialization not found"));

        if (dto.getName() != null && !dto.getName().equals(existing.getName())) {
            validateNameUniqueness(dto.getName(), id);
            existing.setName(dto.getName());
        }

        if (dto.getIconUrl() != null) {
        existing.setIconUrl(dto.getIconUrl());
    }

        Specialization updatedSpecialization = specializationRepository.save(existing);
        return specializationMapper.toDto(updatedSpecialization);
    }

    public void delete(Long id) {
        if (!specializationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Specialization not found");
        }
        specializationRepository.deleteById(id);
    }
}