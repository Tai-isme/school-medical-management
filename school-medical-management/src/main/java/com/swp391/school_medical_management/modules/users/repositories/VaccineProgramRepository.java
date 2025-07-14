package com.swp391.school_medical_management.modules.users.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swp391.school_medical_management.modules.users.entities.VaccineFormEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineNameEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineProgramEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineProgramEntity.VaccineProgramStatus;

public interface VaccineProgramRepository extends JpaRepository<VaccineProgramEntity, Long> {
    Optional<VaccineProgramEntity> findByVaccineNameAndStatus(VaccineNameEntity vaccineName, VaccineProgramStatus status);

    Optional<VaccineProgramEntity> findVaccineProgramByVaccineId(long vaccineId);

    List<VaccineProgramEntity> findAll();

    List<VaccineProgramEntity> findByStatus(VaccineProgramStatus status);

    long countByStatusIn(List<VaccineProgramStatus> statuses);

    Optional<VaccineProgramEntity> findTopByStatusOrderByVaccineDateDesc(VaccineProgramStatus status);

    Optional<VaccineProgramEntity> findTopByVaccineDateLessThanEqualOrderByVaccineDateDesc(LocalDate date);

    public interface VaccineFormRepository extends JpaRepository<VaccineFormEntity, Long> {
    
    Optional<VaccineProgramEntity> findByVaccineNameAndStatus(VaccineNameEntity vaccineName, VaccineProgramStatus status);
    
}

}
