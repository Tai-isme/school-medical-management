package com.swp391.school_medical_management.modules.users.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineFormEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineProgramEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineFormEntity.VaccineFormStatus;

public interface VaccineFormRepository extends JpaRepository<VaccineFormEntity, Long> {
    Optional<VaccineFormEntity> findVaccineFormEntityByVaccineProgramAndStudent(VaccineProgramEntity vaccineProgramEntity, StudentEntity student);
    Optional<VaccineFormEntity> findByIdAndStatus(Long id, VaccineFormStatus status);
    List<VaccineFormEntity> findAll();
    List<VaccineFormEntity> findAllByStudentAndStatus(StudentEntity student, VaccineFormStatus status);
    List<VaccineFormEntity> findByCommitTrue();
    Optional<VaccineFormEntity> findByStudentAndStatus(StudentEntity student, VaccineFormStatus status);
}
