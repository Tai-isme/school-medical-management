package com.swp391.school_medical_management.modules.users.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swp391.school_medical_management.modules.users.entities.VaccineProgramEntity;

public interface VaccineProgramRepository extends JpaRepository<VaccineProgramEntity, Long>{
    Optional<VaccineProgramEntity> findVaccineProgramByVaccineNameAndStatus(String vaccineName, String status);
    Optional<VaccineProgramEntity> findVaccineProgramByVaccineId(long vaccineId);
    List<VaccineProgramEntity> findAll();
}
