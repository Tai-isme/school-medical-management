package com.swp391.school_medical_management.modules.users.dtos.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaccineResultDTO {
    private Integer vaccineResultId;
    private String statusHealth;
    private String resultNote;
    private String reaction;
    private LocalDateTime createdAt;
    private VaccineFormDTO vaccineFormDTO;
    private StudentDTO studentDTO;
}