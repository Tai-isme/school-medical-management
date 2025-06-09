package com.swp391.school_medical_management.modules.users.dtos.request;

import lombok.Data;

@Data
public class NotificationRequest {
    private Long parentId;
    private String message;
}