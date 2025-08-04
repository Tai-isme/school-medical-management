package com.swp391.school_medical_management.modules.users.dtos.request;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class MedicalEventRequest {
    private String eventName;
    private String typeEvent;
    private LocalDateTime date;
    private String description;
    private String actionsTaken;
    private String levelCheck;
    private String location;
    private int studentId;
    private Integer classId; // Thêm dòng này
}



