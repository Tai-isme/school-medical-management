package com.swp391.school_medical_management.modules.users.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthCheckResultStatsDTO {
    private Long programId;
    private String programName;
    private String statusHealth;
    private Long count;
}
