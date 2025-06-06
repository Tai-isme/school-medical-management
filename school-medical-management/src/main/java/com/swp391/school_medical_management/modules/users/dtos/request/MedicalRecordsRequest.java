package com.swp391.school_medical_management.modules.users.dtos.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MedicalRecordsRequest {
    private Long studentId;
    private String allergies;
    private String chronicDisease;
    private String treatmentHistory;
    private String vision;
    private String hearing;
    private Double weight;
    private Double height;
    private LocalDateTime lastUpdate;
    private String note;
}
