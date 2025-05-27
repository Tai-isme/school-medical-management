package com.swp391.school_medical_management.modules.users.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO{
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String role;
}
