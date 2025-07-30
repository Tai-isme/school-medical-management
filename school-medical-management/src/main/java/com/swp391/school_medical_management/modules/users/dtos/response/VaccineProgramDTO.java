package com.swp391.school_medical_management.modules.users.dtos.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaccineProgramDTO {
    private Long vaccineId;
    private VaccineNameDTO vaccineName;
    private String vaccineProgramName;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateSendForm;
    private String location;
    private String status;
    private String note;
    private int sended;
    private UserDTO nurse;
    private UserDTO admin;
}
