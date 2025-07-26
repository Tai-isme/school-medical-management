package com.swp391.school_medical_management.modules.users.dtos.request;

import lombok.Data;

@Data
public class VaccineNameRequest {
    private String vaccineName;
    
    private String manufacture;
    
    private String url;

    private String note; 
}
