package com.swp391.school_medical_management.modules.users.dtos.response;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthCheckProgramDTO {
    private int id;
    private String healthCheckName;
    private String description;
    private LocalDate dateSendForm;
    private LocalDate startDate;
    private String status;
    private String location;
    private int adminId;
    private int nurseId;
    private UserDTO adminDTO;
    private UserDTO nurseDTO;
    List<ParticipateClassDTO> participateClasses;
    List<HealthCheckFormDTO> healthCheckFormDTOs;
}
