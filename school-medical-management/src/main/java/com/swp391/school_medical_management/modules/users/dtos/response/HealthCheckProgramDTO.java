package com.swp391.school_medical_management.modules.users.dtos.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthCheckProgramDTO {
    private Long id;
    private String healthCheckName; 
    private String description;
    private LocalDate dateSendForm;
    private LocalDate startDate;
    private String status;
    private String note;
    private int sended;
    private Long adminId;                
    private Long nurseId;
}
