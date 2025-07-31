package com.swp391.school_medical_management.modules.users.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalEventDTO {
    private Integer eventId;
    private String typeEvent;
    private String eventName;
    private LocalDateTime date;
    private String description;
    private String actionsTaken;
    private String levelCheck;
    private String location;
    private StudentDTO studentDTO;
    private UserDTO parentDTO;
    private int studentId;
    private int nurseId;
    private UserDTO nurseDTO;
    private ClassDTO classDTO;
}
