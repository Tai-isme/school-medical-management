package com.swp391.school_medical_management.modules.users.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.swp391.school_medical_management.modules.users.dtos.request.BlogRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.HealthCheckResultRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.MedicalEventRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.ReplyFeedbackRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.UpdateMedicalRequestStatus;
import com.swp391.school_medical_management.modules.users.dtos.request.VaccineResultRequest;
import com.swp391.school_medical_management.modules.users.dtos.response.BlogResponse;
import com.swp391.school_medical_management.modules.users.dtos.response.ClassStudentDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.FeedbackDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckFormDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckResultDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalEventDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRecordDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRequestDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRequestDetailDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.StudentDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.UserDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineFormDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineResultDTO;
import com.swp391.school_medical_management.modules.users.entities.MedicalRequestEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity.UserRole;
import com.swp391.school_medical_management.modules.users.services.impl.NurseService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("api/nurse")
// @PreAuthorize("hasAnyRole('ROLE_NURSE', 'ROLE_ADMIN')")
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

    @GetMapping("/medical-request-detail/{requestId}")
    public ResponseEntity<List<MedicalRequestDetailDTO>> getMedicalRequestDetail(@PathVariable int requestId) {
        List<MedicalRequestDetailDTO> medicalRequestDetailDTO = nurseService.getMedicalRequestDetail(requestId);
        return ResponseEntity.ok(medicalRequestDetailDTO);
    }

    @PutMapping("/medical-request/{requestId}/status")
    public ResponseEntity<MedicalRequestDTO> updateMedicalRequestStatus(@PathVariable int requestId,
            @RequestBody @Valid UpdateMedicalRequestStatus request) {
        MedicalRequestDTO medicalRequestDTO = nurseService.updateMedicalRequestStatus(requestId, request);
        return ResponseEntity.ok(medicalRequestDTO);
    }

    @GetMapping("/health-check-forms/{healthCheckFormId}")
    public ResponseEntity<HealthCheckFormDTO> getHealthCheckFormById(@PathVariable Long healthCheckFormId) {
        String nurseId = SecurityContextHolder.getContext().getAuthentication().getName();
        HealthCheckFormDTO healthCheckFormDTO = nurseService.getHealthCheckFormById(Long.parseLong(nurseId),
                healthCheckFormId);
        return ResponseEntity.ok(healthCheckFormDTO);
    }

    @GetMapping("/health-check-forms/commited")
    public ResponseEntity<List<HealthCheckFormDTO>> getAllCommitedTrueHealthCheckForm() {
        String nurseId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<HealthCheckFormDTO> healthCheckFormDTOList = nurseService
                .getAllCommitedTrueHealthCheckForm(Long.parseLong(nurseId));
        return ResponseEntity.ok(healthCheckFormDTOList);
    }

    @GetMapping("/vaccine-forms/{vaccineFormId}")
    public ResponseEntity<VaccineFormDTO> getVaccineFormById(@PathVariable Long vaccineFormId) {
        String nurseId = SecurityContextHolder.getContext().getAuthentication().getName();
        VaccineFormDTO vaccineFormDTO = nurseService.getVaccinFormById(Long.parseLong(nurseId), vaccineFormId);
        return ResponseEntity.ok(vaccineFormDTO);
    }

    @GetMapping("/vaccine-forms/commited")
    public ResponseEntity<List<VaccineFormDTO>> getAllCommitedTrueVaccineForm() {
        String nurseId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<VaccineFormDTO> vaccineFormDTOList = nurseService.getAllCommitedTrueVaccineForm(Long.parseLong(nurseId));
        return ResponseEntity.ok(vaccineFormDTOList);
    }

    @GetMapping("/draft-form/count")
    public ResponseEntity<Map<String, Long>> countDraftForms() {
        return ResponseEntity.ok(nurseService.countDraftForm());
    }

    @GetMapping("/medical-events/{studentId}")
    public ResponseEntity<List<MedicalEventDTO>> getMedicalEventsByStudent(@PathVariable Long studentId) {
        List<MedicalEventDTO> events = nurseService.getMedicalEventsByStudent(studentId);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/medical-event")
    public ResponseEntity<MedicalEventDTO> createMedicalEvent(@RequestBody MedicalEventRequest request) {
        String nurseId = SecurityContextHolder.getContext().getAuthentication().getName();
        MedicalEventDTO medicalEventDTO = nurseService.createMedicalEvent(Long.parseLong(nurseId), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(medicalEventDTO);
    }

    @PutMapping("/medical-event/{medicalEventId}")
    public ResponseEntity<MedicalEventDTO> updateMedicalEvent(@PathVariable Long medicalEventId,
            @RequestBody MedicalEventRequest request) {
        String nurseId = SecurityContextHolder.getContext().getAuthentication().getName();
        MedicalEventDTO medicalEventDTO = nurseService.updateMedicalEvent(Long.parseLong(nurseId), medicalEventId,
                request);
        return ResponseEntity.ok(medicalEventDTO);
    }

    @GetMapping("/medical-event/{medicalEventId}")
    public ResponseEntity<MedicalEventDTO> getMedicalEvent(@PathVariable Long medicalEventId) {
        MedicalEventDTO medicalEventDTO = nurseService.getMedicalEvent(medicalEventId);
        return ResponseEntity.ok(medicalEventDTO);
    }

    @GetMapping("/medical-event")
    public ResponseEntity<List<MedicalEventDTO>> getAllMedicalEvent() {
        List<MedicalEventDTO> medicalEventDTOList = nurseService.getAllMedicalEvent();
        return ResponseEntity.ok(medicalEventDTOList);
    }

    @DeleteMapping("/medical-event/{medicalEventId}")
    public ResponseEntity<Void> deleteMedicalEvent(@PathVariable Long medicalEventId) {
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
    public ResponseEntity<HealthCheckResultDTO> putMethodName(@PathVariable Long healCheckResultId,
            @RequestBody HealthCheckResultRequest request) {
        HealthCheckResultDTO healthCheckResultDTO = nurseService.updateHealthCheckResult(healCheckResultId, request);
        return ResponseEntity.ok(healthCheckResultDTO);
    }

    @GetMapping("/health-check-result/{healCheckResultId}")
    public ResponseEntity<HealthCheckResultDTO> getHealthCheckResult(@PathVariable Long healCheckResultId) {
        HealthCheckResultDTO healthCheckResultDTO = nurseService.getHealthCheckResult(healCheckResultId);
        return ResponseEntity.ok(healthCheckResultDTO);
    }

    @GetMapping("/health-check-result")
    public ResponseEntity<List<HealthCheckResultDTO>> getAllHealthCheckResult() {
        List<HealthCheckResultDTO> healthCheckResultDTOList = nurseService.getAllHealthCheckResult();
        return ResponseEntity.ok(healthCheckResultDTOList);
    }

    @DeleteMapping("/health-check-result/{healCheckResultId}")
    public ResponseEntity<Void> deleteHealthCheckResult(@PathVariable Long healCheckResultId) {
        nurseService.deleteHealthCheckResult(healCheckResultId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/vaccine-result")
    public ResponseEntity<List<VaccineResultDTO>> createVaccineResult() {
        List<VaccineResultDTO> vaccineResultDTOList = nurseService.createDefaultVaccineResultsForAllCommittedForms();
        return ResponseEntity.status(HttpStatus.CREATED).body(vaccineResultDTOList);
    }

    @PutMapping("/vaccine-result/{vaccineResultId}")
    public ResponseEntity<VaccineResultDTO> updateVaccineResult(@PathVariable Long vaccineResultId,
            @RequestBody VaccineResultRequest request) {
        VaccineResultDTO vaccineResultDTO = nurseService.updateVaccineResult(vaccineResultId, request);
        return ResponseEntity.ok(vaccineResultDTO);
    }

    @GetMapping("/vaccine-result/{vaccineResultId}")
    public ResponseEntity<VaccineResultDTO> getVaccineResult(@PathVariable Long vaccineResultId) {
        VaccineResultDTO vaccineResultDTO = nurseService.getVaccineResult(vaccineResultId);
        return ResponseEntity.ok(vaccineResultDTO);
    }

    @GetMapping("/vaccine-result")
    public ResponseEntity<List<VaccineResultDTO>> getAllVaccineResult() {
        List<VaccineResultDTO> vaccineResultDTOList = nurseService.getAllVaccineResult();
        return ResponseEntity.ok(vaccineResultDTOList);
    }

    @DeleteMapping("/vaccine-result/{vaccineResultId}")
    public ResponseEntity<Void> deleteVaccineResult(@PathVariable Long vaccineResultId) {
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
            @RequestParam(required = false) Long vaccineProgramId,
            @RequestParam(required = false) Long vaccineName) {
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
    public ResponseEntity<BlogResponse> updatePost(@PathVariable Long id,
            @RequestBody BlogRequest request) {
        BlogResponse response = nurseService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
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
    public ResponseEntity<List<StudentDTO>> getStudentsByClass(@PathVariable Long classId) {
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
    public ResponseEntity<List<MedicalRequestDTO>> getByClass(@PathVariable Long classId) {
        return ResponseEntity.ok(nurseService.getByClass(classId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MedicalRequestDTO>> searchByDateRange(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(nurseService.searchByDateRange(from, to));
    }

    @PutMapping("/{requestId}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable int requestId,
            @RequestParam("status") String status) {
        var newStatus = MedicalRequestEntity.MedicalRequestStatus.valueOf(status.toUpperCase());
        nurseService.updateStatus(requestId, newStatus);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/create-health-check-form/{programId}")
    public ResponseEntity<String> generateForms(@PathVariable Long programId) {
        nurseService.createFormsForHealthCheckProgram(programId);
        return ResponseEntity.ok("Forms generated successfully for all students.");
    }



}