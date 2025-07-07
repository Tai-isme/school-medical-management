package com.swp391.school_medical_management.modules.users.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swp391.school_medical_management.modules.users.entities.HealthCheckProgramEntity;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckProgramEntity.HealthCheckProgramStatus;

public interface HealthCheckProgramRepository extends JpaRepository<HealthCheckProgramEntity, Long> {
    Optional<HealthCheckProgramEntity> findByHealthCheckNameAndStatus(String healthCheckName,
            HealthCheckProgramStatus status);

    List<HealthCheckProgramEntity> findAll();

    Optional<HealthCheckProgramEntity> findById(Long id);

    long countByStatusIn(List<HealthCheckProgramStatus> statuses);

    Optional<HealthCheckProgramEntity> findTopByStatusOrderByEndDateDesc(HealthCheckProgramStatus status);

    Optional<HealthCheckProgramEntity> findTopByStartDateLessThanOrderByStartDateDesc(LocalDate startDate);

    List<HealthCheckProgramEntity> findByEndDateAfter(LocalDate startDate);

}
