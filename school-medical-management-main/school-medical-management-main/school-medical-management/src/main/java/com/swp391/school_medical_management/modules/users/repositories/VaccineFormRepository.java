package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.VaccineForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VaccineFormRepository extends JpaRepository<VaccineForm, Long> {
    List<VaccineForm> findByStudentId(Long studentId);
    List<VaccineForm> findByCommitTrue();
    List<VaccineForm> findByStudent_StudentNameContainingIgnoreCaseOrParent_FullNameContainingIgnoreCase(String studentName, String parentName);
}
