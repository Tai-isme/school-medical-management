package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.UserEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity.UserRole;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findUserByEmailOrPhone(String email, String phone);

    Optional<UserEntity> findUserByEmail(String email);

    Optional<UserEntity> findUserByUserId(int id);

    Optional<UserEntity> findUserByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    //Thien
    List<UserEntity> findByRole(UserRole role);
}
