package ptsd14.find.doctor.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ptsd14.find.doctor.model.User;
import ptsd14.find.doctor.model.UserRole;

public interface UserRepo extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);

    Page<User> findByRoleAndEmailContainingIgnoreCase(UserRole role, String email, Pageable pageable);

    Page<User> findByRole(UserRole role, Pageable pageable);

    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    Page<User> findAll(Pageable pageable);
}
