package com.swp391.school_medical_management.modules.users.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VaccineFormDTO {
    private Long id;
    private Long vaccineId;
    private Long studentId;
    private Long parentId;
    private Long nurseId;
    private LocalDate formDate;
    private String note;
    private Boolean commit;
}
