package com.swp391.school_medical_management.modules.users.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swp391.school_medical_management.modules.users.entities.VaccineNameEntity;

public interface VaccineNameRepository extends JpaRepository<VaccineNameEntity, Long> {
    Optional<VaccineNameEntity> findByVaccineName(String vaccineName); 
    Optional<VaccineNameEntity> findById(Long id);                     
}
