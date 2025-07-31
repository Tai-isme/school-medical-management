package com.swp391.school_medical_management.modules.users.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRequestDTO {
    private int requestId;
    private String requestName;
    private LocalDate date;
    private String note;
    private String status;
    private String reasonRejected;
    private String image;
    private StudentDTO studentDTO;
    private UserDTO nurseDTO;
    private UserDTO parentDTO;
    private String reason;
    private List<MedicalRequestDetailDTO> medicalRequestDetailDTO;
}