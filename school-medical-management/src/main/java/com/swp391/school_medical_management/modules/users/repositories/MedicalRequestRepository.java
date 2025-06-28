package com.swp391.school_medical_management.modules.users.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.swp391.school_medical_management.modules.users.entities.MedicalRequestEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalRequestEntity.MedicalRequestStatus;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity;


public interface MedicalRequestRepository extends JpaRepository<MedicalRequestEntity, Integer> {
    List<MedicalRequestEntity> findMedicalRequestEntityByStudent(StudentEntity student);
    Optional<MedicalRequestEntity> findMedicalRequestEntityByRequestId(int requestId);
    boolean existsByStudentAndStatus(StudentEntity student, MedicalRequestStatus status);
    List<MedicalRequestEntity> findByStatus(MedicalRequestStatus status);
    List<MedicalRequestEntity> findByParent(UserEntity parent);
    long countByStatusIn(List<MedicalRequestStatus> statuses);
    List<MedicalRequestEntity> findByDate(LocalDate date);
    List<MedicalRequestEntity> findByDateBetween(LocalDate from, LocalDate to);
    @Query("SELECT mr FROM MedicalRequestEntity mr WHERE mr.student.classEntity.classId = :classId")
    List<MedicalRequestEntity> findByClassId(@Param("classId") Long classId);


}