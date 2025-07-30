package com.swp391.school_medical_management.modules.users.dtos.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VaccineResultRequest {
    private String resultNote;
    private String reaction;
    private String actionsTaken;
    private LocalDateTime createAt;
    private Boolean isRejected;
    private int vaccineFormId;
}
