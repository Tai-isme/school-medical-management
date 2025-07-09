package com.swp391.school_medical_management.modules.users.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IdTokenRequest {

    @NotBlank(message = "ID Token must not be blank")
    private String idToken; // Firebase ID Token
}
