package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.ClassEntity;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<StudentEntity, Integer> {
    List<StudentEntity> findStudentByParent_UserId(int userId);

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

    List<StudentEntity> findByClassEntity(ClassEntity classEntity);

    Optional<StudentEntity> findByFullNameAndDobAndClassEntityAndParent(String fullName, LocalDate dob, ClassEntity classEntity, UserEntity parent);

    // List<StudentEntity> findByFullNameContainingIgnoreCase(String keyword);

    // @Query("SELECT s FROM StudentEntity s WHERE s.parent IS NOT NULL")
    // List<StudentEntity> findAllWithParent();

}
