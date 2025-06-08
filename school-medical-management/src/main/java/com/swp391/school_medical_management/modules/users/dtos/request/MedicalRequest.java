package com.swp391.school_medical_management.modules.users.dtos.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MedicalRequest {
    @NotBlank(message = "Request name is required")
    @Size(max = 255, message = "Request name must be at most 255 characters")
    private String requestName;

    @Size(max = 500, message = "Note must be at most 500 characters")
    private String note;

    @Positive(message = "Student ID must be a positive number")
    private long studentId;

    @NotEmpty(message = "Medical request details must not be empty")
    @Valid
    private List<MedicalRequestDetailRequest> medicalRequestDetailRequests;
}
