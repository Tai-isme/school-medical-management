package com.swp391.school_medical_management.modules.users.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BlacklistTokenRequest {

    @NotBlank(message = "Token khong duoc de trong")
    private String token;

}
