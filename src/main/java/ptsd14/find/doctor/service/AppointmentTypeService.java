package ptsd14.find.doctor.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ptsd14.find.doctor.dto.AppointmentTypeDto;
import ptsd14.find.doctor.exception.DuplicateResourceException;
import ptsd14.find.doctor.exception.ResourceNotFoundException;
import ptsd14.find.doctor.mapper.AppointmentTypeMapper;
import ptsd14.find.doctor.model.AppointmentType;
import ptsd14.find.doctor.repository.AppointmentTypeRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentTypeService {

    private final AppointmentTypeRepository appointmentTypeRepository;
    private final AppointmentTypeMapper appointmentTypeMapper;

    @Transactional(readOnly = true)
    public Page<AppointmentTypeDto> getAll(Pageable pageable, String search) {
        Page<AppointmentType> types;

        if (search != null && !search.trim().isEmpty()) {
            String trimmedSearch = search.trim();
            types = appointmentTypeRepository.findByNameContainingIgnoreCase(trimmedSearch, pageable);
        } else {
            types = appointmentTypeRepository.findAll(pageable);
        }

        return types.map(appointmentTypeMapper::toDto);
    }

    public Optional<AppointmentTypeDto> getById(Long id) {
        return appointmentTypeRepository.findById(id)
                .map(appointmentTypeMapper::toDto);
    }

    public AppointmentTypeDto create(AppointmentTypeDto dto) {
        validateNameUniqueness(dto.getName(), null);

        AppointmentType appointmentType = appointmentTypeMapper.toEntity(dto);
        appointmentType.setId(null);
        AppointmentType saved = appointmentTypeRepository.save(appointmentType);
        return appointmentTypeMapper.toDto(saved);
    }

    public AppointmentTypeDto update(Long id, AppointmentTypeDto dto) {
        AppointmentType existing = appointmentTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AppointmentType not found"));
                
        if (dto.getName() != null && !dto.getName().equals(existing.getName())) {
            validateNameUniqueness(dto.getName(), id);
            existing.setName(dto.getName());
        }

        if (dto.getPrice() != null) {
            existing.setPrice(dto.getPrice());
        }
        if (dto.getDuration() != null) {
            existing.setDuration(dto.getDuration());
        }

        AppointmentType updated = appointmentTypeRepository.save(existing);
        return appointmentTypeMapper.toDto(updated);
    }

    public void delete(Long id) {
        if (!appointmentTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("AppointmentType not found");
        }
        appointmentTypeRepository.deleteById(id);
    }

    public boolean isNameUnique(String name, Long excludeId) {
        if (excludeId == null) {
            return !appointmentTypeRepository.existsByName(name);
        }
        return !appointmentTypeRepository.existsByNameAndIdNot(name, excludeId);
    }

    private void validateNameUniqueness(String name, Long excludeId) {
        if (!isNameUnique(name, excludeId)) {
            throw new DuplicateResourceException(
                String.format("Appointment type with name '%s' already exists", name)
            );
        }
    }

    @Transactional(readOnly = true)
    public List<AppointmentTypeDto> getAllSortedByName() {
    List<AppointmentType> types = appointmentTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    return types.stream()
                .map(appointmentTypeMapper::toDto)
                .toList();
}

}
