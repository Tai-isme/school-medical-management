package com.swp391.school_medical_management.modules.users.dtos.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VaccineNameDTO {
    private int id;
    private int userId;
    private String vaccineName;
    private String manufacture;
    private Integer ageFrom;
    private Integer ageTo;
    private Integer totalUnit;
    private String url;
    private String description;
    private UserDTO userDTO;
    List<VaccineUnitDTO> vaccineUnitDTOs;
}
