package com.swp391.school_medical_management.modules.users.dtos.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class VaccineProgramRequest {
    private Long vaccineNameId;
    private String vaccineProgramName;
    private LocalDate startDate;
    private LocalDate dateSendForm;
    private String location;
    private String description;
    private Long nurseId;
    private Long adminId;

}
