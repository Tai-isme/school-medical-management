package com.swp391.school_medical_management.modules.users.dtos.request;

import lombok.Data;

@Data
public class VaccineHistoryRequest {
    private int vaccineNameId;  
    private int unit;
    private String note;
    private boolean createBy;
}
