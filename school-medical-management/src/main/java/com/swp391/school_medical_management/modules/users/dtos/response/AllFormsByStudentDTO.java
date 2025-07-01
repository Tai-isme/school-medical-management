package com.swp391.school_medical_management.modules.users.dtos.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllFormsByStudentDTO {
    private List<HealthCheckFormDTO> healthCheckForms;
    private List<VaccineFormDTO> vaccineForms;
}
