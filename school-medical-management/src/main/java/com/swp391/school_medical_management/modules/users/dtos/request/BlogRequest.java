package com.swp391.school_medical_management.modules.users.dtos.request;

import lombok.Data;

@Data
public class BlogRequest {
    private String title;
    private String category;
    private String content;
    private Long userId;
}