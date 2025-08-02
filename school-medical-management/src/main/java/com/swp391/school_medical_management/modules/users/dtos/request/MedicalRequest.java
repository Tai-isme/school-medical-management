package com.swp391.school_medical_management.modules.users.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MedicalRequest {
    @NotBlank(message = "Request name is required")
    @Size(max = 255, message = "Request name must be at most 255 characters")
    private String requestName;

    @Size(max = 500, message = "Note must be at most 500 characters")
    private String note;

    private LocalDate date;

    @Size(max = 500, message = "Reason for rejection must be at most 500 characters")
    private String reasonRejected;


    @Positive(message = "Parent ID must be a positive number")
    private int parentId;

    @Positive(message = "Nurse ID must be a positive number")
    private int nurseId;

    @Positive(message = "Student ID must be a positive number")
    private int studentId;


    @NotEmpty(message = "Medical request details must not be empty")
    @Valid
    private List<MedicalRequestDetailRequest> medicalRequestDetailRequests;
}

