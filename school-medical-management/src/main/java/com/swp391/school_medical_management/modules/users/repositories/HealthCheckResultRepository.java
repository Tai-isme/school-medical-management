package com.swp391.school_medical_management.modules.users.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckResultExportDTO;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckFormEntity;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckResultEntity;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.repositories.projection.HealthCheckResultByProgramStatsRaw;

public interface HealthCheckResultRepository extends JpaRepository<HealthCheckResultEntity, Integer> {
    // Optional<HealthCheckResultEntity> findByHealthCheckFormEntity(HealthCheckFormEntity healthCheckFormEntity);

    // Optional<HealthCheckResultEntity> findByHealthResultId(Long healthResultId);

    // List<HealthCheckResultEntity> findAll();

    Optional<HealthCheckResultEntity> findByHealthCheckForm_Id(int formId);

    List<HealthCheckResultEntity> findByStudent(StudentEntity student);

    // List<HealthCheckResultEntity> findByHealthCheckFormEntity_Student_Id(Long studentId);

    // List<HealthCheckResultEntity> findByHealthCheckFormEntity_HealthCheckProgram_Id(Long programId);

    // @Query("""
    //             SELECT
    //                 p.id AS programId,
    //                 p.healthCheckName AS programName,
    //                 hr.level AS statusHealth,
    //                 COUNT(hr) AS count
    //             FROM HealthCheckResultEntity hr
    //             JOIN hr.healthCheckFormEntity f
    //             JOIN f.healthCheckProgram p
    //             GROUP BY p.id, p.healthCheckName, hr.level
    //             ORDER BY p.id
    //         """)
    // List<HealthCheckResultByProgramStatsRaw> getHealthCheckResultStatusStatsByProgram();

    //Thien
    @Query("""
                            SELECT
                p.id AS programId,
                p.healthCheckName AS programName,
                COUNT(hr) AS count
            FROM HealthCheckResultEntity hr
            JOIN hr.healthCheckForm f
            JOIN f.healthCheckProgram p
            GROUP BY p.id, p.healthCheckName
            ORDER BY p.id
            """)
    List<HealthCheckResultByProgramStatsRaw> getHealthCheckResultStatusStatsByProgram();

    List<HealthCheckResultEntity> findByHealthCheckForm_HealthCheckProgram_Id(int programId);

    Optional<HealthCheckResultEntity> findByHealthCheckForm(HealthCheckFormEntity form);

    @Query(
            "SELECT new com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckResultExportDTO(" +
                    "   hp.healthCheckName, " +                      // programName
                    "   hp.status, " +                                  // programStatus
                    "   hp.startDate, " +                               // startDate
                    "   hp.location, " +                                // location
                    "   nurse.fullName, " +                             // nurseName
                    "   admin.fullName, " +                             // adminName
                    "   hp.description, " +                             // description
                    // Tổng học sinh ĐÃ KHÁM được tính bằng sub-query
                    "   (SELECT COUNT(r2) FROM HealthCheckResultEntity r2 WHERE r2.isChecked = TRUE), " +
                    // Tổng học sinh CHƯA KHÁM
                    "   (SELECT COUNT(r2) FROM HealthCheckResultEntity r2 WHERE r2.isChecked = FALSE), " +
                    // Thông tin từng học sinh
                    "   s.id, " +                       // studentCode (dùng id làm code)
                    "   s.fullName, " +                                 // studentName
                    "   s.dob, " +                                      // dob
                    "   s.gender, " +                                   // gender
                    "   c.className, " +                                // className
                    "   parent.fullName, " +                            // parentName
                    "   parent.phone, " +                               // phone
                    "   parent.address, " +                             // address
                    "   parent.relationship, " +                        // relationship

                    // Thông tin result
                    "   hr.generalCondition, " +
                    "   hr.vision, " +
                    "   hr.hearing, " +
                    "   hr.weight," +
                    "   hr.height," +
                    "   hr.dentalStatus," +
                    "   hr.bloodPressure," +
                    "   hr.heartRate," +
                    "   hr.note," +
                    "   hr.isChecked" +
                    ") " +
                    "FROM HealthCheckResultEntity hr " +
                    " JOIN hr.healthCheckForm hf " +
                    " JOIN hf.healthCheckProgram hp " +
                    " JOIN hp.nurse nurse " +
                    " JOIN hp.admin admin " +
                    " JOIN hr.student s " +
                    " JOIN s.classEntity c " +
                    " JOIN s.parent parent " +
                    "WHERE hp.id = :healthCheckProgramId"
    )
    List<HealthCheckResultExportDTO> findExportByProgramId(@Param("healthCheckProgramId") int healthCheckProgramId);
}
