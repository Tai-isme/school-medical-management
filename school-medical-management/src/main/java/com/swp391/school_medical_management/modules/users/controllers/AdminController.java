package com.swp391.school_medical_management.modules.users.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.swp391.school_medical_management.modules.users.dtos.request.BlacklistTokenRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.HealthCheckProgramRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.NurseAccountRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.UpdateProfileRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.VaccineNameRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.VaccineProgramRequest;
import com.swp391.school_medical_management.modules.users.dtos.response.ClassDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckProgramDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckResultStatsDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRecordDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.ParticipationDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.StudentDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.UserDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineFormStatsDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineNameDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineProgramDTO;
import com.swp391.school_medical_management.modules.users.services.impl.AdminService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/create-nurses-account")
    public ResponseEntity<UserDTO> createNurseAccount(@Valid @RequestBody NurseAccountRequest request) {
        UserDTO userDTO = adminService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NURSE')")
    @GetMapping("/accounts")
    public ResponseEntity<List<UserDTO>> getAllAccounts() {
        List<UserDTO> userList = adminService.getAllAccounts();
        return ResponseEntity.ok(userList);
    }

    @PutMapping("/account/{userId}")
    public ResponseEntity<UserDTO> updateAccount(@PathVariable long userId, @RequestBody UpdateProfileRequest request) {
        UserDTO userDTO = adminService.updateAccount(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id, @RequestBody BlacklistTokenRequest request) {
        adminService.deleteAccount(id, request.getToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/health-check-program")
    public ResponseEntity<HealthCheckProgramDTO> createHealthCheckProgram(
            @Valid @RequestBody HealthCheckProgramRequest request) {
        String adminId = SecurityContextHolder.getContext().getAuthentication().getName();
        HealthCheckProgramDTO healthCheckProgramDTO = adminService.createHealthCheckProgram(request,
                Long.parseLong(adminId));
        return ResponseEntity.status(HttpStatus.CREATED).body(healthCheckProgramDTO);
    }

    @PutMapping("/health-check-program/{id}")
    public ResponseEntity<HealthCheckProgramDTO> updateHealthCheckProgram(
            @Valid @RequestBody HealthCheckProgramRequest request, @PathVariable Long id) {
        HealthCheckProgramDTO healthCheckProgramDTO = adminService.updateHealthCheckProgram(id, request);
        return ResponseEntity.ok(healthCheckProgramDTO);
    }

    @PatchMapping("/health-check-program/{id}")
    public ResponseEntity<HealthCheckProgramDTO> updateHealthCheckProgramStatus(@PathVariable Long id,
            @RequestParam("status") String status) {
        HealthCheckProgramDTO healthCheckProgramDTO = adminService.updateHealthCheckProgramStatus(id, status);
        return ResponseEntity.ok(healthCheckProgramDTO);
    }

    @PatchMapping("/vaccine-program/{id}")
    public ResponseEntity<VaccineProgramDTO> updateVaccineProgramStatus(@PathVariable Long id,
            @RequestParam("status") String status) {
        VaccineProgramDTO vaccineProgramDTO = adminService.updateVaccineProgramStatus(id, status);
        return ResponseEntity.ok(vaccineProgramDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NURSE')")
    @GetMapping("/health-check-program")
    public ResponseEntity<List<HealthCheckProgramDTO>> getAllHealthCheckProgram() {
        String adminId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<HealthCheckProgramDTO> healthCheckProgramList = adminService
                .getAllHealthCheckProgram(Long.parseLong(adminId));
        return ResponseEntity.ok(healthCheckProgramList);
    }

    @GetMapping("/health-check-program/{id}")
    public ResponseEntity<HealthCheckProgramDTO> getAllHealthCheckProgramById(@PathVariable Long id) {
        HealthCheckProgramDTO healthCheckProgramDTO = adminService.getHealthCheckProgramById(id);
        return ResponseEntity.ok(healthCheckProgramDTO);
    }

    @DeleteMapping("/health-check-program/{id}")
    public ResponseEntity<Void> deleteHealthCheckProgram(@PathVariable Long id) {
        adminService.deleteHealthCheckProgram(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/vaccine-program")
    public ResponseEntity<VaccineProgramDTO> createVaccineProgram(@RequestBody VaccineProgramRequest request) {
        String adminId = SecurityContextHolder.getContext().getAuthentication().getName();
        VaccineProgramDTO vaccineProgramDTO = adminService.createVaccineProgram(request, Long.parseLong(adminId));
        return ResponseEntity.status(HttpStatus.CREATED).body(vaccineProgramDTO);
    }

    @PutMapping("/vaccine-program/{vaccineProgramId}")
    public ResponseEntity<VaccineProgramDTO> updateVaccineProgram(@RequestBody VaccineProgramRequest request,
            @PathVariable long vaccineProgramId) {
        VaccineProgramDTO vaccineProgramDTO = adminService.updateVaccineProgram(request, vaccineProgramId);
        return ResponseEntity.ok(vaccineProgramDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NURSE')")
    @GetMapping("/vaccine-program")
    public ResponseEntity<List<VaccineProgramDTO>> getAllVaccineProgram() {
        List<VaccineProgramDTO> vaccineProgramDTOList = adminService.getAllVaccineProgram();
        return ResponseEntity.ok(vaccineProgramDTOList);
    }

    @GetMapping("/vaccine-program/{vaccineProgramId}")
    public ResponseEntity<VaccineProgramDTO> getVaccineProgramById(@PathVariable long vaccineProgramId) {
        VaccineProgramDTO vaccineProgramDTO = adminService.getVaccineProgramById(vaccineProgramId);
        return ResponseEntity.ok(vaccineProgramDTO);
    }

    @DeleteMapping("/vaccine-program/{vaccineProgramId}")
    public ResponseEntity<Void> deleteVaccineProgram(@PathVariable long vaccineProgramId) {
        adminService.deleteVaccineProgram(vaccineProgramId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NURSE')")
    @GetMapping("/class")
    public ResponseEntity<List<ClassDTO>> getAllClass() {
        List<ClassDTO> classList = adminService.getAllClass();
        return ResponseEntity.ok(classList);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NURSE')")
    @GetMapping("/students/{classId}")
    public ResponseEntity<List<StudentDTO>> getAllStudentInClass(@PathVariable long classId) {
        List<StudentDTO> studentList = adminService.getAllStudentInClass(classId);
        return ResponseEntity.ok(studentList);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NURSE')")
    @GetMapping("/medical-records/{studentId}")
    public ResponseEntity<MedicalRecordDTO> getMedicalRecordsByStudentId(@PathVariable long studentId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        MedicalRecordDTO medicalRecordDTO = adminService.getMedicalRecordByStudentId(Long.parseLong(parentId),
                studentId);
        if (medicalRecordDTO == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(medicalRecordDTO);
    }

    @PostMapping("/student/import-excel")
    public ResponseEntity<String> uploadStudentFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            adminService.importStudentFromExcel(file);
            return ResponseEntity.ok("Import thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Import thất bại: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NURSE')")
    @GetMapping("/statistics/overview")
    public Map<String, Long> getOverviewStats() {
        long studentCount = adminService.countStudents();
        long medicalRecordCount = adminService.countMedicalRecords();
        long vaccineProgramCount = adminService.countVaccineProgram();
        long healthCheckProgramCount = adminService.countHealthCheckProgram();
        long processingMedicalRequestCount = adminService.countProcessingMedicalRequest();
        return Map.of(
                "studentCount", studentCount,
                "medicalRecordCount", medicalRecordCount,
                "vaccineProgramCount", vaccineProgramCount,
                "healthCheckProgramCount", healthCheckProgramCount,
                "processingMedicalRequestCount", processingMedicalRequestCount);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NURSE')")
    @GetMapping("/event-stats/monthly")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyStats(@RequestParam int year) {
        return ResponseEntity.ok(adminService.getEventStatsByMonth(year));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NURSE')")
    @GetMapping("/vaccine-results-status-by-program")
    public ResponseEntity<List<HealthCheckResultStatsDTO>> getVaccineResultStatusStatsByProgram() {
        return ResponseEntity.ok(adminService.getVaccineResultStatusStatsByProgram());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NURSE')")
    @GetMapping("/health-check-results-status-by-program")
    public ResponseEntity<List<HealthCheckResultStatsDTO>> getHealthCheckResultStatusStatsByProgram() {
        return ResponseEntity.ok(adminService.getHealthCheckResultStatusStatsByProgram());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NURSE')")
    @GetMapping("/committed-participation-rate")
    public ResponseEntity<ParticipationDTO> getParticipationRate() {
        return ResponseEntity.ok(adminService.getLatestParticipation());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NURSE')")
    @GetMapping("/stats/{vaccineProgramId}")
    public ResponseEntity<VaccineFormStatsDTO> getFormStats(@PathVariable Long vaccineProgramId) {
        VaccineFormStatsDTO stats = adminService.getFormStatsByProgram(vaccineProgramId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/get=all-VaccineName")
    public ResponseEntity<List<VaccineNameDTO>> getAllVaccineNames() {
        List<VaccineNameDTO> list = adminService.getAllVaccineNames();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/create-VaccineName")
    public ResponseEntity<VaccineNameDTO> createVaccineName(@RequestBody @Valid VaccineNameRequest request) {
        VaccineNameDTO dto = adminService.createVaccineName(request);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PostMapping("/vaccine-name/import-excel")
    public ResponseEntity<String> uploadVaccineNameExcel(@RequestParam("file") MultipartFile file) {
        try {
            adminService.importVaccineNameFromExcel(file);
            return ResponseEntity.ok("Import thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Import thất bại: " + e.getMessage());
        }
    }
}
