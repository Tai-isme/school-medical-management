package com.swp391.school_medical_management.modules.users.controllers;

import com.swp391.school_medical_management.modules.users.dtos.response.NotificationMessageDTO;
import com.swp391.school_medical_management.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class NotifyController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notify/{userId}")
    public ResponseEntity<List<NotificationMessageDTO>> getAllNotify(@PathVariable int userId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<NotificationMessageDTO> notificationMessageDTOList = notificationService.getNotificationByUserId(Integer.parseInt(parentId));
        return ResponseEntity.ok(notificationMessageDTOList);
    }

    @PostMapping("/test-websocket")
    public ResponseEntity<Void> sendHello() {
        System.out.print("da vao");
        Map<String, String> payload = new HashMap<>();
        payload.put("message", "hello");
        messagingTemplate.convertAndSend("/topic/parent/1", payload);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/notify/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable int id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}