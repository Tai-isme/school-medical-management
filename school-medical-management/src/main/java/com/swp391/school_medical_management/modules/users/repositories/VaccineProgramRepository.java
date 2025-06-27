package com.swp391.school_medical_management.modules.users.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swp391.school_medical_management.modules.users.entities.VaccineProgramEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineProgramEntity.VaccineProgramStatus;

public interface VaccineProgramRepository extends JpaRepository<VaccineProgramEntity, Long> {
    Optional<VaccineProgramEntity> findByVaccineNameAndStatus(String vaccineName, VaccineProgramStatus status);

    Optional<VaccineProgramEntity> findVaccineProgramByVaccineId(long vaccineId);

    List<VaccineProgramEntity> findAll();

    long countByStatusIn(List<VaccineProgramStatus> statuses);

    Optional<VaccineProgramEntity> findTopByStatusOrderByVaccineDateDesc(VaccineProgramStatus status);

}
