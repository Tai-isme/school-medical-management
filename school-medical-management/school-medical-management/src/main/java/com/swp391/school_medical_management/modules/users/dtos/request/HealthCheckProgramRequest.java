package com.swp391.school_medical_management.modules.users.dtos.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class HealthCheckProgramRequest {
    
    @NotBlank(message = "Health check name must not be blank")
    @Size(max = 100, message = "Health check name must be at most 100 characters")
    private String healthCheckName;

    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    @NotNull(message = "Start date must not be null")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;

    @NotNull(message = "End date must not be null")
    @Future(message = "End date must be in the future")
    private LocalDate endDate;

    @Size(max = 255, message = "Note must be at most 255 characters")
    private String note;
}
