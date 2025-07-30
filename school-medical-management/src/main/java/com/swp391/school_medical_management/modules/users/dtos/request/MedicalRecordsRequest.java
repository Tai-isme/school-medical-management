package com.swp391.school_medical_management.modules.users.dtos.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MedicalRecordsRequest {

    private int studentId;

    private String allergies;

    private String chronicDisease;

    private String vision;

    private String hearing;

    private Double weight;

    private int height;

    private LocalDateTime lastUpdate;

    private String note;

    private List<VaccineHistoryRequest> vaccineHistories;
}
