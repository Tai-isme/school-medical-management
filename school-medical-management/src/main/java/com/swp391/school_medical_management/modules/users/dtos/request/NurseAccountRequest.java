package com.swp391.school_medical_management.modules.users.dtos.request;

import com.google.firebase.database.annotations.NotNull;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NurseAccountRequest {

    @NotBlank(message = "Email cannot be blank!")
    @Email(message = "Email is not valid!")
    private String email;

    @NotBlank(message = "Name cannot be blank!")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;
    
    @NotNull
    @NotBlank
    @Size(min = 6, message = "New password must be at least 6 characters")
    private String newPassword;
}
