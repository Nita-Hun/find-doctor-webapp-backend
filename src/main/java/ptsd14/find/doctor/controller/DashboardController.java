package ptsd14.find.doctor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ptsd14.find.doctor.dto.DashboardStatsDto;
import ptsd14.find.doctor.service.DashboardService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboards")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/counts")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Map<String, Long>> getCounts() {
    DashboardStatsDto stats = dashboardService.getDashboardStats();
    Map<String, Long> counts = new HashMap<>();
    counts.put("doctors", stats.getDoctorCount());
    counts.put("patients", stats.getPatientCount());
    counts.put("appointments", stats.getAppointmentCount());
    counts.put("specializations", stats.getSpecializationCount());

    return ResponseEntity.ok()
            .header("Cache-Control", "private, max-age=60") // cache for 60 seconds
            .body(counts);
}


    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Map<String, Object>> getRevenueData() {
        return dashboardService.getMonthlyRevenue();
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardStatsDto> getStats() {
        DashboardStatsDto stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/appointments/upcoming")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DashboardStatsDto.AppointmentSummary>> getUpcomingAppointments() {
        List<DashboardStatsDto.AppointmentSummary> upcoming = dashboardService.getUpcomingAppointments();
        return ResponseEntity.ok(upcoming);
    }

    @GetMapping("/appointments/weekly")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DashboardStatsDto.AppointmentSummary>> getWeeklyAppointments() {
        List<DashboardStatsDto.AppointmentSummary> weeklyAppointments = dashboardService.getWeeklyAppointments();
        return ResponseEntity.ok(weeklyAppointments);
    }

    @GetMapping("/activity/recent")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Map<String, Object>> getRecentActivities() {
        return dashboardService.getRecentActivities();
    }
}
