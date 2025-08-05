package com.swp391.school_medical_management.modules.users.dtos.request;

import lombok.Data;

@Data
public class UpdateRequestDetailStatusRequest {
    private int requestDetailId;
    private String note;
    private String status;
}
