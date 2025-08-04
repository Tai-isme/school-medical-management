package com.swp391.school_medical_management.modules.users.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordAndHistoryDTO {
    private MedicalRecordDTO medicalRecord;
    private List<VaccineHistoryDTO> vaccineHistories;
}

