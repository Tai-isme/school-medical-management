package com.swp391.school_medical_management.modules.users.dtos.request;

import com.swp391.school_medical_management.modules.users.entities.MedicalRequestEntity.MedicalRequestStatus;

import lombok.Data;

@Data
public class UpdateMedicalRequestStatus {
    private MedicalRequestStatus status;
    private String reason_rejected;
}
