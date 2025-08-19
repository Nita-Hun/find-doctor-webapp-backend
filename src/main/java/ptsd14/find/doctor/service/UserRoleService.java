package ptsd14.find.doctor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ptsd14.find.doctor.dto.RoleDto;
import ptsd14.find.doctor.dto.RoleFormDto;
import ptsd14.find.doctor.mapper.UserRoleMapper;
import ptsd14.find.doctor.model.UserRole;
import ptsd14.find.doctor.repository.UserRoleRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserRoleMapper roleMapper;

    public Page<RoleDto> getAllRoles(Pageable pageable) {
        return userRoleRepository.findAll(pageable)
                .map(roleMapper::toDto);
    }

    public RoleDto createRole(RoleFormDto formDto) {
        UserRole role = roleMapper.toEntity(formDto);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        UserRole saved = userRoleRepository.save(role);
        return roleMapper.toDto(saved);
    }

    public RoleDto updateRole(Long id, RoleFormDto formDto) {
        UserRole existing = userRoleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        roleMapper.updateFromFormDto(formDto, existing);
        existing.setUpdatedAt(LocalDateTime.now());
        UserRole saved = userRoleRepository.save(existing);
        return roleMapper.toDto(saved);
    }

    public Optional<RoleDto> getRoleById(Long id) {
        return userRoleRepository.findById(id)
                .map(roleMapper::toDto);
    }

    public UserRole getRoleByName(String name) {
        return userRoleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found: " + name));
    }

    public void deleteRole(Long id) {
        if (!userRoleRepository.existsById(id)) {
            throw new RuntimeException("Role not found");
        }
        userRoleRepository.deleteById(id);
    }
}
