package com.swp391.school_medical_management.modules.users.repositories.projection;

public interface EventStatRaw {
    Integer getMonth();
    String getTypeEvent();
    Long getCount();
}   