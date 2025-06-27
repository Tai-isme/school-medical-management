package com.swp391.school_medical_management.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.swp391.school_medical_management.modules.users.dtos.response.NotificationMessageDTO;
import com.swp391.school_medical_management.modules.users.entities.NotificationEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity;
import com.swp391.school_medical_management.modules.users.repositories.NotificationRepository;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ModelMapper modelMapper;

    public void sendNotificationToParent(Long parentId, String title, String content, String formType, long formId,
            boolean isRead) {
        UserEntity user = userRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setUser(user);
        notificationEntity.setTitle(title);
        notificationEntity.setContent(content);
        notificationEntity.setFormType(formType);
        notificationEntity.setFormId(formId);
        notificationEntity.setRead(isRead);
        notificationEntity.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notificationEntity);

        NotificationMessageDTO message = new NotificationMessageDTO(
                title,
                content,
                notificationEntity.getCreatedAt().toString(),
                formType,
                formId,
                isRead);
        messagingTemplate.convertAndSend("/topic/parent/" + parentId, message);
    }

    public List<NotificationMessageDTO> getNotificationByUserId(Long userId) {
        List<NotificationEntity> notificationEntityList = notificationRepository
                .findByUser_UserIdOrderByCreatedAtDesc(userId);
        List<NotificationMessageDTO> notificationMessageDTOList = notificationEntityList.stream()
                .map(notify -> modelMapper.map(notify, NotificationMessageDTO.class)).collect(Collectors.toList());
        return notificationMessageDTOList;
    }
}
