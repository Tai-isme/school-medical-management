package com.swp391.school_medical_management.modules.users.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private String title;       // Tiêu đề thông báo
    private String content;     // Nội dung chính
    private String timestamp;   // Thời gian gửi (có thể là LocalDateTime.toString())
}
