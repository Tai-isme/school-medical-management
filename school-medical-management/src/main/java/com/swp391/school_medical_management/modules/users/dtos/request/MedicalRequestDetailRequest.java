package com.swp391.school_medical_management.modules.users.dtos.request;

import java.sql.Time;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MedicalRequestDetailRequest {
    
    @NotBlank(message = "Medicine name is required")
    @Size(max = 255, message = "Medicine name must be at most 255 characters")
    private String medicineName;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 1000, message = "Quantity must not exceed 1000")
    private int quantity;
    
    @NotBlank(message = "Instructions are required")
    @Size(max = 500, message = "Instructions must be at most 500 characters")
    private String instructions;

    @NotNull(message = "Time of use is required")
    private Time time;
}
