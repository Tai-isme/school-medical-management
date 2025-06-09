package com.swp391.school_medical_management.modules.users.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HealthCheckProgramDTO {
    private Long id;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private String status;
    private String note;
    private Long adminId;
}
