package com.swp391.school_medical_management.modules.users.dtos.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthCheckFormDTO {
    private int id;
    private int studentId;
    private int parentId;
    private int nurseId;
    private LocalDate expDate;  
    private String notes;
    private Boolean commit;
    private StudentDTO studentDTO;
    private UserDTO parentDTO;
    private UserDTO nurseDTO;
    private HealthCheckProgramDTO healthCheckProgramDTO;
    private HealthCheckResultDTO healthCheckResultDTO;
}
