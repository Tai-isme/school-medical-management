package com.swp391.school_medical_management.modules.users.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VaccineHistoryDTO {
    private Long id;
    private String vaccineName;
    private String note;
    private VaccineProgramDTO vaccineProgramDTO;
}
