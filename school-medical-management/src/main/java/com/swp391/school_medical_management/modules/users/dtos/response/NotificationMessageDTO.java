package com.swp391.school_medical_management.modules.users.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessageDTO {
    private int id;
    private String title;
    private String content;
    private String formType;
    private int formId;
    private boolean isRead;
    private LocalDateTime createdAt;
}
