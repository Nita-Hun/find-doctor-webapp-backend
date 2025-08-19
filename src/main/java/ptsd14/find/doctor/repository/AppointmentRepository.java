package ptsd14.find.doctor.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Streamable;

import ptsd14.find.doctor.dto.AppointmentDto;
import ptsd14.find.doctor.model.Appointment;
import ptsd14.find.doctor.model.AppointmentStatus;
import ptsd14.find.doctor.model.Patient;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Streamable<AppointmentDto> findByDateTimeAfterOrderByDateTimeAsc(LocalDateTime now);

    List<Appointment> findByPaymentIsNull();

    @Query("""
        SELECT a FROM Appointment a
        JOIN a.doctor d
        WHERE (:search IS NULL OR :search = '' OR
               LOWER(d.firstname) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(d.lastname) LIKE LOWER(CONCAT('%', :search, '%')))
        """)
    Page<Appointment> findByDoctorNameContainingIgnoreCase(@Param("search") String search, Pageable pageable);

    Page<Appointment> findByPatientFirstnameContainingIgnoreCaseOrPatientLastnameContainingIgnoreCase(
            String search, String search2, Pageable pageable);

    List<Appointment> findByDateTimeBetweenOrderByDateTimeAsc(LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Appointment> findTop5ByOrderByDateTimeDesc();

    @Query("""
        SELECT a
        FROM Appointment a
        JOIN FETCH a.patient
        JOIN FETCH a.doctor
        JOIN FETCH a.appointmentType
        WHERE a.dateTime > :now
        ORDER BY a.dateTime ASC
    """)
    List<Appointment> findTop5Upcoming(@Param("now") LocalDateTime now);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.user.id = :doctorUserId")
    Page<Appointment> findByDoctorUserId(@Param("doctorUserId") Long doctorUserId, Pageable pageable);

    @Query("""
        SELECT COUNT(DISTINCT a.patient.id)
        FROM Appointment a
        WHERE a.doctor.id = :doctorId
    """)
    Long countDistinctPatients(@Param("doctorId") Long doctorId);

    @Query("""
    SELECT COUNT(a)
    FROM Appointment a
    WHERE a.doctor.id = :doctorId
      AND a.appointmentType.name LIKE :typeName
    """)
Long countConsultations(@Param("doctorId") Long doctorId, @Param("typeName") String typeName);


    @Query("""
    SELECT FUNCTION('DATE_FORMAT', a.dateTime, '%Y-%m') AS month,
        SUM(CASE WHEN LOWER(a.patient.gender) = 'male' THEN 1 ELSE 0 END),
        SUM(CASE WHEN LOWER(a.patient.gender) = 'female' THEN 1 ELSE 0 END),
        SUM(CASE WHEN LOWER(a.patient.gender) NOT IN ('male', 'female') THEN 1 ELSE 0 END)
    FROM Appointment a
    WHERE a.doctor.id = :doctorId
    AND a.dateTime >= :startDate
    GROUP BY FUNCTION('DATE_FORMAT', a.dateTime, '%Y-%m')
    ORDER BY month
    """)
    List<Object[]> findMonthlyGenderCounts(@Param("doctorId") Long doctorId, @Param("startDate") LocalDate startDate);


    @Query("""
    SELECT a.patient, MAX(a.dateTime)
    FROM Appointment a
    WHERE a.doctor.id = :doctorId
    GROUP BY a.patient
    ORDER BY MAX(a.dateTime) DESC
    """)
    List<Object[]> findRecentPatients(@Param("doctorId") Long doctorId);


    @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.doctor.id = :doctorId AND a.dateTime BETWEEN :startDate AND :endDate")
    List<Patient> findPatientsByDoctorAndDateRange(@Param("doctorId") Long doctorId,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);


    List<Appointment> findByPatientId(Long patientId);

    Page<Appointment> findByPatientUserId(Long userId, Pageable pageable);

    Page<Appointment> findByPatientIdAndStatus(Long patientId, AppointmentStatus status, Pageable pageable);

}
