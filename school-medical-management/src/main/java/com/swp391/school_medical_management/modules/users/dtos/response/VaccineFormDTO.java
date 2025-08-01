package com.swp391.school_medical_management.modules.users.dtos.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaccineFormDTO {
    private int id;
    private int studentID;
    private int parentID;
    private int nurseID;
    private int vaccineProgramID;
    private LocalDate expDate;
    private String note;
    private Boolean commit;
    private VaccineProgramDTO vaccineProgramDTO;
    private VaccineNameDTO vaccineNameDTO;
    private StudentDTO studentDTO;
    private UserDTO parentDTO;
    private UserDTO nurseDTO;
    private VaccineResultDTO vaccineResultDTO;
}
