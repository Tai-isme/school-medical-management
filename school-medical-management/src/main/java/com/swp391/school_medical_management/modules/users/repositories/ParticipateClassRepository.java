package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.ParticipateClassEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParticipateClassRepository extends JpaRepository<ParticipateClassEntity, Integer> {

    @Modifying
    @Transactional
    @Query("DELETE FROM ParticipateClassEntity p WHERE p.programId = :programId")
    void deleteByHealthCheckProgramId(@Param("programId") int programId);

    List<ParticipateClassEntity> findByProgramId(int programId);

    List<ParticipateClassEntity> findByProgramIdAndType(int programId, ParticipateClassEntity.Type type);

}
