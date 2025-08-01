package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.MedicalRequestEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalRequestEntity.MedicalRequestStatus;
import com.swp391.school_medical_management.modules.users.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface MedicalRequestRepository extends JpaRepository<MedicalRequestEntity, Integer> {
    long countByStatusIn(Collection<MedicalRequestStatus> statuses);
    // List<MedicalRequestEntity> findMedicalRequestEntityByStudent(StudentEntity student);

    // Optional<MedicalRequestEntity> findMedicalRequestEntityByRequestId(int requestId);

    // boolean existsByStudentAndStatus(StudentEntity student, MedicalRequestStatus status);

    List<MedicalRequestEntity> findByStatus(MedicalRequestStatus status);

    List<MedicalRequestEntity> findByParent(UserEntity parent);

    // List<MedicalRequestEntity> findByParent(UserEntity parent);

    // List<MedicalRequestEntity> findByDate(LocalDate date);
    // List<MedicalRequestEntity> findByDateBetween(LocalDate from, LocalDate to);
    // @Query("SELECT mr FROM MedicalRequestEntity mr WHERE mr.student.classEntity.classId = :classId")
    // List<MedicalRequestEntity> findByClassId(@Param("classId") Long classId);


}