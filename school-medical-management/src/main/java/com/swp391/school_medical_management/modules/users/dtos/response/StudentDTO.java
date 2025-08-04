package com.swp391.school_medical_management.modules.users.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentDTO {
    private int studentId;
    private String fullName;
    private LocalDate dob;
    private String gender;
    private int classId;
    private int parentId;
    private String avatarUrl;
    private ClassDTO classDTO;
    private UserDTO parentDTO;
}
