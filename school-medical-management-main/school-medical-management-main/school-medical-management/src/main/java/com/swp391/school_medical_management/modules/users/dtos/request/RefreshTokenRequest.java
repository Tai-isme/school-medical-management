package com.swp391.school_medical_management.modules.users.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @NotBlank(message = "RefreshToken can't blank")
    private String refreshToken;
}
