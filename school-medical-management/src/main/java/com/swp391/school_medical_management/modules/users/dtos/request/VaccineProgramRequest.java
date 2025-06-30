package com.swp391.school_medical_management.modules.users.dtos.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class VaccineProgramRequest {
    private String vaccineName;
    private String manufacture;
    private String description;
    private LocalDate vaccineDate;
    private String note;
}
