package com.swp391.school_medical_management.modules.users.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.swp391.school_medical_management.modules.users.entities.HealthCheckFormEntity.HealthCheckFormStatus;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineFormEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineFormEntity.VaccineFormStatus;
import com.swp391.school_medical_management.modules.users.entities.VaccineProgramEntity;
import com.swp391.school_medical_management.modules.users.repositories.projection.ParticipationRateRaw;

public interface VaccineFormRepository extends JpaRepository<VaccineFormEntity, Long> {
  List<VaccineFormEntity> findVaccineFormEntityByVaccineProgramAndStudent(VaccineProgramEntity vaccineProgramEntity,
      StudentEntity student);

  Optional<VaccineFormEntity> findByIdAndStatus(Long id, VaccineFormStatus status);

  List<VaccineFormEntity> findAll();

  List<VaccineFormEntity> findAllByStudentAndStatusAndCommitIsTrue(StudentEntity student, VaccineFormStatus status);

  List<VaccineFormEntity> findByCommitTrue();

  List<VaccineFormEntity> findByCommitTrueAndVaccineProgram_VaccineId(Long programId);

  List<VaccineFormEntity> findByVaccineProgram_VaccineId(Long programId);

  // long countByVaccineProgram_IdAndStatusAndCommitFalse(Long programId,
  // VaccineFormStatus status);
  List<VaccineFormEntity> findAllByVaccineProgram_VaccineId(Long programId);

  Optional<VaccineFormEntity> findByStudentAndStatus(StudentEntity student, VaccineFormStatus status);

  @Query("""
      SELECT
        COUNT(CASE WHEN vf.commit = true THEN 1 END) AS committedCount,
        COUNT(vf) AS totalSent
      FROM VaccineFormEntity vf
      WHERE vf.vaccineProgram.vaccineId = :vaccineId
      """)
  ParticipationRateRaw getParticipationRateByVaccineId(@Param("vaccineId") Long vaccineId);

  Long countByVaccineProgram_VaccineId(Long vaccineProgramId);

  List<VaccineFormEntity> findByStatusAndVaccineProgram_VaccineId(VaccineFormStatus status, Long programId);

  Long countByVaccineProgram_VaccineIdAndCommitTrue(Long vaccineProgramId);

  @Query("SELECT f FROM VaccineFormEntity f WHERE f.vaccineProgram.id = :programId AND f.commit = true")
    List<VaccineFormEntity> findCommittedFormsByProgramId(@Param("programId") Long programId);
}
