package com.swp391.school_medical_management.modules.users.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VaccineHistoryDTO {
    private Long id;
    private int vaccineNameId;
    private String note;
    private boolean createBy;
    private int unit;
    private int studentId;
    private StudentDTO studentDTO;
    private VaccineNameDTO vaccineNameDTO;
}
