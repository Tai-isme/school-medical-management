package com.swp391.school_medical_management.modules.users.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.swp391.school_medical_management.modules.users.entities.HealthCheckFormEntity;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckFormEntity.HealthCheckFormStatus;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckProgramEntity;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.repositories.projection.ParticipationRateRaw;

public interface HealthCheckFormRepository extends JpaRepository<HealthCheckFormEntity, Long> {
        List<HealthCheckFormEntity> findHealthCheckFormEntityByHealthCheckProgramAndStudent(
                        HealthCheckProgramEntity healthCheckProgramEntity, StudentEntity student);

        Optional<HealthCheckFormEntity> findByIdAndStatus(Long id, HealthCheckFormStatus status);

        List<HealthCheckFormEntity> findAll();

        List<HealthCheckFormEntity> findAllByStudentAndStatusAndCommitIsTrue(StudentEntity student, HealthCheckFormStatus status);

        List<HealthCheckFormEntity> findByCommitTrue();

        List<HealthCheckFormEntity> findByCommitTrueAndHealthCheckProgram_Id(Long programId);

        List<HealthCheckFormEntity> findByHealthCheckProgram_Id(Long programId);

        List<HealthCheckFormEntity> findByStatusAndHealthCheckProgram_Id(HealthCheckFormStatus status, Long programId);

        // long countByHealthCheckProgram_IdAndStatusAndCommitFalse(Long programId, HealthCheckFormStatus status);

        List<HealthCheckFormEntity> findAllByHealthCheckProgram_Id(Long programId);

        Optional<HealthCheckFormEntity> findByStudentAndStatus(StudentEntity student, HealthCheckFormStatus status);

        @Query("""
                        SELECT
                        COUNT(CASE WHEN hf.commit = true THEN 1 END) AS committedCount,
                        COUNT(hf) AS totalSent
                        FROM HealthCheckFormEntity hf
                        WHERE hf.healthCheckProgram.Id = :healthCheckId
                        """)
        ParticipationRateRaw getParticipationRateByHealthCheckId(@Param("healthCheckId") Long healthCheckId);

        @Query("SELECT f FROM HealthCheckFormEntity f " +
                        "WHERE f.healthCheckProgram.id = :programId AND f.commit = true")
        List<HealthCheckFormEntity> findCommittedFormsByProgramId(@Param("programId") Long programId);

}
