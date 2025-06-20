package com.swp391.school_medical_management.modules.users.repositories.projection;

import java.time.LocalDate;

public interface EventStatRaw {
    LocalDate getDate();
    String getTypeEvent();
    Long getCount();
}
