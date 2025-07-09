package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findUserByEmail(String email);

    Optional<UserEntity> findUserByUserId(Long id);

    Optional<UserEntity> findUserByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}
