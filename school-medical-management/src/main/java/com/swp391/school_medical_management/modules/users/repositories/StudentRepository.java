package com.swp391.school_medical_management.modules.users.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.swp391.school_medical_management.modules.users.entities.ClassEntity;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity;

public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
    List<StudentEntity> findStudentByParent_UserId(Long aLong);

    // Optional<StudentEntity> findStudentById(Long id);

    // Optional<StudentEntity> findByFullNameAndDobAndGenderAndRelationshipAndClassEntityAndParent(String fullName,
    //         LocalDate dob, String gender, String relationship, ClassEntity classEntity, UserEntity parent);

    // List<StudentEntity> findByClassEntity(ClassEntity classEntity);

    // @Query("""
    //     SELECT s FROM StudentEntity s
    //     WHERE s.id NOT IN (
    //         SELECT DISTINCT vh.medicalRecord.student.id
    //         FROM VaccineHistoryEntity vh
    //         WHERE vh.vaccineNameEntity.vaccineNameId = (
    //             SELECT vp.vaccineName.vaccineNameId
    //             FROM VaccineProgramEntity vp
    //             WHERE vp.vaccineId = :vaccineProgramId
    //         )
    //     )
    // """)
    // List<StudentEntity> findStudentsNeverVaccinatedByProgramId(@Param("vaccineProgramId") Long vaccineProgramId);

    // @Query("""
    //     SELECT s FROM StudentEntity s
    //     WHERE s.id NOT IN (
    //         SELECT DISTINCT vh.medicalRecord.student.id
    //         FROM VaccineHistoryEntity vh
    //         WHERE vh.vaccineNameEntity.vaccineNameId = :vaccineNameId
    //     )
    // """)
    // List<StudentEntity> findStudentsNeverVaccinatedByVaccineNameId(@Param("vaccineNameId") Long vaccineNameId);

    // long count();

    // List<StudentEntity> findByClassEntity_ClassId(Long classId);

    // List<StudentEntity> findByFullNameContainingIgnoreCase(String keyword);

    // @Query("SELECT s FROM StudentEntity s WHERE s.parent IS NOT NULL")
    // List<StudentEntity> findAllWithParent();

}
