package com.swp391.school_medical_management.modules.users.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swp391.school_medical_management.modules.users.entities.HealthCheckFormEntity;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckResultEntity;

public interface HealthCheckResultRepository extends JpaRepository<HealthCheckResultEntity, Integer>{
    Optional<HealthCheckResultEntity> findByHealthCheckFormEntity(HealthCheckFormEntity healthCheckFormEntity);
    Optional<HealthCheckResultEntity> findByHealthResultId(Long healthResultId);
    List<HealthCheckResultEntity> findAll();
}
