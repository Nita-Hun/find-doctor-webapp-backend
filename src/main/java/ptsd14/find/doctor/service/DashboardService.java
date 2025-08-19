package ptsd14.find.doctor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ptsd14.find.doctor.dto.DashboardStatsDto;
import ptsd14.find.doctor.model.Appointment;
import ptsd14.find.doctor.repository.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final SpecializationRepos specializationRepository;
    private final PaymentRepository paymentRepository;

    public DashboardStatsDto getDashboardStats() {
    DashboardStatsDto dto = new DashboardStatsDto();

    dto.setDoctorCount(doctorRepository.count());
    dto.setPatientCount(patientRepository.count());
    dto.setAppointmentCount(appointmentRepository.count());
    dto.setSpecializationCount(specializationRepository.count());

    BigDecimal totalRevenue = paymentRepository.sumTotalRevenue();
    dto.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

    List<DashboardStatsDto.DailyRevenue> dailyRevenue = paymentRepository.findRevenueLast30Days(LocalDateTime.now().minusDays(30))
        .stream()
        .map(record -> {
            DashboardStatsDto.DailyRevenue r = new DashboardStatsDto.DailyRevenue();
            r.setDate(((java.sql.Date) record[0]).toLocalDate()); 
            r.setRevenue((BigDecimal) record[1]);
            return r;
        })
        .collect(Collectors.toList());

    dto.setRevenueLast30Days(dailyRevenue);
    dto.setUpcomingAppointments(getUpcomingAppointments());

    return dto;
}


    public List<DashboardStatsDto.AppointmentSummary> getUpcomingAppointments() {
    List<Appointment> upcoming = appointmentRepository.findTop5Upcoming(LocalDateTime.now());
    return upcoming.stream()
        .map(a -> {
        DashboardStatsDto.AppointmentSummary s = new DashboardStatsDto.AppointmentSummary();
        s.setId(a.getId());
        s.setPatientName(a.getPatient().getFirstname() + " " + a.getPatient().getLastname());
        s.setDoctorName(a.getDoctor().getFirstname() + " " + a.getDoctor().getLastname());
        s.setTypeName(a.getAppointmentType().getName());
        s.setNote(a.getNote());
        s.setDateTime(a.getDateTime());
        return s;
        })
        .collect(Collectors.toList());
    }

    @Transactional
    public List<DashboardStatsDto.AppointmentSummary> getWeeklyAppointments() {

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        LocalDateTime startDateTime = startOfWeek.atStartOfDay();
        LocalDateTime endDateTime = endOfWeek.atTime(LocalTime.MAX);

        List<Appointment> weeklyAppointments = appointmentRepository
                .findByDateTimeBetweenOrderByDateTimeAsc(startDateTime, endDateTime);

        return weeklyAppointments.stream()
                .map(a -> {
                    DashboardStatsDto.AppointmentSummary s = new DashboardStatsDto.AppointmentSummary();
                    s.setId(a.getId());
                    s.setPatientName(a.getPatient().getFirstname() + " " + a.getPatient().getLastname());
                    s.setDoctorName(a.getDoctor().getFirstname() + " " + a.getDoctor().getLastname());
                    s.setTypeName(a.getAppointmentType().getName());
                    s.setNote(a.getNote());
                    s.setDateTime(a.getDateTime());
                    return s;
                })
                .collect(Collectors.toList());
    }
    @Transactional
    public List<Map<String, Object>> getMonthlyRevenue() {
        List<Object[]> rawData = paymentRepository.findMonthlyRevenue();

        return rawData.stream()
        .map(record -> {
            Integer monthNumber = (Integer) record[0];
            BigDecimal revenue = (BigDecimal) record[1];
            String monthName = Month.of(monthNumber).getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH);
            return Map.<String, Object>of("month", monthName, "revenue", revenue);
        })
        .collect(Collectors.toList());
    }
    @Transactional
    public List<Map<String, Object>> getRecentActivities() {
        List<Appointment> recentAppointments = appointmentRepository.findTop5ByOrderByDateTimeDesc();

        return recentAppointments.stream()
        .map(a -> Map.<String, Object>of(
            "description", "Appointment for " + a.getPatient().getFirstname() + " " + a.getPatient().getLastname() +
                " with Dr. " + a.getDoctor().getFirstname() + " " + a.getDoctor().getLastname() +
                " on " + a.getDateTime().toLocalDate()
        ))
        .collect(Collectors.toList());
    }
    
}
