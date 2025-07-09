package com.swp391.school_medical_management.modules.users.dtos.request;
import lombok.Data;

@Data
public class MedicalEventRequest {
    private String typeEvent;
    private String description;
    private Long studentId;
}
