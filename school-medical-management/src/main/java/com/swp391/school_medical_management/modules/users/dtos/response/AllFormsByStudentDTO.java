package com.swp391.school_medical_management.modules.users.dtos.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllFormsByStudentDTO {
    private List<HealthCheckFormDTO> healthCheckForms;
    private List<VaccineFormDTO> vaccineForms;

    public void setHealthCheckForms(List<HealthCheckFormDTO> forms) {
        this.healthCheckForms = forms;
    }

}
