package com.swp391.school_medical_management.modules.users.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshTokenDTO {
    private String token;
    private String refreshToken;
}
