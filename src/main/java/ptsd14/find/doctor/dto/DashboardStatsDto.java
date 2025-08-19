package ptsd14.find.doctor.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class DashboardStatsDto {
    private long doctorCount;
    private long patientCount;
    private long appointmentCount;
    private long specializationCount;
    private BigDecimal totalRevenue;
    private List<DailyRevenue> revenueLast30Days = new ArrayList<>();
    private List<AppointmentSummary> upcomingAppointments = new ArrayList<>();

    @Data
    public static class DailyRevenue {
        private LocalDate date;
        private BigDecimal revenue;
    }

    @Data
    public static class AppointmentSummary {
        private Long id;
        private String patientName;
        private String doctorName;
        private String typeName;
        private String note;
        private LocalDateTime dateTime;
    }
}
