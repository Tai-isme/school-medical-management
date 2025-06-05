package com.swp391.school_medical_management.modules.users.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swp391.school_medical_management.modules.users.entities.MedicalRequestEntity;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;


public interface MedicalRequestRepository extends JpaRepository<MedicalRequestEntity, Integer> {
    List<MedicalRequestEntity> findMedicalRequestEntityByStudent(StudentEntity student);
    Optional<MedicalRequestEntity> findMedicalRequestEntityByRequestId(int requestId);
    boolean existsByStudentAndStatus(StudentEntity student, String status);
    List<MedicalRequestEntity> findMedicalRequestEntitiesByStatusIgnoreCase(String status);
}