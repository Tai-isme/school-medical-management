package com.swp391.school_medical_management.modules.users.dtos.request;

import java.util.List;

import lombok.Data;

@Data
public class VaccineNameRequest {
    private String vaccineName;
    private String manufacture;
    private int ageFrom;
    private int ageTo;
    private int totalUnit;
    private String url;
    private String description;
    private int adminId;
    private List<VaccineUnitRequest> vaccineUnits;
}
