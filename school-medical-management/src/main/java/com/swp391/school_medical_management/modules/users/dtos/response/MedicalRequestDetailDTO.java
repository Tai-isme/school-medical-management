package com.swp391.school_medical_management.modules.users.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRequestDetailDTO {
    private int detailId;
    private String medicineName;
    private Integer quantity;
    private String type;
    private String timeSchedule;
    private String status;
    private String note;
    private String method;
    private int requestId;
}
