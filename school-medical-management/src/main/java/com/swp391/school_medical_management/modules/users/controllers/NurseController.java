package com.swp391.school_medical_management.modules.users.controllers;

import com.swp391.school_medical_management.modules.users.dtos.request.*;
import com.swp391.school_medical_management.modules.users.dtos.response.*;
import com.swp391.school_medical_management.modules.users.entities.MedicalRequestEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity.UserRole;
import com.swp391.school_medical_management.modules.users.services.impl.NurseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("api/nurse")
//@PreAuthorize("hasAnyRole('ROLE_NURSE', 'ROLE_ADMIN')")
public class NurseController {

    @Autowired
    private NurseService nurseService;

    @GetMapping("/medical-request/processing")
    public ResponseEntity<List<MedicalRequestDTO>> getPendingMedicalRequest() {
        List<MedicalRequestDTO> medicalrequestDTOList = nurseService.getPendingMedicalRequest();
        return ResponseEntity.ok(medicalrequestDTOList);
    }

    @GetMapping("/medical-request")
    public ResponseEntity<List<MedicalRequestDTO>> getAllMedicalRequest() {
        List<MedicalRequestDTO> medicalrequestDTOList = nurseService.getAllMedicalRequest();
        return ResponseEntity.ok(medicalrequestDTOList);
    }

    @GetMapping("/medical-request/{status}")
    public ResponseEntity<List<MedicalRequestDTO>> getAllMedicalRequestByStatus(@PathVariable String status) {
        List<MedicalRequestDTO> medicalrequestDTOList = nurseService.getAllMedicalRequestByStatus(status);
        return ResponseEntity.ok(medicalrequestDTOList);
    }

    @GetMapping("/medical-request-detail/{requestId}")
    public ResponseEntity<List<MedicalRequestDetailDTO>> getMedicalRequestDetail(@PathVariable int requestId) {
        List<MedicalRequestDetailDTO> medicalRequestDetailDTO = nurseService.getMedicalRequestDetail(requestId);
        return ResponseEntity.ok(medicalRequestDetailDTO);
    }

    @PutMapping("/{requestId}/status")
    public ResponseEntity<MedicalRequestDTO> updateMedicalRequestStatus(@PathVariable int requestId,
                                                                        @RequestBody @Valid UpdateMedicalRequestStatus request) {
        MedicalRequestDTO medicalRequestDTO = nurseService.updateMedicalRequestStatus(requestId, request);
        return ResponseEntity.ok(medicalRequestDTO);
    }

    @GetMapping("/health-check-forms/{healthCheckFormId}")
    public ResponseEntity<HealthCheckFormDTO> getHealthCheckFormById(@PathVariable int healthCheckFormId) {
        HealthCheckFormDTO healthCheckFormDTO = nurseService.getHealthCheckFormById(healthCheckFormId);
        return ResponseEntity.ok(healthCheckFormDTO);
    }

    @GetMapping("/health-check-forms/program/{programId}")
    public ResponseEntity<List<HealthCheckFormDTO>> getHealthCheckFormsByProgram(
            @PathVariable int programId,
            @RequestParam(required = false) Boolean committed) {
        List<HealthCheckFormDTO> list = nurseService.getHealthCheckFormsByProgram(programId, committed);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/health-check-forms-commit/program/{programId}")
    public ResponseEntity<List<HealthCheckFormDTO>> getCommittedHealthCheckFormsByProgram(
            @PathVariable int programId) {
        List<HealthCheckFormDTO> list = nurseService.getCommittedHealthCheckFormsByProgram(programId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/vaccine-forms-commit/program/{programId}")
    public ResponseEntity<List<VaccineFormDTO>> getCommittedVaccineFormsByProgram(@PathVariable int programId) {
        List<VaccineFormDTO> list = nurseService.getCommittedVaccineFormsByProgram(programId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/vaccine-forms/program/{programId}")
    public ResponseEntity<List<VaccineFormDTO>> getVaccineFormsByProgram(
            @PathVariable int programId,
            @RequestParam(required = false) Boolean committed) {
        List<VaccineFormDTO> forms = nurseService.getVaccineFormsByProgram(programId, committed);
        return ResponseEntity.ok(forms);
    }

    @GetMapping("/vaccine-forms/{vaccineFormId}")
    public ResponseEntity<VaccineFormDTO> getVaccineFormById(@PathVariable int vaccineFormId) {
        VaccineFormDTO vaccineFormDTO = nurseService.getVaccinFormById(vaccineFormId);
        return ResponseEntity.ok(vaccineFormDTO);
    }

    @GetMapping("/vaccine-forms/not-send/program/{programId}")
    public ResponseEntity<List<VaccineFormDTO>> getNotSentVaccineFormsByProgram(@PathVariable int programId) {
        List<VaccineFormDTO> forms = nurseService.getNotSentVaccineFormsByProgram(programId);
        return ResponseEntity.ok(forms);
    }

    @GetMapping("/health-check-forms/not-send/program/{programId}")
    public ResponseEntity<List<HealthCheckFormDTO>> getNotSentHealthCheckFormsByProgram(@PathVariable int programId) {
        List<HealthCheckFormDTO> forms = nurseService.getNotSentHealthCheckFormsByProgram(programId);
        return ResponseEntity.ok(forms);
    }

    @GetMapping("/programs/on-going")
    public ResponseEntity<OnGoingProgramDTO> getOnGoingPrograms() {
        return ResponseEntity.ok(nurseService.getOnGoingPrograms());
    }

    // @GetMapping("/draft-form/count/{programId}")
    // public ResponseEntity<Map<String, int>>
    // countDraftFormsByProgram(@PathVariable int programId) {
    // return ResponseEntity.ok(nurseService.countDraftFormByProgram(programId));
    // }

    @GetMapping("/medical-events/{studentId}")
    public ResponseEntity<List<MedicalEventDTO>> getMedicalEventsByStudent(@PathVariable int studentId) {
        List<MedicalEventDTO> events = nurseService.getMedicalEventsByStudent(studentId);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/medical-event")
    public ResponseEntity<MedicalEventDTO> createMedicalEvent(@RequestBody MedicalEventRequest request) {
        String nurseId = SecurityContextHolder.getContext().getAuthentication().getName();
        MedicalEventDTO medicalEventDTO = nurseService.createMedicalEvent(Integer.parseInt(nurseId), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(medicalEventDTO);
    }

    @PutMapping("/medical-event/{medicalEventId}")
    public ResponseEntity<MedicalEventDTO> updateMedicalEvent(@PathVariable int medicalEventId,
                                                              @RequestBody MedicalEventRequest request) {
        String nurseId = SecurityContextHolder.getContext().getAuthentication().getName();
        MedicalEventDTO medicalEventDTO = nurseService.updateMedicalEvent(Integer.parseInt(nurseId), medicalEventId,
                request);
        return ResponseEntity.ok(medicalEventDTO);
    }

    @GetMapping("/medical-event/{medicalEventId}")
    public ResponseEntity<MedicalEventDTO> getMedicalEvent(@PathVariable int medicalEventId) {
        MedicalEventDTO medicalEventDTO = nurseService.getMedicalEvent(medicalEventId);
        return ResponseEntity.ok(medicalEventDTO);
    }

    @GetMapping("/medical-event")
    public ResponseEntity<List<MedicalEventDTO>> getAllMedicalEvent() {
        List<MedicalEventDTO> medicalEventDTOList = nurseService.getAllMedicalEvent();
        return ResponseEntity.ok(medicalEventDTOList);
    }

    @DeleteMapping("/medical-event/{medicalEventId}")
    public ResponseEntity<Void> deleteMedicalEvent(@PathVariable int medicalEventId) {
        nurseService.deleteMedicalEvent(medicalEventId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/health-check-result")
    public ResponseEntity<List<HealthCheckResultDTO>> createDefaultHealthCheckResultsForAllCommittedForms() {
        List<HealthCheckResultDTO> healthCheckResultDTOList = nurseService
                .createDefaultHealthCheckResultsForAllCommittedForms();
        return ResponseEntity.status(HttpStatus.CREATED).body(healthCheckResultDTOList);
    }

    @PutMapping("/health-check-result/{healCheckResultId}")
    public ResponseEntity<HealthCheckResultDTO> putMethodName(@PathVariable int healCheckResultId,
                                                              @RequestBody HealthCheckResultRequest request) {
        HealthCheckResultDTO healthCheckResultDTO = nurseService.updateHealthCheckResult(healCheckResultId, request);
        return ResponseEntity.ok(healthCheckResultDTO);
    }

    @GetMapping("/health-check-result/{healCheckResultId}")
    public ResponseEntity<HealthCheckResultDTO> getHealthCheckResult(@PathVariable int healCheckResultId) {
        HealthCheckResultDTO healthCheckResultDTO = nurseService.getHealthCheckResult(healCheckResultId);
        return ResponseEntity.ok(healthCheckResultDTO);
    }

    @GetMapping("/health-check-result/program/{programId}")
    public ResponseEntity<List<HealthCheckResultDTO>> getHealthCheckResultByProgram(@PathVariable int programId) {
        List<HealthCheckResultDTO> resultDTOList = nurseService.getHealthCheckResultByProgram(programId);
        return ResponseEntity.ok(resultDTOList);
    }

    @DeleteMapping("/health-check-result/{healCheckResultId}")
    public ResponseEntity<Void> deleteHealthCheckResult(@PathVariable int healCheckResultId) {
        nurseService.deleteHealthCheckResult(healCheckResultId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/vaccine-result")
    public ResponseEntity<List<VaccineResultDTO>> createVaccineResult() {
        List<VaccineResultDTO> vaccineResultDTOList = nurseService.createDefaultVaccineResultsForAllCommittedForms();
        return ResponseEntity.status(HttpStatus.CREATED).body(vaccineResultDTOList);
    }

    @PutMapping("/vaccine-result/{vaccineResultId}")
    public ResponseEntity<VaccineResultDTO> updateVaccineResult(@PathVariable int vaccineResultId,
                                                                @RequestBody VaccineResultRequest request) {
        VaccineResultDTO vaccineResultDTO = nurseService.updateVaccineResult(vaccineResultId, request);
        return ResponseEntity.ok(vaccineResultDTO);
    }

    @GetMapping("/vaccine-result/{vaccineResultId}")
    public ResponseEntity<VaccineResultDTO> getVaccineResult(@PathVariable int vaccineResultId) {
        VaccineResultDTO vaccineResultDTO = nurseService.getVaccineResult(vaccineResultId);
        return ResponseEntity.ok(vaccineResultDTO);
    }

    @GetMapping("/vaccine-result/program/{programId}")
    public ResponseEntity<List<VaccineResultDTO>> getVaccineResultByProgram(@PathVariable int programId) {
        List<VaccineResultDTO> results = nurseService.getVaccineResultByProgram(programId);
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/vaccine-result/{vaccineResultId}")
    public ResponseEntity<Void> deleteVaccineResult(@PathVariable int vaccineResultId) {
        nurseService.deleteVaccineResult(vaccineResultId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/replyfeedback")
    public ResponseEntity<FeedbackDTO> replyToFeedback(
            @PathVariable("id") Integer feedbackId,
            @RequestBody ReplyFeedbackRequest request) {

        FeedbackDTO dto = nurseService.replyToFeedback(feedbackId, request);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/getfeedback/{nurseId}")
    public ResponseEntity<List<FeedbackDTO>> getFeedbacksForNurse(@PathVariable Integer nurseId) {
        return ResponseEntity.ok(nurseService.getFeedbacksForNurse(nurseId));
    }

    @GetMapping("/getAllFeedback")
    public ResponseEntity<List<FeedbackDTO>> getAllFeedbacks() {
        return ResponseEntity.ok(nurseService.getAllFeedbacks());
    }

    // @GetMapping("/students")
    // public ResponseEntity<List<StudentDTO>> getAllStudent() {
    // return ResponseEntity.ok(nurseService.getAllStudent());
    // }

    @GetMapping("/students")
    public ResponseEntity<List<ClassStudentDTO>> getAllStudent() {
        return ResponseEntity.ok(nurseService.getAllStudenttt());
    }

    // Lọc ra nhưng học sinh chưa từng tiêm loại vaccine name history đó hoặc chưa
    // từng tham gia chương trình vaccine
    @GetMapping("/students/not-vaccinated")
    public ResponseEntity<List<StudentDTO>> getStudentsNotVaccinated(
            @RequestParam(required = false) int vaccineProgramId,
            @RequestParam(required = false) int vaccineName) {
        List<StudentDTO> result = nurseService.getStudentsNotVaccinated(vaccineProgramId, vaccineName);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/create-blog")
    public ResponseEntity<BlogResponse> createPost(@RequestBody BlogRequest request) {
        BlogResponse response = nurseService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("all-blog")
    public ResponseEntity<List<BlogResponse>> getAllPosts() {
        List<BlogResponse> list = nurseService.getAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<BlogResponse> getPostBySlug(@PathVariable String slug) {
        BlogResponse response = nurseService.getBySlug(slug);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlogResponse> updatePost(@PathVariable int id,
                                                   @RequestBody BlogRequest request) {
        BlogResponse response = nurseService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable int id) {
        nurseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<UserDTO>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UserRole role) {
        return ResponseEntity.ok(nurseService.searchUsers(keyword, role));
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<StudentDTO>> getStudentsByClass(@PathVariable int classId) {
        List<StudentDTO> students = nurseService.getStudentsByClass(classId);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/search-medicalrecord")
    public ResponseEntity<List<MedicalRecordDTO>> searchMedicalRecords(@RequestParam String keyword) {
        List<MedicalRecordDTO> records = nurseService.searchMedicalRecordsByStudentName(keyword);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<MedicalRequestDTO>> getByStatus(@PathVariable String status) {
        var statusEnum = MedicalRequestEntity.MedicalRequestStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(nurseService.getByStatus(statusEnum));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<MedicalRequestDTO>> getByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(nurseService.getByDate(date));
    }

    @GetMapping("/class/{classId}/students")
    public ResponseEntity<List<MedicalRequestDTO>> getByClass(@PathVariable int classId) {
        return ResponseEntity.ok(nurseService.getByClass(classId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MedicalRequestDTO>> searchByDateRange(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(nurseService.searchByDateRange(from, to));
    }

    @PostMapping("/create-health-check-form/{programId}")
    public ResponseEntity<String> createHealthCheckForm(@PathVariable int programId) {
        nurseService.createFormsForHealthCheckProgram(programId);
        return ResponseEntity.ok("Forms generated successfully for all students.");
    }
  
    // @PostMapping("/create-default-by-program/{programId}")
    // public ResponseEntity<List<HealthCheckResultDTO>>
    // createResultsByProgram(@PathVariable int programId) {
    // List<HealthCheckResultDTO> results = nurseService
    // .createDefaultHealthCheckResultsByProgramId(programId);
    // return ResponseEntity.ok(results);
    // }

    @PostMapping("/create-healthCheckResult-byProgram-{programId}")
    public ResponseEntity<String> createHealthCheckResult(
            @PathVariable int programId,
            @RequestBody HealthCheckResultRequest request) {
        nurseService.createResultByProgramId(programId, request);
        return ResponseEntity.ok("Tạo kết quả khám sức khỏe thành công.");
    }

    @PostMapping("/create-vaccineResults-byProgram/{programId}")
    public ResponseEntity<String> createVaccineResultsByProgramId(
            @PathVariable int programId,
            @RequestBody VaccineResultRequest request) {
        nurseService.createVaccineResultsByProgramId(programId, request);
        return ResponseEntity.ok("Tạo kết quả tiêm chủng thành công.");
    }

    //Thien
    @PreAuthorize("hasAnyRole('ROLE_NURSE', 'ROLE_ADMIN')")
    @GetMapping("/class-list")
    public ResponseEntity<List<ClassDTO>> getAllClasses() {
        List<ClassDTO> classList = nurseService.getAllClasses();
        return ResponseEntity.ok(classList);
    }

    //Thien
    @PreAuthorize("hasAnyRole('ROLE_NURSE', 'ROLE_ADMIN')")
    @GetMapping("/nurse-list")
    public ResponseEntity<List<UserDTO>> getAllNurses() {
        List<UserDTO> nurseList = nurseService.getAllNurses();
        return ResponseEntity.ok(nurseList);
    }

    @PostMapping("/health-check-form/{programId}")
    public ResponseEntity<String> createFormsForProgram(
            @PathVariable int programId,
            @RequestParam("expDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expDate) {

        nurseService.createHealthCheckForm(programId, expDate);
        return ResponseEntity.ok("Tạo form thành công.");
    }

    //Update status
    @PatchMapping("/health-check-program/{id}")
    public ResponseEntity<HealthCheckProgramDTO> updateHealthCheckProgramStatus(@PathVariable int id, @RequestParam("status") String status) {
        HealthCheckProgramDTO healthCheckProgramDTO = nurseService.updateHealthCheckProgramStatus(id, status);
        return ResponseEntity.ok(healthCheckProgramDTO);
    }

    @GetMapping("/health-check-programs/{programId}/committed-forms")
    public ResponseEntity<List<HealthCheckFormDTO>> getCommittedForms(@PathVariable int programId) {
        List<HealthCheckFormDTO> dtos = nurseService.getCommittedFormsByProgramId(programId);
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/create-vaccine-form/{programId}")
    public ResponseEntity<String> createVaccineFormsForProgram(
            @PathVariable int programId,
            @RequestParam("expDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expDate) {

        nurseService.createVaccineForm(programId, expDate);
        return ResponseEntity.ok("Tạo form tiêm chủng thành công.");
    }

    @GetMapping("/health-check-results")
    public ResponseEntity<List<HealthCheckResultDTO>> getAllHealthCheckResults() {
        List<HealthCheckResultDTO> results = nurseService.getAllHealthCheckResults();
        return ResponseEntity.ok(results);
    }
}