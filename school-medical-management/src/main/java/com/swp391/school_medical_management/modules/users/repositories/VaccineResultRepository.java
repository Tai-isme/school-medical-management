package com.swp391.school_medical_management.modules.users.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swp391.school_medical_management.modules.users.entities.VaccineFormEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineResultEntity;

public interface VaccineResultRepository extends JpaRepository<VaccineResultEntity, Long> {
    Optional<VaccineResultEntity> findByVaccineFormEntity(VaccineFormEntity vaccineFormEntity);
    Optional<VaccineResultEntity> findById(Long id);
    List<VaccineResultEntity> findAll();
}
