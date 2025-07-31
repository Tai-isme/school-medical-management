package com.swp391.school_medical_management.modules.users.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaccineProgramDTO {
    private int vaccineProgramId;
    private String vaccineProgramName;
    private String description;
    private int vaccineId;
    private int unit;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateSendForm;
    private String status;
    private String location;
    private int nurseId;
    private int adminId;
    private UserDTO nurseDTO;
    private UserDTO adminDTO;
    private VaccineNameDTO vaccineNameDTO;
    List<ParticipateClassDTO> participateClassDTOs;
    List<VaccineFormDTO> vaccineFormDTOs;
}
