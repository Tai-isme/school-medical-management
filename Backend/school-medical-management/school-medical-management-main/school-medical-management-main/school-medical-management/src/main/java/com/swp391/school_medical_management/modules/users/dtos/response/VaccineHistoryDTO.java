package com.swp391.school_medical_management.modules.users.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VaccineHistoryDTO {
    private Long id;
    private String vaccineName;
    private String note;
    private Long recordID;
}
