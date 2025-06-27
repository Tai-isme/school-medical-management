package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.FeedbackEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Integer> {
    List<FeedbackEntity> findByParent(UserEntity parent);

    List<FeedbackEntity> findByNurse(UserEntity nurse);
}
