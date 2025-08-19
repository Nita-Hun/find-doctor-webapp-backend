package ptsd14.find.doctor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ptsd14.find.doctor.dto.CreateUserRequest;
import ptsd14.find.doctor.dto.UpdateProfileRequest;
import ptsd14.find.doctor.dto.UpdateUserRequest;
import ptsd14.find.doctor.dto.UserDto;
import ptsd14.find.doctor.exception.ResourceNotFoundException;
import ptsd14.find.doctor.mapper.UserMapper;
import ptsd14.find.doctor.model.User;
import ptsd14.find.doctor.model.UserRole;
import ptsd14.find.doctor.repository.UserRepo;
import ptsd14.find.doctor.repository.UserRoleRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;

    @Transactional(readOnly = true)
    public Page<UserDto> getAll(Pageable pageable, String search, String roleName) {
        Page<User> users;

        boolean hasSearch = search != null && !search.trim().isEmpty();
        boolean hasRole = roleName != null && !roleName.trim().isEmpty();

        UserRole role = null;
        if (hasRole) {
            role = userRoleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        }

        if (hasRole && hasSearch) {
            users = userRepository.findByRoleAndEmailContainingIgnoreCase(role, search.trim(), pageable);
        } else if (hasRole) {
            users = userRepository.findByRole(role, pageable);
        } else if (hasSearch) {
            users = userRepository.findByEmailContainingIgnoreCase(search.trim(), pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        return users.map(userMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<UserDto> getById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto);
    }

    public UserDto createUser(CreateUserRequest request) {
    UserRole role = userRoleRepository.findById(request.getRoleId())
                     .orElseThrow(() -> new RuntimeException("Role not found"));

    User user = new User();
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(role);
    user.setProfilePhotoUrl(request.getProfilePhotoUrl());

    User savedUser = userRepository.save(user);
    return userMapper.toDto(savedUser);
}


    private void deleteProfilePhotoFileIfExists(String profilePhotoUrl) {
        if (profilePhotoUrl == null || profilePhotoUrl.isBlank() || profilePhotoUrl.equals("/uploads/default-profile.png")) {
            return;
        }

        String basePath = System.getProperty("user.dir");
        Path photoPath = Paths.get(basePath, profilePhotoUrl.replaceFirst("/", ""));

        try {
            Files.deleteIfExists(photoPath);
        } catch (Exception ex) {
            System.err.println("Failed to delete profile photo file: " + ex.getMessage());
        }
    }

    public UserDto updateUser(Long id, UpdateUserRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmail(req.getEmail());

        UserRole role = userRoleRepository.findById(req.getRoleId())
            .orElseThrow(() -> new RuntimeException("Role not found with id: " + req.getRoleId()));
        user.setRole(role);


        if (req.getProfilePhotoUrl() != null && !req.getProfilePhotoUrl().isBlank()) {
            String oldPhotoUrl = user.getProfilePhotoUrl();
            if (!oldPhotoUrl.equals(req.getProfilePhotoUrl())) {
                deleteProfilePhotoFileIfExists(oldPhotoUrl);
                user.setProfilePhotoUrl(req.getProfilePhotoUrl());
            }
        }

        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        User updated = userRepository.save(user);
        return userMapper.toDto(updated);
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        deleteProfilePhotoFileIfExists(user.getProfilePhotoUrl());
        userRepository.deleteById(id);
    }

    @Transactional
    public UserDto updateProfile(String currentEmail, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(currentEmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
    return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
}

}
