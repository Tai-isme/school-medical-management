package com.swp391.school_medical_management.modules.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.swp391.school_medical_management.modules.users.entities.MedicalEventEntity;

import java.util.List;
import java.util.Optional;

import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.repositories.projection.EventStatRaw;


public interface MedicalEventRepository extends JpaRepository<MedicalEventEntity, Integer>{
    Optional<MedicalEventEntity> findByStudentAndTypeEventAndDescription(StudentEntity student, String typeEvent, String description);
    Optional<MedicalEventEntity> findByEventId(Long eventId);
    List<MedicalEventEntity> findByStudent(StudentEntity student);
    @Query("""
        SELECT e.date as date, e.typeEvent as typeEvent, COUNT(e) as count
        FROM MedicalEventEntity e
        GROUP BY e.date, e.typeEvent
        ORDER BY e.date
    """)
    List<EventStatRaw> getEventStatsRaw();
}