package com.swp391.school_medical_management.modules.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swp391.school_medical_management.modules.users.entities.MedicalEventEntity;

import java.util.List;
import java.util.Optional;

import com.swp391.school_medical_management.modules.users.entities.StudentEntity;


public interface MedicalEventRepository extends JpaRepository<MedicalEventEntity, Integer>{
    Optional<MedicalEventEntity> findByStudentAndTypeEventAndDescription(StudentEntity student, String typeEvent, String description);
    Optional<MedicalEventEntity> findByEventId(Long eventId);
    List<MedicalEventEntity> findByStudent(StudentEntity student);
}