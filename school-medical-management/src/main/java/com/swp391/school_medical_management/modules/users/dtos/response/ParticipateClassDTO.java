package com.swp391.school_medical_management.modules.users.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipateClassDTO {
    private int participate_id;
    private int class_id;
    private int program_id;
    private String type;
    private ClassDTO classDTO;
    private HealthCheckProgramDTO healthCheckProgramDTO;
    private VaccineProgramDTO vaccineProgramDTO;
}
