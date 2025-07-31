package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassRepository extends JpaRepository<ClassEntity, Integer> {
    // Define any custom query methods if needed
    Optional<ClassEntity> findClassEntityByTeacherName(String teacherName);

    List<ClassEntity> findAll();

    Optional<ClassEntity> findByClassName(String className);
}
