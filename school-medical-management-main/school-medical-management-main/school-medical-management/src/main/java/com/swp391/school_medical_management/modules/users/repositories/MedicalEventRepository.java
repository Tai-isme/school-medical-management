package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.MedicalEvent;
import com.swp391.school_medical_management.modules.users.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalEventRepository extends JpaRepository<MedicalEvent, Long> {
    List<MedicalEvent> findByStudent(Student student);
}
