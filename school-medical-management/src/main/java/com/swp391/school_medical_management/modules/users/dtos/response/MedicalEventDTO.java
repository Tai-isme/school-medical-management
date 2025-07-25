package com.swp391.school_medical_management.modules.users.dtos.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalEventDTO {
    private Integer eventId;
    private String typeEvent;
    private LocalDate date;
    private String description;
    private StudentDTO studentDTO;
    private UserDTO parentDTO;
    private UserDTO nurseDTO;
    private ClassDTO classDTO;
}
