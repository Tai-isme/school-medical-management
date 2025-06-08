package com.swp391.school_medical_management.modules.users.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MedicalEventDTO {
    private Long eventId;
    private String typeEvent;
    private LocalDate date;
    private String description;
    private Long studentId;
    private String studentName;
    private Long nurseId;
    private String nurseName;
}

