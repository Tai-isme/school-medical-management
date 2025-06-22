package com.swp391.school_medical_management.modules.users.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.swp391.school_medical_management.modules.users.entities.VaccineFormEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineResultEntity;
import com.swp391.school_medical_management.modules.users.repositories.projection.HealthCheckResultByProgramStatsRaw;

public interface VaccineResultRepository extends JpaRepository<VaccineResultEntity, Long> {
    Optional<VaccineResultEntity> findByVaccineFormEntity(VaccineFormEntity vaccineFormEntity);
    Optional<VaccineResultEntity> findById(Long id);
    List<VaccineResultEntity> findAll();

    @Query("""
        SELECT 
            p.vaccineId AS programId,
            p.vaccineName AS programName,
            vr.statusHealth AS statusHealth,
            COUNT(vr) AS count
        FROM VaccineResultEntity vr
        JOIN vr.vaccineFormEntity vf
        JOIN vf.vaccineProgram p
        GROUP BY p.vaccineId, p.vaccineName, vr.statusHealth
        ORDER BY p.vaccineId
    """)
    List<HealthCheckResultByProgramStatsRaw> getVaccineResultStatusStatsByProgram();
}
