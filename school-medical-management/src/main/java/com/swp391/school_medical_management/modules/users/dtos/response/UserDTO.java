package com.swp391.school_medical_management.modules.users.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swp391.school_medical_management.modules.users.entities.UserEntity.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO{
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private String address;
    private UserRole role;
}
