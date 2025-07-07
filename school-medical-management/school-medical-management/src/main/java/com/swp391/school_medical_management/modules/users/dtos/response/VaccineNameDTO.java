package com.swp391.school_medical_management.modules.users.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VaccineNameDTO {
    private Long id;
    private String vaccineName;
    private String manufacture;
    private String url;
    private String note;
}
