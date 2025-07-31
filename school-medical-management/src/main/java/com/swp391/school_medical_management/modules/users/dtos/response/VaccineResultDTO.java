package com.swp391.school_medical_management.modules.users.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaccineResultDTO {
    private int vaccineResultId;
    private int vaccineFormId;
    private int nurseId;
    private int studentId;
    private String reaction;
    private String actionsTaken;
    private String resultNote;
    private Boolean isInjected;
    private LocalDateTime createdAt;
    private VaccineFormDTO vaccineFormDTO;
    private StudentDTO studentDTO;
    private UserDTO nurseDTO;
}