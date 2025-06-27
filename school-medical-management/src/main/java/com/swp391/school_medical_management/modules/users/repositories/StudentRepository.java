package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.ClassEntity;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
    List<StudentEntity> findStudentByParent_UserId(Long aLong);

    Optional<StudentEntity> findStudentById(Long id);

    List<StudentEntity> findByClassEntity(ClassEntity classEntity);

    @Query("""
            SELECT s FROM StudentEntity s
            WHERE s.id NOT IN (
                SELECT DISTINCT vh.medicalRecord.student.id
                FROM VaccineHistoryEntity vh
                WHERE
                    (:vaccineProgramId IS NOT NULL AND vh.vaccineProgram.vaccineId = :vaccineProgramId)
                    OR
                    (:vaccineName IS NOT NULL AND LOWER(vh.vaccineName) = LOWER(:vaccineName))
            )
            """)
    List<StudentEntity> findStudentsNeverVaccinatedByProgramOrName(
            @Param("vaccineProgramId") Long vaccineProgramId,
            @Param("vaccineName") String vaccineName);

    long count();
}
