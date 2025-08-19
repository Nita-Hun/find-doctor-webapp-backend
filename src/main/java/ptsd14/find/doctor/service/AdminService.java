package ptsd14.find.doctor.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ptsd14.find.doctor.model.User;
import ptsd14.find.doctor.model.UserRole;
import ptsd14.find.doctor.repository.UserRepo;
import ptsd14.find.doctor.repository.UserRoleRepository;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepo userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(String email, String rawPassword, String roleName) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        UserRole role = userRoleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        return userRepository.save(user);
    }

    public User updateProfilePhoto(Long userId, String photoUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setProfilePhotoUrl(photoUrl);
        return userRepository.save(user);
    }
}


