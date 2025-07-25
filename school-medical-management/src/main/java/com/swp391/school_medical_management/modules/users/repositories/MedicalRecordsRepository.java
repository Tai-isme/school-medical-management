package com.swp391.school_medical_management.modules.users.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swp391.school_medical_management.modules.users.entities.MedicalRecordEntity;

public interface MedicalRecordsRepository extends JpaRepository<MedicalRecordEntity, Long> {
    Optional<MedicalRecordEntity> findMedicalRecordByStudent_Id(long studentId);
    List<MedicalRecordEntity> findMedicalRecordByStudent_Parent_UserId(Long parentId);
    long count();

    List<MedicalRecordEntity> findAllByStudent_Id(long studentId);


}
