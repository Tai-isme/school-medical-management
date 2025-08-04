package com.swp391.school_medical_management.modules.users.dtos.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecordDTO {
    private int recordId;
    private int studentId;
    private String allergies;
    private String chronicDisease;
    private String vision;
    private String hearing;
    private Double weight;
    private Double height;
    private LocalDateTime lastUpdate;
    private boolean createBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String note;
    private StudentDTO studentDTO;
    List<VaccineHistoryDTO> vaccineHistoryDTOS;
}