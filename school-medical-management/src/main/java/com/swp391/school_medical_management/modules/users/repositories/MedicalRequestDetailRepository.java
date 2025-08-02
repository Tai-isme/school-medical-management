package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.MedicalRequestDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRequestDetailRepository extends JpaRepository<MedicalRequestDetailEntity, Integer> {
    List<MedicalRequestDetailEntity> findByMedicalRequest_RequestId(int medicalRequestRequestId);
}
