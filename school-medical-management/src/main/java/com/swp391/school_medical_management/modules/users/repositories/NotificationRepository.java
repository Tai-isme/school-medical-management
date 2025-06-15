package com.swp391.school_medical_management.modules.users.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swp391.school_medical_management.modules.users.entities.NotificationEntity;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long>{
    List<NotificationEntity> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
}
