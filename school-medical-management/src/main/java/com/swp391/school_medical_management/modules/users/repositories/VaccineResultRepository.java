package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.dtos.response.VaccineResultExportDTO;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineResultEntity;
import com.swp391.school_medical_management.modules.users.repositories.projection.HealthCheckResultByProgramStatsRaw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VaccineResultRepository extends JpaRepository<VaccineResultEntity, Integer> {
    // Optional<VaccineResultEntity> findByVaccineFormEntity(VaccineFormEntity vaccineFormEntity);

    // Optional<VaccineResultEntity> findById(Long id);
    // List<VaccineResultEntity> findByVaccineFormEntity_Student_Id(Long studentId);
    // List<VaccineResultEntity> findAll();

    List<VaccineResultEntity> findByStudentEntity(StudentEntity studentEntity);

    List<VaccineResultEntity> findByVaccineFormEntity_VaccineProgram_VaccineId(int vaccineId);

    // @Query("""
    //             SELECT
    //                 p.vaccineId AS programId,
    //                 p.vaccineName AS programName,
    //                 vr.statusHealth AS statusHealth,
    //                 COUNT(vr) AS count
    //             FROM VaccineResultEntity vr
    //             JOIN vr.vaccineFormEntity vf
    //             JOIN vf.vaccineProgram p
    //             GROUP BY p.vaccineId, p.vaccineName, vr.statusHealth
    //             ORDER BY p.vaccineId
    //         """)
    // List<HealthCheckResultByProgramStatsRaw> getVaccineResultStatusStatsByProgram();
    // Thien
    @Query("""
                            SELECT
                p.vaccineId AS programId,
                p.vaccineName.vaccineName AS programName,
                COUNT(vr) AS count
            FROM VaccineResultEntity vr
            JOIN vr.vaccineFormEntity vf
            JOIN vf.vaccineProgram p
            GROUP BY p.vaccineId, p.vaccineName.vaccineName
            ORDER BY p.vaccineId
            """)
    List<HealthCheckResultByProgramStatsRaw> getVaccineResultStatusStatsByProgram();

    @Query(
            "SELECT new com.swp391.school_medical_management.modules.users.dtos.response.VaccineResultExportDTO(" +
                    "   vp.vaccineProgramName, " +                      // programName
                    "   vp.startDate, " +                               // startDate
                    "   vp.location, " +                                // location
                    "   nurse.fullName, " +                             // nurseName
                    "   admin.fullName, " +                             // adminName
                    "   vp.description, " +                             // description
                    // Tổng học sinh TIÊM được tính bằng sub-query
                    "   (SELECT COUNT(r2) FROM VaccineResultEntity r2 " +
                    "       WHERE r2.vaccineFormEntity.vaccineProgram = vp AND r2.isInjected = TRUE), " +
                    // Tổng học sinh CHƯA TIÊM
                    "   (SELECT COUNT(r2) FROM VaccineResultEntity r2 " +
                    "       WHERE r2.vaccineFormEntity.vaccineProgram = vp AND r2.isInjected = FALSE), " +
                    "   vn.vaccineName, " +                             // vaccineName
                    "   vp.unit, " +                                    // unit
                    "   vn.manufacture, " +                             // manufature
                    "   vn.ageFrom, " +                                 // ageFrom
                    "   vn.ageTo, " +                                   // ageTo
                    "   vn.description, " +                             // vaccineDescription
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
                    "   vr.reaction, " +                                // reaction
                    "   vr.actionsTaken, " +                           // actionsTaken
                    "   vr.resultNote, " +                              // note
                    "   vr.isInjected" +                                // isInjected
                    ") " +
                    "FROM VaccineResultEntity vr " +
                    " JOIN vr.vaccineFormEntity vf " +
                    " JOIN vf.vaccineProgram vp " +
                    " JOIN vp.vaccineName vn " +
                    " JOIN vp.nurse nurse " +
                    " JOIN vp.admin admin " +
                    " JOIN vr.studentEntity s " +
                    " JOIN s.classEntity c " +
                    " JOIN s.parent parent " +
                    "WHERE vp.vaccineId = :vaccineProgramId"
    )
    List<VaccineResultExportDTO> findExportByProgramId(@Param("vaccineProgramId") int vaccineProgramId);

}
