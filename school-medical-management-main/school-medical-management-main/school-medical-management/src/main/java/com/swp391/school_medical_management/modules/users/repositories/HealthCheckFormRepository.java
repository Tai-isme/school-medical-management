package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.HealthCheckForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthCheckFormRepository extends JpaRepository<HealthCheckForm, Long> {
    List<HealthCheckForm> findByNurseId(Long nurseId);
    List<HealthCheckForm> findByCommitTrue();
    List<HealthCheckForm> findByStudent_StudentNameContainingIgnoreCaseOrParent_FullNameContainingIgnoreCase(String studentName, String parentName);
}
