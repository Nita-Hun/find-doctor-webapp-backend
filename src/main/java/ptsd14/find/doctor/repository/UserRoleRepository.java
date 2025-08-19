package ptsd14.find.doctor.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ptsd14.find.doctor.model.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByName(String name);
}

