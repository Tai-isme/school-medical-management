package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.HealthCheckProgramEntity;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckProgramEntity.HealthCheckProgramStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface HealthCheckProgramRepository extends JpaRepository<HealthCheckProgramEntity, Integer> {
    Optional<HealthCheckProgramEntity> findByHealthCheckNameAndStatus(String healthCheckName, HealthCheckProgramStatus status);


    // List<HealthCheckProgramEntity> findAll();

    // Optional<HealthCheckProgramEntity> findById(Long id);

    // List<HealthCheckProgramEntity> findByStatus(HealthCheckProgramStatus status);

    // Optional<HealthCheckProgramEntity> findTopByStatusOrderByEndDateDesc(HealthCheckProgramStatus status);

    // Optional<HealthCheckProgramEntity> findTopByStartDateLessThanOrderByStartDateDesc(LocalDate startDate);

    //Thien
    Optional<HealthCheckProgramEntity> findTopByStatusOrderByStartDateDesc(HealthCheckProgramStatus status);

    List<HealthCheckProgramEntity> findByAdmin_UserId(int userId);

    long countByStatusIn(Collection<HealthCheckProgramStatus> statuses);
}
