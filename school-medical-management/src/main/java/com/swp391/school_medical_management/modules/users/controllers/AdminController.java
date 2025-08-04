package com.swp391.school_medical_management.modules.users.controllers;

import com.swp391.school_medical_management.modules.users.dtos.request.*;
import com.swp391.school_medical_management.modules.users.dtos.response.*;
import com.swp391.school_medical_management.modules.users.services.impl.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<UserDTO> updateAccount(@PathVariable int userId, @RequestBody UpdateProfileRequest request) {
        UserDTO userDTO = adminService.updateAccount(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id, @RequestBody BlacklistTokenRequest request) {
        adminService.deleteAccount(id, request.getToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/health-check-program")
    public ResponseEntity<HealthCheckProgramDTO> createHealthCheckProgram(@Valid @RequestBody HealthCheckProgramRequest request) {
        String adminId = SecurityContextHolder.getContext().getAuthentication().getName();
        HealthCheckProgramDTO healthCheckProgramDTO = adminService.createHealthCheckProgram(request, Integer.parseInt(adminId));
        return ResponseEntity.status(HttpStatus.CREATED).body(healthCheckProgramDTO);
    }

    @PutMapping("/health-check-program/{id}")
    public ResponseEntity<HealthCheckProgramDTO> updateHealthCheckProgram(@Valid @RequestBody HealthCheckProgramRequest request, @PathVariable int id) {
        HealthCheckProgramDTO healthCheckProgramDTO = adminService.updateHealthCheckProgram(id, request);
        return ResponseEntity.ok(healthCheckProgramDTO);
    }

    @PatchMapping("/health-check-program/{id}")
    public ResponseEntity<HealthCheckProgramDTO> updateHealthCheckProgramStatus(@PathVariable int id, @RequestParam("status") String status) {
        HealthCheckProgramDTO healthCheckProgramDTO = adminService.updateHealthCheckProgramStatus(id, status);
        return ResponseEntity.ok(healthCheckProgramDTO);
    }

    @PatchMapping("/completed-health-check-program/{id}")
    public ResponseEntity<HealthCheckProgramDTO> completeHealthCheckProgram(@PathVariable int id, @RequestParam("status") String status) {
        HealthCheckProgramDTO healthCheckProgramDTO = adminService.completeHealthCheckProgram(id, status);
        return ResponseEntity.ok(healthCheckProgramDTO);
    }

    @PatchMapping("/completed-vaccine-program/{id}")
    public ResponseEntity<VaccineProgramDTO> completeVaccineProgramStatus(@PathVariable int id, @RequestParam("status") String status) {
        VaccineProgramDTO vaccineProgramDTO = adminService.completeVaccineProgramStatus(id, status);
        return ResponseEntity.ok(vaccineProgramDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NURSE')")
    @GetMapping("/health-check-program")
    public ResponseEntity<List<HealthCheckProgramDTO>> getAllHealthCheckProgram() {
        String adminId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<HealthCheckProgramDTO> healthCheckProgramList = adminService.getAllHealthCheckProgram(Integer.parseInt(adminId));
        return ResponseEntity.ok(healthCheckProgramList);
    }

    @GetMapping("/health-check-program/{id}")
    public ResponseEntity<HealthCheckProgramDTO> getAllHealthCheckProgramById(@PathVariable int id) {
        HealthCheckProgramDTO healthCheckProgramDTO = adminService.getHealthCheckProgramById(id);
        return ResponseEntity.ok(healthCheckProgramDTO);
    }

    @DeleteMapping("/health-check-program/{id}")
    public ResponseEntity<Void> deleteHealthCheckProgram(@PathVariable int id) {
        adminService.deleteHealthCheckProgram(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/vaccine-program")
    public ResponseEntity<VaccineProgramDTO> createVaccineProgram(@RequestBody VaccineProgramRequest request) {
        String adminId = SecurityContextHolder.getContext().getAuthentication().getName();
        VaccineProgramDTO vaccineProgramDTO = adminService.createVaccineProgram(request, Integer.parseInt(adminId));
        return ResponseEntity.status(HttpStatus.CREATED).body(vaccineProgramDTO);
    }

    @PutMapping("/vaccine-program/{vaccineProgramId}")
    public ResponseEntity<VaccineProgramDTO> updateVaccineProgram(@RequestBody VaccineProgramRequest request, @PathVariable int vaccineProgramId) {
        String adminId = SecurityContextHolder.getContext().getAuthentication().getName();
        VaccineProgramDTO vaccineProgramDTO = adminService.updateVaccineProgram(request, vaccineProgramId, Integer.parseInt(adminId));
        return ResponseEntity.ok(vaccineProgramDTO);
    }

    //Thien
    @PutMapping("/vaccine-program/{vaccineProgramId}/status")
    public ResponseEntity<VaccineProgramDTO> updateVaccineProgramStatus(@PathVariable int vaccineProgramId, @RequestParam("status") String status) {
        VaccineProgramDTO vaccineProgramDTO = adminService.updateVaccineProgramStatus(vaccineProgramId, status);
        return ResponseEntity.ok(vaccineProgramDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NURSE')")
    @GetMapping("/vaccine-program")
    public ResponseEntity<List<VaccineProgramDTO>> getAllVaccineProgram() {
        List<VaccineProgramDTO> vaccineProgramDTOList = adminService.getAllVaccineProgram();
        return ResponseEntity.ok(vaccineProgramDTOList);
    }

    @GetMapping("/vaccine-program/{vaccineProgramId}")
    public ResponseEntity<VaccineProgramDTO> getVaccineProgramById(@PathVariable int vaccineProgramId) {
        VaccineProgramDTO vaccineProgramDTO = adminService.getVaccineProgramById(vaccineProgramId);
        return ResponseEntity.ok(vaccineProgramDTO);
    }

    @DeleteMapping("/vaccine-program/{vaccineProgramId}")
    public ResponseEntity<Void> deleteVaccineProgram(@PathVariable int vaccineProgramId) {
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
    public ResponseEntity<List<StudentDTO>> getAllStudentInClass(@PathVariable int classId) {
        List<StudentDTO> studentList = adminService.getAllStudentInClass(classId);
        return ResponseEntity.ok(studentList);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NURSE')")
    @GetMapping("/medical-records/{studentId}")
    public ResponseEntity<RecordAndHistoryDTO> getMedicalRecordsByStudentId(@PathVariable int studentId) {
        RecordAndHistoryDTO recordAndHistoryDTO = adminService.getMedicalRecordByStudentId(studentId);
        if (recordAndHistoryDTO == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(recordAndHistoryDTO);
    }

    @PostMapping("/student/import-excel")
    public ResponseEntity<Map<String, String>> uploadStudentFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            adminService.importStudentFromExcel(file);
            return ResponseEntity.ok(Map.of("message", "Import thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
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
        return Map.of("studentCount", studentCount, "medicalRecordCount", medicalRecordCount, "vaccineProgramCount", vaccineProgramCount, "healthCheckProgramCount", healthCheckProgramCount, "processingMedicalRequestCount", processingMedicalRequestCount);
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
    public ResponseEntity<VaccineFormStatsDTO> getFormStats(@PathVariable int vaccineProgramId) {
        VaccineFormStatsDTO stats = adminService.getFormStatsByProgram(vaccineProgramId);
        return ResponseEntity.ok(stats);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NURSE')")
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
    public ResponseEntity<Map<String, String>> uploadVaccineNameExcel(@RequestParam("file") MultipartFile file) {
        String adminId = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            adminService.importVaccineNameFromExcel(file, Integer.parseInt(adminId));
            return ResponseEntity.ok(Map.of("message", "Import thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/export-vaccine-result-excel-by-vaccine-program/{vaccineProgramId}")
    public ResponseEntity<InputStreamResource> exportVaccineResultExcel(@PathVariable int vaccineProgramId) throws IOException {
        ByteArrayInputStream in = adminService.exportVaccineResultToExcel(vaccineProgramId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=vaccine_result_report.xlsx");

        return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(new InputStreamResource(in));
    }

    @PostMapping("/export-health-check-result-excel-by-health-check-program/{healthCheckProgramId}")
    public ResponseEntity<InputStreamResource> exportHealthCheckResultExcel(@PathVariable int healthCheckProgramId) throws IOException {
        ByteArrayInputStream in = adminService.exportHealthCheckResultToExcel(healthCheckProgramId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=health_check_result_report.xlsx");

        return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(new InputStreamResource(in));
    }
}
