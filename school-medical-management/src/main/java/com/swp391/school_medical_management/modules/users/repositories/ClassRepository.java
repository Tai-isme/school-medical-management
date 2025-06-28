package com.swp391.school_medical_management.modules.users.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swp391.school_medical_management.modules.users.entities.ClassEntity;

public interface ClassRepository extends JpaRepository<ClassEntity, Integer> {
    // Define any custom query methods if needed
    Optional<ClassEntity> findClassEntityByTeacherName(String teacherName);

    Optional<ClassEntity> findByClassId(Long classId);

    Optional<ClassEntity> findByClassName(String className);

    List<ClassEntity> findAll();
}
