package com.swp391.school_medical_management.modules.users.dtos.response;

import com.swp391.school_medical_management.modules.users.entities.VaccineHistoryEntity.VaccineName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VaccineHistoryDTO {
    private Long id;
    private VaccineName vaccineName;
    private String note;
    private VaccineProgramDTO vaccineProgramDTO;
}
