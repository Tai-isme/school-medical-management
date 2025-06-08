package com.swp391.school_medical_management.modules.users.dtos.request;

import java.util.List;

import lombok.Data;

@Data
public class HealthCheckFormCreateRequest {
    private Long healthCheckProgramId;
    private List<Long> studentIds;
}
