package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineHistoryEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineNameEntity;

import org.apache.poi.sl.draw.geom.GuideIf.Op;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VaccineHistoryRepository extends JpaRepository<VaccineHistoryEntity, Integer> {
    VaccineHistoryEntity findByStudentAndVaccineNameEntity_vaccineNameId(StudentEntity student, int vaccineNameId);

    List<VaccineHistoryEntity> findByStudent(StudentEntity student);

    @Query("SELECT COALESCE(SUM(v.unit), 0) FROM VaccineHistoryEntity v WHERE v.student = :student AND v.vaccineNameEntity = :vaccineName")
    int sumUnitsByStudentAndVaccineName(@Param("student") StudentEntity student,
                                        @Param("vaccineName") VaccineNameEntity vaccineName);


    Optional<VaccineHistoryEntity> findByStudentAndVaccineNameEntity_VaccineNameId(StudentEntity student, int vaccineNameId);

    Optional<VaccineHistoryEntity> findByStudentAndVaccineNameEntity_vaccineNameIdAndUnit(StudentEntity student, int vaccineName, int unit);

    Optional<VaccineHistoryEntity> findByStudentAndVaccineNameEntity(StudentEntity student, VaccineNameEntity vaccineName);

    void deleteAllByStudent(StudentEntity student);
}
