package com.swp391.school_medical_management.modules.users.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineFormEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineProgramEntity;
import com.swp391.school_medical_management.modules.users.repositories.projection.ParticipationRateRaw;

public interface VaccineFormRepository extends JpaRepository<VaccineFormEntity, Integer> {
    // List<VaccineFormEntity> findVaccineFormEntityByVaccineProgramAndStudent(VaccineProgramEntity vaccineProgramEntity,
    //     StudentEntity student);

    // Optional<VaccineFormEntity> findByIdAndStatus(Long id, VaccineFormStatus status);

    // List<VaccineFormEntity> findAll();

    // List<VaccineFormEntity> findAllByStudentAndStatusAndCommitIsTrue(StudentEntity student, VaccineFormStatus status);

    List<VaccineFormEntity> findByStudent(StudentEntity student);


    // List<VaccineFormEntity> findByCommitTrue();

    List<VaccineFormEntity> findByCommitTrueAndVaccineProgram_VaccineId(int programId);

    // List<VaccineFormEntity> findByVaccineProgram_VaccineId(Long programId);

    // // long countByVaccineProgram_IdAndStatusAndCommitFalse(Long programId,
    // // VaccineFormStatus status);

    List<VaccineFormEntity> findByVaccineProgram(VaccineProgramEntity vaccineProgram);

    List<VaccineFormEntity> findVaccineFormEntityByVaccineProgramAndStudent(
        VaccineProgramEntity program, StudentEntity student);

    // Optional<VaccineFormEntity> findByStudentAndStatus(StudentEntity student, VaccineFormStatus status);

    // @Query("""
    //     SELECT
    //       COUNT(CASE WHEN vf.commit = true THEN 1 END) AS committedCount,
    //       COUNT(vf) AS totalSent
    //     FROM VaccineFormEntity vf
    //     WHERE vf.vaccineProgram.vaccineId = :vaccineId
    //     """)
    // ParticipationRateRaw getParticipationRateByVaccineId(@Param("vaccineId") Long vaccineId);

    // Long countByVaccineProgram_VaccineId(Long vaccineProgramId);

    // List<VaccineFormEntity> findByStatusAndVaccineProgram_VaccineId(VaccineFormStatus status, Long programId);

    // Long countByVaccineProgram_VaccineIdAndCommitTrue(Long vaccineProgramId);

    // @Query("SELECT f FROM VaccineFormEntity f WHERE f.vaccineProgram.id = :programId AND f.commit = true")
    //   List<VaccineFormEntity> findCommittedFormsByProgramId(@Param("programId") Long programId);

    //Thien
    @Query("""
            SELECT
              COUNT(CASE WHEN vf.commit = true THEN 1 END) AS committedCount,
              COUNT(vf) AS totalSent
            FROM VaccineFormEntity vf
            WHERE vf.vaccineProgram.vaccineId = :vaccineId
            """)
    ParticipationRateRaw getParticipationRateByVaccineId(@Param("vaccineId") int vaccineId);

    List<VaccineFormEntity> findByVaccineProgram_VaccineId(int id);

    // List<VaccineFormEntity> findByCommitTrueAndVaccineProgram_VaccineId(int programId);


}
