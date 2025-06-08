package com.swp391.school_medical_management.modules.users.dtos.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class MedicalRecordsRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;



    @Size(max = 255, message = "Allergies must be at most 255 characters")
    private String allergies;

    @Size(max = 255, message = "Chronic disease must be at most 255 characters")
    private String chronicDisease;

    @Size(max = 500, message = "Treatment history must be at most 500 characters")
    private String treatmentHistory;

    @Pattern(regexp = "^(Normal|Abnormal|Unknown)?$", message = "Vision must be 'Normal', 'Abnormal' or 'Unknown'")
    private String vision;

    @Pattern(regexp = "^(Normal|Abnormal|Unknown)?$", message = "Hearing must be 'Normal', 'Abnormal' or 'Unknown'")
    private String hearing;


    @DecimalMin(value = "1.0", message = "Weight must be at least 1kg")
    @DecimalMax(value = "300.0", message = "Weight must be less than or equal to 300kg")
    private Double weight;


    @DecimalMin(value = "30.0", message = "Height must be at least 30cm")
    @DecimalMax(value = "250.0", message = "Height must be less than or equal to 250cm")
    private Double height;


    // private LocalDateTime lastUpdate;


    @Size(max = 500, message = "Note must be at most 500 characters")
    private String note;


    private List<VaccineHistoryRequest> vaccineHistories;
}
