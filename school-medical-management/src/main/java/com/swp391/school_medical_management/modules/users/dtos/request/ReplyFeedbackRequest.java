package com.swp391.school_medical_management.modules.users.dtos.request;

import lombok.Data;

@Data
public class ReplyFeedbackRequest {
    private String response;
    private Long nurseId;
}
