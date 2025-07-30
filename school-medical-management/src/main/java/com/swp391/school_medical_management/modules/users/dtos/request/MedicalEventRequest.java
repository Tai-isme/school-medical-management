package com.swp391.school_medical_management.modules.users.dtos.request;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MedicalEventRequest {
    private String typeEvent;
    private String description;
    private String actionsTaken;
    private String location;
    private String level;
    private LocalDateTime date;
    private Long studentId;
    private Long nurseId;
}
