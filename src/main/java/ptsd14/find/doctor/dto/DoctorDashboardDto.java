package ptsd14.find.doctor.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DoctorDashboardDto {

    private Long totalPatients;
    private Long consultations;
    private DoctorInfo doctor;
    private List<RecentPatient> recentPatients;
     private ChartData chart;

    @Data
    @Builder
    public static class DoctorInfo {
        private Long id;
        private String name;
        private String specialization;
        private Double rating;
        private Integer ratingCount;
    }

    @Data
    @Builder
    public static class RecentPatient {
        private Long id;
        private String name;
        private int age;
        private String date;
    }

    @Data
    @Builder
    public static class ChartData {
        private long maleCount;
        private long femaleCount;
        private long otherCount;
    }

}


