package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.MedicalRecordEntity;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalRecordsRepository extends JpaRepository<MedicalRecordEntity, Integer> {
    Optional<MedicalRecordEntity> findByStudent_Id(int studentId);

    Optional<MedicalRecordEntity> findByStudent(StudentEntity student);

    Optional<MedicalRecordEntity> findMedicalRecordByStudent_Id(int studentId);


    // List<MedicalRecordEntity> findMedicalRecordByStudent_Parent_UserId(Long parentId);
    // long count();

    // List<MedicalRecordEntity> findAllByStudent_Id(long studentId);
}
