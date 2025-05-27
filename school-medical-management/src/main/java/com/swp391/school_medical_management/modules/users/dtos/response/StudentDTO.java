package com.swp391.school_medical_management.modules.users.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentDTO {
    private long id;
    private String fullName;
    private LocalDate dob;
    private String gender;
    private String relationship;
    private long classID;
    private long parentID;
}
