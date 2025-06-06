package com.swp391.school_medical_management.modules.users.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private UserDTO users;
    private List<StudentDTO> students;
}
