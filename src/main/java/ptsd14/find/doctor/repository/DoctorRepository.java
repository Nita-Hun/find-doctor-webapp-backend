package ptsd14.find.doctor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ptsd14.find.doctor.dto.DoctorDto;
import ptsd14.find.doctor.model.Doctor;
import ptsd14.find.doctor.model.User;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @EntityGraph(attributePaths = {"hospital", "specialization", "user"})
    Page<Doctor> findBy(Pageable pageable);
    Page<Doctor> findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(
        String firstname,
        String lastname,
        Pageable pageable
    );
    Page<Doctor> findByStatusIgnoreCaseAndFirstnameContainingIgnoreCaseOrStatusIgnoreCaseAndLastnameContainingIgnoreCase(
            String trimmedStatus, String trimmedSearch, String trimmedStatus2, String trimmedSearch2,
            Pageable pageable);
    Page<Doctor> findByStatusIgnoreCase(String trim, Pageable pageable);

    @EntityGraph(attributePaths = {"hospital", "specialization", "user"})
    Optional<DoctorDto> findWithDetailsById(Long id);
    
    List<Doctor> findBySpecializationIdAndIdNot(Long specializationId, Long doctorId);
    
    Optional<Doctor> findByUser(User user);



}

