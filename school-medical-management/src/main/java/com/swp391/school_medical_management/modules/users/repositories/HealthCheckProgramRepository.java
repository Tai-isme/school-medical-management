package com.swp391.school_medical_management.modules.users.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swp391.school_medical_management.modules.users.entities.HealthCheckProgramEntity;

public interface HealthCheckProgramRepository extends JpaRepository<HealthCheckProgramEntity, Long> {
    Optional<HealthCheckProgramEntity> findHealthCheckProgramEntityByHealthCheckNameAndStatus(String healthCheckName, String status);
    List<HealthCheckProgramEntity> findAll();
    Optional<HealthCheckProgramEntity> findHealthCheckProgramEntityById(Long id);
}
