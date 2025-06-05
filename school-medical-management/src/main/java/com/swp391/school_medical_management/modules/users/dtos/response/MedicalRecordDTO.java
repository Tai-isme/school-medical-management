package com.swp391.school_medical_management.modules.users.dtos.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class    MedicalRecordDTO {
    private Long recordId;
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