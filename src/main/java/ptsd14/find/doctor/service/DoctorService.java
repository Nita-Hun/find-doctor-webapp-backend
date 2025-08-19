package ptsd14.find.doctor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ptsd14.find.doctor.dto.DoctorDashboardDto;
import ptsd14.find.doctor.dto.DoctorDto;
import ptsd14.find.doctor.dto.FeedbackDto.FeedbackSummaryDto;
import ptsd14.find.doctor.exception.ResourceNotFoundException;
import ptsd14.find.doctor.mapper.DoctorMapper;
import ptsd14.find.doctor.model.Doctor;
import ptsd14.find.doctor.model.Feedback;
import ptsd14.find.doctor.model.Hospital;
import ptsd14.find.doctor.model.Patient;
import ptsd14.find.doctor.model.Specialization;
import ptsd14.find.doctor.model.User;
import ptsd14.find.doctor.repository.AppointmentRepository;
import ptsd14.find.doctor.repository.DoctorRepository;
import ptsd14.find.doctor.repository.FeedbackRepository;
import ptsd14.find.doctor.repository.HospitalRepository;
import ptsd14.find.doctor.repository.SpecializationRepos;
import ptsd14.find.doctor.repository.UserRepo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final HospitalRepository hospitalRepository;
    private final SpecializationRepos specializationRepository;
    private final FeedbackRepository feedbackRepository;
    private final UserRepo userRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional(readOnly = true)
    public Page<DoctorDto> getAll(Pageable pageable, String search, String status) {
        Page<Doctor> doctors;

        boolean hasSearch = search != null && !search.trim().isEmpty();
        boolean hasStatus = status != null && !status.trim().isEmpty();

        if (hasStatus && hasSearch) {
            String trimmedSearch = search.trim();
            String trimmedStatus = status.trim();
            doctors = doctorRepository.findByStatusIgnoreCaseAndFirstnameContainingIgnoreCaseOrStatusIgnoreCaseAndLastnameContainingIgnoreCase(
                    trimmedStatus, trimmedSearch,
                    trimmedStatus, trimmedSearch,
                    pageable
            );
        } else if (hasStatus) {
            doctors = doctorRepository.findByStatusIgnoreCase(status.trim(), pageable);
        } else if (hasSearch) {
            String trimmed = search.trim();
            doctors = doctorRepository.findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(
                    trimmed,
                    trimmed,
                    pageable
            );
        } else {
            doctors = doctorRepository.findBy(pageable);
        }

        return doctors.map(doctorMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<DoctorDto> getById(Long id) {
        return doctorRepository.findWithDetailsById(id);
    }

     @Transactional(readOnly = true)
    public List<DoctorDto> getDoctorsWithFeedback() {
        return doctorRepository.findAll().stream()
                .filter(doctor -> !feedbackRepository.findByAppointment_Doctor_Id(doctor.getId()).isEmpty())
                .map(doctorMapper::toDto)
                .toList();
    }

    @Transactional
    public DoctorDto create(DoctorDto dto) {
        Doctor doctor = doctorMapper.toEntity(dto);

        // Set hospital
        Hospital hospital = hospitalRepository.findById(dto.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));

        // Set specialization
        Specialization specialization = specializationRepository.findById(dto.getSpecializationId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialization not found"));

        // Set user
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        doctor.setHospital(hospital);
        doctor.setSpecialization(specialization);
        doctor.setUser(user);

        Doctor saved = doctorRepository.save(doctor);
        return doctorMapper.toDto(saved);
    }
    @Transactional
    public DoctorDto update(Long id, DoctorDto dto) {
        Doctor existing = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        existing.setFirstname(dto.getFirstname());
        existing.setLastname(dto.getLastname());
        existing.setStatus(dto.getStatus());

        // Update hospital
        Hospital hospital = hospitalRepository.findById(dto.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));

        // Update specialization
        Specialization specialization = specializationRepository.findById(dto.getSpecializationId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialization not found"));

        // Update user
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        existing.setHospital(hospital);
        existing.setSpecialization(specialization);
        existing.setUser(user);

        Doctor updated = doctorRepository.save(existing);
        return doctorMapper.toDto(updated);
    }

    public void delete(Long id) {
        doctorRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public DoctorDashboardDto getDashboard(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        Long totalPatients = appointmentRepository.countDistinctPatients(doctorId);
        Long consultations = appointmentRepository.countConsultations(doctorId, "Consultation%");
        Double avgRating = feedbackRepository.findAverageRatingByDoctor(doctorId);
        if (avgRating == null) avgRating = 0.0;

        Integer ratingCount = feedbackRepository.countRatingsByDoctor(doctorId);
        if (ratingCount == null) ratingCount = 0;

        DoctorDashboardDto.DoctorInfo doctorInfo = DoctorDashboardDto.DoctorInfo.builder()
            .id(doctor.getId())
            .name(doctor.getFirstname() + " " + doctor.getLastname())
            .specialization(doctor.getSpecialization().getName())
            .rating(avgRating)
            .ratingCount(ratingCount)
            .build();

        // Recent patients (last 5 visits)
        List<Object[]> recentRaw = appointmentRepository.findRecentPatients(doctorId);
        List<DoctorDashboardDto.RecentPatient> recentPatients = recentRaw.stream()
            .limit(5)
            .map(row -> {
                Patient p = (Patient) row[0];
                LocalDateTime lastVisit = (LocalDateTime) row[1];
                String fullName = p.getFirstname() + " " + p.getLastname();
                return DoctorDashboardDto.RecentPatient.builder()
                    .id(p.getId())
                    .name(fullName)
                    .age(calculateAge(p.getDateOfBirth()))
                    .date(lastVisit.toLocalDate().toString())
                    .build();
            })
            .toList();

        // Demographics for this week
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDateTime startDateTime = startOfWeek.atStartOfDay();
        LocalDateTime endDateTime = endOfWeek.atTime(23, 59, 59);

        List<Patient> patientsThisWeek = appointmentRepository.findPatientsByDoctorAndDateRange(doctorId, startDateTime, endDateTime);

        long maleCount = patientsThisWeek.stream()
            .filter(p -> "male".equalsIgnoreCase(p.getGender()))
            .count();

        long femaleCount = patientsThisWeek.stream()
            .filter(p -> "female".equalsIgnoreCase(p.getGender()))
            .count();

        long otherCount = patientsThisWeek.size() - maleCount - femaleCount;

        DoctorDashboardDto.ChartData chartData = DoctorDashboardDto.ChartData.builder()
            .maleCount(maleCount)
            .femaleCount(femaleCount)
            .otherCount(otherCount)
            .build();

        return DoctorDashboardDto.builder()
            .totalPatients(totalPatients)
            .consultations(consultations)
            .doctor(doctorInfo)
            .recentPatients(recentPatients)
            .chart(chartData)
            .build();
    }

    private int calculateAge(LocalDate dob) {
        return dob != null ? Period.between(dob, LocalDate.now()).getYears() : 0;
    }

    @Transactional(readOnly = true)
    public List<DoctorDto> getTopRatedDoctors(int limit) {
    return doctorRepository.findAll().stream()
        .map(doctor -> {
            List<Feedback> feedbacks = feedbackRepository.findByAppointment_Doctor_Id(doctor.getId());
            double avgRating = feedbacks.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);
            DoctorDto dto = doctorMapper.toDto(doctor);
            dto.setTotalFeedbacks(feedbacks.size());
            dto.setAverageRating(avgRating);
            return dto;
        })
        .filter(d -> d.getTotalFeedbacks() > 0)
        .sorted((d1, d2) -> Double.compare(d2.getAverageRating(), d1.getAverageRating()))
        .limit(limit)
        .toList();
    }

    @Transactional(readOnly = true)
    public FeedbackSummaryDto getFeedbackSummary() {
    Double avgRating = feedbackRepository.findAverageRatingAllDoctors();
    if (avgRating == null) avgRating = 0.0;

    Integer ratingCount = feedbackRepository.countAllRatings();
    if (ratingCount == null) ratingCount = 0;

    return new FeedbackSummaryDto(avgRating, ratingCount);
}

}
