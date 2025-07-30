package com.swp391.school_medical_management.modules.users.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swp391.school_medical_management.modules.users.entities.ParticipateClassEntity;

public interface ParticipateClassRepository extends JpaRepository<ParticipateClassEntity, Integer> {
    List<ParticipateClassEntity> findByHealthCheckProgram_Id(Long programId);

}
