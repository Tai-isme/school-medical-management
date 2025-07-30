package com.swp391.school_medical_management.modules.users.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MedicalRequestDetailRequest {
    
    @NotBlank(message = "Medicine name is required")
    @Size(max = 255, message = "Medicine name must be at most 255 characters")
    private String medicineName;

    @Positive(message = "Quantity must be a positive number")
    private Integer quantity;

    @Size(max = 20, message = "Type must be at most 20 characters")
    private String type;

    @Size(max = 100, message = "Time schedule must be at most 100 characters")
    private String timeSchedule;

    private String status;
}
