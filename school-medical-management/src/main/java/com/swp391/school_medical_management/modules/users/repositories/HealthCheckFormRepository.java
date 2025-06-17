package com.swp391.school_medical_management.modules.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swp391.school_medical_management.modules.users.entities.HealthCheckFormEntity;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckProgramEntity;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;

import java.util.List;
import java.util.Optional;


public interface HealthCheckFormRepository extends JpaRepository<HealthCheckFormEntity, Long>{
    List<HealthCheckFormEntity> findHealthCheckFormEntityByHealthCheckProgramAndStudent(HealthCheckProgramEntity healthCheckProgramEntity, StudentEntity student);
    Optional<HealthCheckFormEntity> findById(Long id);
    List<HealthCheckFormEntity> findAll();
    List<HealthCheckFormEntity> findByStudent(StudentEntity student);
    List<HealthCheckFormEntity> findByCommitTrue();
    
}
