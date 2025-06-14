package com.swp391.school_medical_management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.swp391.school_medical_management.modules.users.dtos.response.NotificationMessage;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Gửi cho danh sách phụ huynh
    public void sendToParents(List<String> parentIds, NotificationMessage message) {
        for (String parentId : parentIds) {
            messagingTemplate.convertAndSend("/topic/parent/" + parentId, message);
        }
    }
}
