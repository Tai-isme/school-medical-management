package com.swp391.school_medical_management.modules.users.repositories.projection;

public interface HealthCheckResultByProgramStatsRaw {
    Long getProgramId();
    String getProgramName();
    String getStatusHealth();
    Long getCount();
}
