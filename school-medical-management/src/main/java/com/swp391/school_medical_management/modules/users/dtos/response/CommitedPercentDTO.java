package com.swp391.school_medical_management.modules.users.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommitedPercentDTO {
    private String programName;
    private Long committedCount;
    private Long totalSent;
    private double percent;

        public CommitedPercentDTO(String programName, Long committedCount, Long totalSent) {
        this.programName = programName;
        this.committedCount = committedCount;
        this.totalSent = totalSent;
        this.percent = (totalSent == 0) ? 0 : (committedCount * 100.0 / totalSent);
    }
}
