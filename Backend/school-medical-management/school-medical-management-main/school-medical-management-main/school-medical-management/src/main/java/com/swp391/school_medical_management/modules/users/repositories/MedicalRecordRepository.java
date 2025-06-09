package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.MedicalRecord;
import com.swp391.school_medical_management.modules.users.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    Optional<MedicalRecord> findByStudentId(Long studentId);
}
