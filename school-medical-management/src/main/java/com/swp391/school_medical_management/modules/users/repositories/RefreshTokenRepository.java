package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.RefreshTokenEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);
    Optional<RefreshTokenEntity> findByUser(UserEntity userEntity);
    void deleteByUser(UserEntity userEntity);
}
