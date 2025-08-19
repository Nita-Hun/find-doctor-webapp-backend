package ptsd14.find.doctor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ptsd14.find.doctor.dto.FeedbackDto;
import ptsd14.find.doctor.model.Feedback;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Page<FeedbackDto> findAllByAppointmentId(Long appointmentId, Pageable pageable);

    List<Feedback> findByAppointment_Doctor_Id(Long id);

    Page<Feedback> findByRatingAndCommentContainingIgnoreCase(Integer rating, String trim, Pageable pageable);

    Page<Feedback> findByRating(Integer rating, Pageable pageable);

    Page<Feedback> findByCommentContainingIgnoreCase(String trim, Pageable pageable);

    @Query("""
        SELECT AVG(f.rating)
        FROM Feedback f
        WHERE f.appointment.doctor.id = :doctorId
    """)
    Double findAverageRatingByDoctor(@Param("doctorId") Long doctorId);

    @Query("""
        SELECT COUNT(f)
        FROM Feedback f
        WHERE f.appointment.doctor.id = :doctorId
    """)
    Integer countRatingsByDoctor(@Param("doctorId") Long doctorId);

    boolean existsByAppointmentId(Long appointmentId);

    @Query("SELECT AVG(f.rating) FROM Feedback f")
    Double findAverageRatingAllDoctors();

    @Query("SELECT COUNT(f) FROM Feedback f")
    Integer countAllRatings();
}
