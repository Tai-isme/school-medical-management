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
public class SchoolClassDTO {
    private Long id;
    private String teacherName;
    private String className;
    private Integer quantity;
}
