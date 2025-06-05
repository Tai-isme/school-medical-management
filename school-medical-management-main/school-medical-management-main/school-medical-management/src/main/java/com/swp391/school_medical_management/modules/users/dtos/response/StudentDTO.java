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
    private Long id;
    private String studentName;
    private LocalDate dob;
    private String gender;
    private String relationship;
    private Long classID;
    private Long parentID;
    private SchoolClassDTO schoolClass;
//    private String teacherName;
//    private String className;

}
