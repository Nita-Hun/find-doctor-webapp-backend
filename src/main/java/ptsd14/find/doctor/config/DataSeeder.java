package ptsd14.find.doctor.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ptsd14.find.doctor.model.User;
import ptsd14.find.doctor.model.UserRole;
import ptsd14.find.doctor.repository.UserRepo;
import ptsd14.find.doctor.repository.UserRoleRepository;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepo userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void seed() {

        createRoleIfNotExists("ADMIN");
        createRoleIfNotExists("DOCTOR");
        createRoleIfNotExists("PATIENT");

        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("adminpass"));
            admin.setRole(getRole("ADMIN"));
            userRepository.save(admin);

            User doctor = new User();
            doctor.setEmail("doctor@example.com");
            doctor.setPassword(passwordEncoder.encode("doctorpass"));
            doctor.setRole(getRole("DOCTOR"));
            userRepository.save(doctor);

            User patient = new User();
            patient.setEmail("patient@example.com");
            patient.setPassword(passwordEncoder.encode("patientpass"));
            patient.setRole(getRole("PATIENT"));
            userRepository.save(patient);
        }
    }

    private void createRoleIfNotExists(String name) {
        userRoleRepository.findByName(name).orElseGet(() -> {
            UserRole role = new UserRole();
            role.setName(name);
            role.setDescription(name + " role");
            return userRoleRepository.save(role);
        });
    }

    private UserRole getRole(String name) {
        return userRoleRepository.findByName(name)
            .orElseThrow(() -> new IllegalArgumentException("Role not found: " + name));
    }
}
