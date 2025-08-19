package ptsd14.find.doctor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptsd14.find.doctor.model.Hospital;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long>{

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    Page<Hospital> findByNameContainingIgnoreCase(String trimmedSearch, Pageable pageable);

    
}
