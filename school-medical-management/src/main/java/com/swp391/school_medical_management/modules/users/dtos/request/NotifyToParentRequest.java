package com.swp391.school_medical_management.modules.users.dtos.request;

import lombok.Data;

import java.util.List;

@Data
public class NotifyToParentRequest {
    private List<Integer> formIds;
}
