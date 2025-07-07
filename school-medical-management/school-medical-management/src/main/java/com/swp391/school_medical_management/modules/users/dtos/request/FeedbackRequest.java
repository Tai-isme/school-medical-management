package com.swp391.school_medical_management.modules.users.dtos.request;

import lombok.Data;

@Data
public class FeedbackRequest {
    private String satisfaction;
    private String comment;
    private Long parentId;
    private Long vaccineResultId;
    private Integer healthResultId;
}
