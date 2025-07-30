package com.swp391.school_medical_management.modules.users.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.swp391.school_medical_management.modules.users.entities.HealthCheckFormEntity;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckResultEntity;
import com.swp391.school_medical_management.modules.users.repositories.projection.HealthCheckResultByProgramStatsRaw;

public interface HealthCheckResultRepository extends JpaRepository<HealthCheckResultEntity, Integer> {
    // Optional<HealthCheckResultEntity> findByHealthCheckFormEntity(HealthCheckFormEntity healthCheckFormEntity);

    // Optional<HealthCheckResultEntity> findByHealthResultId(Long healthResultId);

    // List<HealthCheckResultEntity> findAll();

    // Optional<HealthCheckResultEntity> findByHealthCheckFormEntity_Id(Long formId);

    // List<HealthCheckResultEntity> findByHealthCheckFormEntity_Student_Id(Long studentId);

    // List<HealthCheckResultEntity> findByHealthCheckFormEntity_HealthCheckProgram_Id(Long programId);

    // @Query("""
    //             SELECT
    //                 p.id AS programId,
    //                 p.healthCheckName AS programName,
    //                 hr.level AS statusHealth,
    //                 COUNT(hr) AS count
    //             FROM HealthCheckResultEntity hr
    //             JOIN hr.healthCheckFormEntity f
    //             JOIN f.healthCheckProgram p
    //             GROUP BY p.id, p.healthCheckName, hr.level
    //             ORDER BY p.id
    //         """)
    // List<HealthCheckResultByProgramStatsRaw> getHealthCheckResultStatusStatsByProgram();
}
