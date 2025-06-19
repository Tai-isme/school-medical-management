package com.swp391.school_medical_management.modules.users.dtos.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaccineFormDTO {
    private Long id;
    private Long studentId;
    private Long parentId;
    private Long nurseId;
    private LocalDate formDate;
    private String notes;
    private Boolean commit;
    private String status;
    private VaccineProgramDTO vaccineProgram;
}
