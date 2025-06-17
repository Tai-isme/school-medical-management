package com.swp391.school_medical_management.modules.users.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineFormEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineProgramEntity;

public interface VaccineFormRepository extends JpaRepository<VaccineFormEntity, Long> {
    Optional<VaccineFormEntity> findVaccineFormEntityByVaccineProgramAndStudent(VaccineProgramEntity vaccineProgramEntity, StudentEntity student);
    Optional<VaccineFormEntity> findVaccineFormEntityByvaccineFormId(Long vaccineFormId);
    List<VaccineFormEntity> findAll();
    List<VaccineFormEntity> findByStudent(StudentEntity student);
    List<VaccineFormEntity> findByCommitTrue();
}
