package com.swp391.school_medical_management.modules.users.dtos.request;

import com.google.firebase.database.annotations.NotNull;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePassRequest {
    @NotNull
    @NotBlank
    @Size(min = 6, message = "Old password must be at least 6 characters")
    private String oldPassword;

    @NotNull
    @NotBlank
    @Size(min = 6, message = "New password must be at least 6 characters")
    private String newPassword;

    @NotNull
    @NotBlank
    @Email(message = "Email should be valid")
    private String email;
}

