package com.swp391.school_medical_management.modules.users.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BlacklistTokenRequest {

    @NotNull(message = "Token cannot be null")
    @NotBlank(message = "Token cannot be blank")
    @Size(min = 10, max = 1000, message = "Token must be between 10 and 1000 characters")
    @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "Token can only contain letters, numbers, dots, underscores and hyphens")
    private String token;

}
