package com.swp391.school_medical_management.modules.users.controllers;

import java.util.List;
import java.util.Map;

import com.swp391.school_medical_management.modules.users.dtos.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.swp391.school_medical_management.modules.users.dtos.request.BlogRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.HealthCheckResultRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.MedicalEventRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.VaccineResultRequest;
import com.swp391.school_medical_management.modules.users.services.impl.NurseService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Validated
@RestController
@RequestMapping("api/nurse")
@PreAuthorize("hasAnyRole('ROLE_NURSE', 'ROLE_ADMIN')")
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
    public ResponseEntity<MedicalRequestDTO> updateMedicalRequestStatus(@PathVariable int requestId,@Valid @RequestParam String status) {
    MedicalRequestDTO medicalRequestDTO = nurseService.updateMedicalRequestStatus(requestId, status);
    return ResponseEntity.ok(medicalRequestDTO);
    }
    
    @GetMapping("/health-check-forms/{healthCheckFormId}")
    public ResponseEntity<HealthCheckFormDTO> getHealthCheckFormById(@PathVariable Long healthCheckFormId) {
        String nurseId = SecurityContextHolder.getContext().getAuthentication().getName();
        HealthCheckFormDTO healthCheckFormDTO = nurseService.getHealthCheckFormById(Long.parseLong(nurseId), healthCheckFormId);
        return ResponseEntity.ok(healthCheckFormDTO);
    }

    @GetMapping("/health-check-forms/commited")
    public ResponseEntity<List<HealthCheckFormDTO>> getAllCommitedTrueHealthCheckForm() {
        String nurseId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<HealthCheckFormDTO> healthCheckFormDTOList = nurseService.getAllCommitedTrueHealthCheckForm(Long.parseLong(nurseId));
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
    
    @PostMapping("/medical-event")
    public ResponseEntity<MedicalEventDTO> createMedicalEvent(@RequestBody MedicalEventRequest request) {
        String nurseId = SecurityContextHolder.getContext().getAuthentication().getName();
        MedicalEventDTO medicalEventDTO = nurseService.createMedicalEvent(Long.parseLong(nurseId), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(medicalEventDTO);
    }

    @PutMapping("/medical-event/{medicalEventId}")
    public ResponseEntity<MedicalEventDTO> updateMedicalEvent(@PathVariable Long medicalEventId, @RequestBody MedicalEventRequest request) {
        String nurseId = SecurityContextHolder.getContext().getAuthentication().getName();
        MedicalEventDTO medicalEventDTO = nurseService.updateMedicalEvent(Long.parseLong(nurseId), medicalEventId, request);
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
    public ResponseEntity<HealthCheckResultDTO> createHealthCheckResult(@RequestBody HealthCheckResultRequest request) {
        HealthCheckResultDTO healthCheckResultDTO = nurseService.createHealthCheckResult(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(healthCheckResultDTO);
    }
    
    @PutMapping("/health-check-result/{healCheckResultId}")
    public ResponseEntity<HealthCheckResultDTO> putMethodName(@PathVariable Long healCheckResultId, @RequestBody HealthCheckResultRequest request) {
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
    public ResponseEntity<VaccineResultDTO> createVaccineResult(@RequestBody VaccineResultRequest request) {
        VaccineResultDTO vaccineResultDTO = nurseService.createVaccineResult(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(vaccineResultDTO);        
    }

    @PutMapping("/vaccine-result/{vaccineResultId}")
    public ResponseEntity<VaccineResultDTO> updateVaccineResult(@PathVariable Long vaccineResultId, @RequestBody VaccineResultRequest request) {
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

    @PostMapping("/{id}/replyfeeback")
    public ResponseEntity<String> replyToFeedback(
            @PathVariable("id") Integer feedbackId,
            @RequestBody Map<String, String> request
    ) {
        nurseService.replyToFeedback(feedbackId, request.get("response"));
        return ResponseEntity.ok("Replied to feedback.");
    }

    @GetMapping("/getfeedback/{nurseId}")
    public ResponseEntity<List<FeedbackDTO>> getFeedbacksForNurse(@PathVariable Integer nurseId) {
        return ResponseEntity.ok(nurseService.getFeedbacksForNurse(nurseId));
    }

    @GetMapping("/students")
    public ResponseEntity<List<StudentDTO>> getAllStudent() {
        return ResponseEntity.ok(nurseService.getAllStudent());
    }
    
    @GetMapping("/medical-records/{studentId}")
    public ResponseEntity<MedicalRecordDTO> getMedicalRecordsByStudentId(@PathVariable long studentId) {
        MedicalRecordDTO medicalRecordDTO = nurseService.getMedicalRecordByStudentId(studentId);
        if (medicalRecordDTO == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(medicalRecordDTO);
    }

    //Lọc ra nhưng học sinh chưa từng tiêm loại vaccine name history đó hoặc chưa từng tham gia chương trình vaccine
    @GetMapping("/students/not-vaccinated")
    public ResponseEntity<List<StudentDTO>> getStudentsNotVaccinated(
            @RequestParam(required = false) Long vaccineProgramId,
            @RequestParam(required = false) String vaccineName) {
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

}