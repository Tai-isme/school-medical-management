package com.swp391.school_medical_management.modules.users.repositories;
import java.util.List;
import java.util.Optional;

import com.swp391.school_medical_management.modules.users.entities.ClassEntity;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
        List<StudentEntity> findStudentByParent_UserId(Long aLong);

        Optional<StudentEntity> findStudentById(Long id);

        Optional<StudentEntity> findByFullNameAndDobAndGenderAndRelationshipAndClassEntityAndParent(String fullName,
                        LocalDate dob, String gender, String relationship, ClassEntity classEntity, UserEntity parent);

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

        List<StudentEntity> findByClassEntity_ClassId(Long classId);

        List<StudentEntity> findByFullNameContainingIgnoreCase(String keyword);

}
