package com.swp391.school_medical_management.modules.users.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swp391.school_medical_management.modules.users.entities.HealthCheckProgramEntity;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckProgramEntity.HealthCheckProgramStatus;

public interface HealthCheckProgramRepository extends JpaRepository<HealthCheckProgramEntity, Long> {
    Optional<HealthCheckProgramEntity> findByHealthCheckNameAndStatus(String healthCheckName, HealthCheckProgramStatus status);
    List<HealthCheckProgramEntity> findAll();
    Optional<HealthCheckProgramEntity> findById(Long id);
}
