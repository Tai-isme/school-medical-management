package com.swp391.school_medical_management.modules.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swp391.school_medical_management.modules.users.entities.VaccineUnitEntity;


public interface  VaccineUnitRepository extends JpaRepository<VaccineUnitEntity, Integer>{
    
}
