package com.swp391.school_medical_management.modules.users.dtos.response;

import java.sql.Time;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRequestDetailDTO {
    private int detailId;
    private String medicineName;
    private int quantity;
    private String instruction;
    private Time time;
    private int requestId;
}
