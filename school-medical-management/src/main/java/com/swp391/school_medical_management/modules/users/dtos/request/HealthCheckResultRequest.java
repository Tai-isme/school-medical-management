package com.swp391.school_medical_management.modules.users.dtos.request;

import lombok.Data;

@Data
public class HealthCheckResultRequest {
    private String level;
    private String note;
    private String vision;
    private String hearing;
    private Double weight;
    private Double height;
    private String dentalStatus;
    private String bloodPressure;
    private String heartRate;
    private String generalCondition;
    private Boolean isChecked;
    private Long healthCheckFormId;
    private Long nurseId;
}