package com.swp391.school_medical_management.modules.users.dtos.response;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassStudentDTO {
    private int classId;
    private String teacherName;
    private String className;
    private int quantity;
    private List<StudentDTO> students;
}