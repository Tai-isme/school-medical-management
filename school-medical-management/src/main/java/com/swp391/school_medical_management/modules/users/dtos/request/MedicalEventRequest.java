package com.swp391.school_medical_management.modules.users.dtos.request;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MedicalEventRequest {
    private String eventName;
    private String typeEvent;
    private LocalDateTime date;
    private String description;
    private String actionsTaken;
    private String levelCheck;
    private String location;
    private String image;
    private int studentId;
}
