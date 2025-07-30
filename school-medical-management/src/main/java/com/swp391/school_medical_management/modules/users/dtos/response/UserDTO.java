package com.swp391.school_medical_management.modules.users.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swp391.school_medical_management.modules.users.entities.UserEntity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private int id;
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String relationship;
    private String address;
    private UserRole role;
    private boolean isActive;
    List<StudentDTO> studentDTOs;
}
