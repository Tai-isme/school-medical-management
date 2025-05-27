package com.swp391.school_medical_management.helpers;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorDTO {
    private String message;
    private Map<String,String> errors;
}
