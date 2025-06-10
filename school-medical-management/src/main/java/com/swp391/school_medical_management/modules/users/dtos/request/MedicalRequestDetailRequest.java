package com.swp391.school_medical_management.modules.users.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MedicalRequestDetailRequest {
    
    @NotBlank(message = "Medicine name is required")
    @Size(max = 255, message = "Medicine name must be at most 255 characters")
    private String medicineName;

    @NotBlank(message = "Dosage cannot be blank")
    private String dosage;

    @NotBlank(message = "Time cannot be blank")
    private String time;
}
