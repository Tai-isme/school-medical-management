package com.swp391.school_medical_management.modules.users.dtos.request;

import lombok.Data;

@Data
public class CommitVaccineFormRequest {
    private boolean commit;
    private String note;
}
