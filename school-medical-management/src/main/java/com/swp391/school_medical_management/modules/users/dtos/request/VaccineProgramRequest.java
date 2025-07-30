package com.swp391.school_medical_management.modules.users.dtos.request;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class VaccineProgramRequest {
    private String vaccineProgramName;
    private int vaccineNameId;
    private int unit;
    private String description;
    private LocalDate startDate;
    private LocalDate dateSendForm;
    private String location;
    private int nurseId;
    private List<Integer> classIds;
}
