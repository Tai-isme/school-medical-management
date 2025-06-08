package com.swp391.school_medical_management.modules.users.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MedicalRecordDTO {
    private Long id;
    private Long studentID;
    private String allergies;
    private String chronicDisease;
    private String treatmentHistory;
    private String vision;
    private String hearing;
    private Float weight;
    private Float hight;
    private LocalDate lastUpdate;
    private String note;
    private List<VaccineHistoryDTO> vaccineHistories;
}
