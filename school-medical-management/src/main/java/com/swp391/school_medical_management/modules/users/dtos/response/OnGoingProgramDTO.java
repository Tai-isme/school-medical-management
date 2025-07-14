package com.swp391.school_medical_management.modules.users.dtos.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnGoingProgramDTO {
    private List<VaccineProgramDTO> vaccinePrograms;
    private List<HealthCheckProgramDTO> healthCheckPrograms;
}
