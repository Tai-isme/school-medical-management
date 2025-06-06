package com.swp391.school_medical_management.modules.users.dtos.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRequestDTO {
    private int requestId;
    private String requestName;
    private LocalDateTime date;
    private String note;
    private String status;
    private boolean commit;
    private long studentId;
    private long parentId;
    private String teacherName;
    private List<MedicalRequestDetailDTO> medicalRequestDetailDTO;
}