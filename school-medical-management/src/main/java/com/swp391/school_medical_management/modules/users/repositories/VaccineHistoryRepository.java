package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VaccineHistoryRepository extends JpaRepository<VaccineHistoryEntity, Integer> {
    VaccineHistoryEntity findByStudentAndVaccineNameEntity_vaccineNameId(StudentEntity student, int vaccineNameId);

    List<VaccineHistoryEntity> findByStudent(StudentEntity student);
}
