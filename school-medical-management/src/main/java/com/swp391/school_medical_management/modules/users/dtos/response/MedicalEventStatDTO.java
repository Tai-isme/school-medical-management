package com.swp391.school_medical_management.modules.users.dtos.response;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalEventStatDTO {
    private LocalDate date;
    private Map<String, Integer> eventCounts = new HashMap<>();

    public MedicalEventStatDTO(LocalDate date) {
        this.date = date;
    }

    public void addCount(String type, int count) {
        this.eventCounts.put(type, count);
    }
}
