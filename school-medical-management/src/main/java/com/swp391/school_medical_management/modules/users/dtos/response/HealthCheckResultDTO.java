package com.swp391.school_medical_management.modules.users.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthCheckResultDTO {
    private Integer healthResultId;
    private String diagnosis;
    private String level;
    private String note;
    private String vision;
    private String hearing;
    private Double weight;
    private Double height;
    private HealthCheckFormDTO healthCheckFormDTO;
    private StudentDTO studentDTO;
}
