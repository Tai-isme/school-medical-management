package com.swp391.school_medical_management.modules.users.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackDTO {
    private Integer feedbackId;
    private String satisfaction;
    private String comment;
    private String status;
    private Long parentId;
    private Long nurseId;
    private Integer vaccineResultId;
    private Integer healthResultId;
}
