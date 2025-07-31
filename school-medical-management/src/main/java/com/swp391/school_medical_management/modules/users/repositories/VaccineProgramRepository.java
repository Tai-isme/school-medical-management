package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.VaccineProgramEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineProgramEntity.VaccineProgramStatus;

import jakarta.persistence.criteria.CriteriaBuilder.In;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VaccineProgramRepository extends JpaRepository<VaccineProgramEntity, Integer> {
    // Optional<VaccineProgramEntity> findByVaccineNameAndStatus(VaccineNameEntity vaccineName,
    //         VaccineProgramStatus status);

    // Optional<VaccineProgramEntity> findVaccineProgramByVaccineId(long vaccineId);

    // List<VaccineProgramEntity> findAll();

    // List<VaccineProgramEntity> findByStatus(VaccineProgramStatus status);

    // long countByStatusIn(List<VaccineProgramStatus> statuses);

    // Optional<VaccineProgramEntity> findTopByStatusOrderByVaccineDateDesc(VaccineProgramStatus status);

    // Optional<VaccineProgramEntity> findTopByVaccineDateLessThanEqualOrderByVaccineDateDesc(LocalDate date);

    // public interface VaccineFormRepository extends JpaRepository<VaccineFormEntity, Long> {

    //     Optional<VaccineProgramEntity> findByVaccineNameAndStatus(VaccineNameEntity vaccineName,
    //             VaccineProgramStatus status);

    // }

    //Thien
    Optional<VaccineProgramEntity> findTopByStatusOrderByStartDateDesc(VaccineProgramStatus status);

}
