package com.swp391.school_medical_management.modules.users.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActiveRequest {
    @Email(message = "Email not valid!")
    @NotBlank(message = "Email cannot blank!")
    private String email;
}
