package ptsd14.find.doctor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import ptsd14.find.doctor.model.User;
import ptsd14.find.doctor.model.UserRole;
import ptsd14.find.doctor.repository.UserRepo;
import ptsd14.find.doctor.repository.UserRoleRepository;
import ptsd14.find.doctor.service.AdminService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private UserRoleRepository userRoleRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_shouldCreateUserIfNotExists() {
        String email = "test@example.com";
        String rawPassword = "password";
        String encodedPassword = "encoded123";
        String roleName = "ADMIN";

        UserRole role = new UserRole();
        role.setName(roleName);

        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());
        when(userRoleRepo.findByName(roleName)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepo.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = adminService.createUser(email, rawPassword, roleName);

        assertEquals(email, result.getEmail());
        assertEquals(encodedPassword, result.getPassword());
        assertEquals(role, result.getRole());
    }

    @Test
    void createUser_shouldThrowIfEmailExists() {
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(RuntimeException.class, () ->
            adminService.createUser("test@example.com", "password", "ADMIN")
        );
    }

    @Test
    void createUser_shouldThrowIfRoleNotFound() {
        when(userRepo.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRoleRepo.findByName("ADMIN")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            adminService.createUser("new@example.com", "pass", "ADMIN")
        );
    }

    @Test
    void updateProfilePhoto_shouldUpdateIfUserExists() {
        Long userId = 1L;
        String newPhotoUrl = "http://photo.url/image.jpg";

        User user = new User();
        user.setId(userId);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = adminService.updateProfilePhoto(userId, newPhotoUrl);

        assertEquals(newPhotoUrl, result.getProfilePhotoUrl());
    }

    @Test
    void updateProfilePhoto_shouldThrowIfUserNotFound() {
        when(userRepo.findById(100L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            adminService.updateProfilePhoto(100L, "http://photo.url")
        );
    }
}

