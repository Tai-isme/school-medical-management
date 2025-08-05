package com.swp391.school_medical_management.service;

import com.swp391.school_medical_management.modules.users.dtos.response.NotificationMessageDTO;
import com.swp391.school_medical_management.modules.users.entities.NotificationEntity;
import com.swp391.school_medical_management.modules.users.repositories.NotificationRepository;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public NotificationMessageDTO sendNotificationToParent(int userId, String title, String content, String formType, int formId, boolean isRead) {
        NotificationEntity noti = new NotificationEntity();
        noti.setUser(userRepository.findById(userId).orElseThrow());
        noti.setTitle(title);
        noti.setContent(content);
        noti.setFormType(formType);
        noti.setFormId(formId);
        noti.setCreatedAt(LocalDateTime.now());
        noti.setRead(isRead);

        NotificationEntity saved = notificationRepository.save(noti);
        messagingTemplate.convertAndSend(
                "/topic/parent/" + userId,
                modelMapper.map(saved, NotificationMessageDTO.class)
        );
        return modelMapper.map(saved, NotificationMessageDTO.class);
    }

    public NotificationMessageDTO sendNotificationToAllNurse(String title, String content, String formType, int formId) {
        NotificationEntity noti = new NotificationEntity();
        noti.setUser(null);
        noti.setTitle(title);
        noti.setContent(content);
        noti.setFormType(formType);
        noti.setFormId(formId);
        noti.setCreatedAt(LocalDateTime.now());
        noti.setRead(false);

        NotificationEntity saved = notificationRepository.save(noti);

        NotificationMessageDTO dto = modelMapper.map(saved, NotificationMessageDTO.class);
        messagingTemplate.convertAndSend("/topic/nurse/global", dto);
        return dto;
    }

    public void markAsRead(int id) {
        NotificationEntity notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông báo"));

        notification.setRead(true); // isRead = true
        notificationRepository.save(notification);
    }

    public List<NotificationMessageDTO> getNotificationByUserId(int userId) {
        List<NotificationEntity> notificationEntityList = notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);
        List<NotificationMessageDTO> notificationMessageDTOList = notificationEntityList.stream().map(notify -> modelMapper.map(notify, NotificationMessageDTO.class)).collect(Collectors.toList());
        return notificationMessageDTOList;
    }
}
