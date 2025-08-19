package ptsd14.find.doctor.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ptsd14.find.doctor.model.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long>{

    @EntityGraph(attributePaths = {"user"})
    Page<Patient> findByStatusIgnoreCaseAndFirstnameContainingIgnoreCaseOrStatusIgnoreCaseAndLastnameContainingIgnoreCase(
            String trimmedStatus, String trimmedSearch, String trimmedStatus2, String trimmedSearch2,
            Pageable pageable);
    @EntityGraph(attributePaths = {"user"})
    Page<Patient> findByStatusIgnoreCase(String trim, Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<Patient> findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(String trimmed, String trimmed2,
            Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<Patient> findBy(Pageable pageable);
    
    @EntityGraph(attributePaths = "user")
    Optional<Patient> findWithUserById(Long id);


    @EntityGraph(attributePaths = {"user"})
    Optional<Patient> findByUserEmail(String email);
    
    Optional<Patient> findByUserId(Long id);

}
