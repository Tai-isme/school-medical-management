package com.swp391.school_medical_management.modules.users.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @NotBlank
    private String fullName;

    @Email(message = "Email không hợp lệ")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Email không đúng định dạng")
    private String email;

    @Pattern(regexp = "^(03|05|07|08|09)[0-9]{8}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    private String address;
}