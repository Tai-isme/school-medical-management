package com.swp391.school_medical_management.modules.users.dtos.request;

import java.sql.Time;

import lombok.Data;

@Data
public class MedicalRequestDetails {
    private String medicineName;
    private int quantity;
    private String instructions;
    private Time time;
}
