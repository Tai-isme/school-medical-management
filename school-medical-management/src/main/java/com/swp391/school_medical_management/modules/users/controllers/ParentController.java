package com.swp391.school_medical_management.modules.users.controllers;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.swp391.school_medical_management.modules.users.dtos.request.CommitHealthCheckFormRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.CommitVaccineFormRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.FeedbackRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.MedicalRecordsRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.MedicalRequest;
import com.swp391.school_medical_management.modules.users.dtos.response.AllFormsByStudentDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.FeedbackDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckFormDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckResultDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalEventDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRecordDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRequestDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineFormDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineNameDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineResultDTO;
import com.swp391.school_medical_management.modules.users.services.impl.NurseService;
import com.swp391.school_medical_management.modules.users.services.impl.ParentService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("api/parent")
@PreAuthorize("hasRole('ROLE_PARENT')")
public class ParentController {

    @Autowired
    private ParentService parentService;

    @Autowired
    private NurseService nurseService;

    /**
     * Medical record for a student.
     *
     * @param medicalRecordsRequest the request containing medical record details
     * @return ResponseEntity with the created MedicalRecordDTO
     */

    @PostMapping("/medical-records")
    public ResponseEntity<MedicalRecordDTO> createMedicalRecords(
            @RequestBody MedicalRecordsRequest medicalRecordsRequest) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        MedicalRecordDTO medicalRecordDTO = parentService.createMedicalRecord(Integer.parseInt(parentId), medicalRecordsRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(medicalRecordDTO);
    }

    @PutMapping("/medical-records/{studentId}")
    public ResponseEntity<MedicalRecordDTO> updateMedicalRecords(
            @RequestBody MedicalRecordsRequest medicalRecordsRequest) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        MedicalRecordDTO medicalRecordDTO = parentService.updateMedicalRecord(Integer.parseInt(parentId), medicalRecordsRequest);
        return ResponseEntity.ok(medicalRecordDTO);
    }

    @GetMapping("/medical-records")
    public ResponseEntity<List<MedicalRecordDTO>> getAllMedicalRecords() {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<MedicalRecordDTO> medicalRecordDTOList = parentService.getAllMedicalRecordByParentId(Integer.parseInt(parentId));
        if (medicalRecordDTOList.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(medicalRecordDTOList);
    }

    @GetMapping("/medical-records/{studentId}")
//    @PreAuthorize("hasAnyRole('ROLE_PARENT', 'ROLE_ADMIN', 'ROLE_NURSE')")
    public ResponseEntity<MedicalRecordDTO> getMedicalRecordsByStudentId(@PathVariable int studentId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        MedicalRecordDTO medicalRecordDTO = parentService.getMedicalRecordByStudentId(Integer.parseInt(parentId), studentId);
        if (medicalRecordDTO == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(medicalRecordDTO);
    }

    @DeleteMapping("/medical-records/{studentId}")
    public ResponseEntity<Void> deleteMedicalRecords(@PathVariable int studentId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        parentService.deleteMedicalRecord(Integer.parseInt(parentId), studentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/medical-request", consumes = { "multipart/form-data" })
    public ResponseEntity<MedicalRequestDTO> createMedicalRequest(
            @RequestPart("request") String requestJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        MedicalRequest request;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); 
            request = mapper.readValue(requestJson, MedicalRequest.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid request format: " + e.getMessage());
        }
        MedicalRequestDTO medicalRequestDTO = parentService.createMedicalRequest(Integer.parseInt(parentId), request,
                image);
        return ResponseEntity.status(HttpStatus.CREATED).body(medicalRequestDTO);
    }


    @GetMapping("/medical-request/by-request/{requestId}")
    public ResponseEntity<MedicalRequestDTO> getMedicalRequestByRequestId(@PathVariable int requestId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        MedicalRequestDTO medicalRequestDTO = parentService.getMedicalRequestByRequestId(Integer.parseInt(parentId),
                requestId);
        return ResponseEntity.ok(medicalRequestDTO);
    }

    @GetMapping("/medical-request/by-student/{studentId}")
    public ResponseEntity<List<MedicalRequestDTO>> getMedicalRequestByStudent(@PathVariable int studentId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<MedicalRequestDTO> medicalRequestDtoList = parentService.getMedicalRequestByStudent(Integer.parseInt(parentId), studentId);
        return ResponseEntity.ok(medicalRequestDtoList);
    }

    @GetMapping("/medical-request")
    public ResponseEntity<List<MedicalRequestDTO>> getMedicalRequestByParent() {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<MedicalRequestDTO> medicalRequestDtoList = parentService
                .getMedicalRequestByParent(Integer.parseInt(parentId));
        return ResponseEntity.ok(medicalRequestDtoList);
    }

    @PutMapping("/medical-request/{requestId}")
    public ResponseEntity<MedicalRequestDTO> updateMedicalRequest(@Valid @RequestBody MedicalRequest request,
                                                                  @PathVariable int requestId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        MedicalRequestDTO medicalRequestDTO = parentService.updateMedicalRequest(Integer.parseInt(parentId), request,
                requestId);
        return ResponseEntity.ok(medicalRequestDTO);
    }

    @DeleteMapping("/medical-request/{requestId}")
    public ResponseEntity<Void> deleteMedicalRequest(@PathVariable int requestId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        parentService.deleteMedicalRequest(Integer.parseInt(parentId), requestId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health-check-forms/student/{studentId}")
    public ResponseEntity<List<HealthCheckFormDTO>> getAllHealthCheckForm(@PathVariable int studentId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<HealthCheckFormDTO> healthCheckFormDTOList = parentService.getAllHealthCheckFormCommited(Integer.parseInt(parentId),
                studentId);
        return ResponseEntity.ok(healthCheckFormDTOList);
    }

    @GetMapping("/vaccine-forms/student/{studentId}")
    public ResponseEntity<List<VaccineFormDTO>> getAllVaccineForm(@PathVariable int studentId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<VaccineFormDTO> vaccineFormDTOList = parentService.getAllVaccineFormCommited(Integer.parseInt(parentId), studentId);
        return ResponseEntity.ok(vaccineFormDTOList);
    }

    @GetMapping("/health-check-forms/form/{healthCheckFormId}")
    public ResponseEntity<HealthCheckFormDTO> getHealthCheckForm(@PathVariable int healthCheckFormId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        HealthCheckFormDTO healthCheckFormDTO = parentService.getHealthCheckForm(Integer.parseInt(parentId),
                healthCheckFormId);
        return ResponseEntity.ok(healthCheckFormDTO);
    }

    @GetMapping("/vaccine-forms/form/{vaccineFormId}")
    public ResponseEntity<VaccineFormDTO> getVaccineForm(@PathVariable int vaccineFormId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        VaccineFormDTO vaccineFormDTO = parentService.getVaccineForm(Integer.parseInt(parentId), vaccineFormId);
        return ResponseEntity.ok(vaccineFormDTO);
    }

    @PatchMapping("/health-check-forms/{healCheckFormId}/commit")
    public ResponseEntity<String> commitHealthCheckForm(@RequestBody CommitHealthCheckFormRequest request,
            @PathVariable int healCheckFormId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        parentService.commitHealthCheckForm(Integer.parseInt(parentId), healCheckFormId, request);
        return ResponseEntity.ok("Xác nhận thành công");
    }


    @PatchMapping("/vaccine-forms/{vaccineFormId}/commit")
    public ResponseEntity<Void> commitVaccineForm(@RequestBody CommitVaccineFormRequest request,
                                                  @PathVariable int vaccineFormId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        parentService.commitVaccineForm(Integer.parseInt(parentId), vaccineFormId, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/feedback/submit")
    public ResponseEntity<String> submitFeedback(@RequestBody FeedbackRequest request) {
        parentService.submitFeedback(request);
        return ResponseEntity.ok("SUCCESSFULLY SUBMITTED");
    }

    @GetMapping("/getfeedback/{parentId}")
    public ResponseEntity<List<FeedbackDTO>> getParentFeedbacks(@PathVariable int parentId) {
        return ResponseEntity.ok(parentService.getFeedbacksByParent(parentId));
    }

    @GetMapping("/medical-events/{studentId}")
    public ResponseEntity<List<MedicalEventDTO>> getMedicalEventsByStudent(@PathVariable int studentId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<MedicalEventDTO> events = parentService.getMedicalEventsByStudent(Integer.parseInt(parentId), studentId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/all-forms/{studentId}")
    @PreAuthorize("hasAnyRole('ROLE_PARENT', 'ROLE_NURSE')")
    public ResponseEntity<AllFormsByStudentDTO> getAllFormByStudent(@PathVariable int studentId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        AllFormsByStudentDTO allFormsByStudentDTOList = parentService.getAllFormByStudent(Integer.parseInt(parentId),
                studentId);
        return ResponseEntity.ok(allFormsByStudentDTOList);
    }

    @GetMapping("/vaccine-result/student/{studentId}")
    public ResponseEntity<List<VaccineResultDTO>> getVaccineResult(@PathVariable int studentId) {
        List<VaccineResultDTO> vaccineResultDTOs = parentService.getVaccineResults(studentId);
        return ResponseEntity.ok(vaccineResultDTOs);
    }

    @GetMapping("/health-check-result/student/{studentId}")
    public ResponseEntity<List<HealthCheckResultDTO>> getHealthCheckResultsByStudent(@PathVariable int studentId) {
        List<HealthCheckResultDTO> resultList = parentService.getHealthCheckResults(studentId);
        return ResponseEntity.ok(resultList);
    }

    @GetMapping("/health-check-result/form/{formId}")
    public ResponseEntity<HealthCheckResultDTO> getHealthCheckResultByFormId(@PathVariable int formId) {
        HealthCheckResultDTO dto = parentService.getHealthCheckResultByFormId(formId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/vaccine-result/form/{formId}")
    public ResponseEntity<VaccineResultDTO> getVaccineResultByFormId(@PathVariable int formId) {
        VaccineResultDTO dto = parentService.getVaccineResultByFormId(formId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/health-check-result/{healCheckResultId}")
    public ResponseEntity<HealthCheckResultDTO> getHealthCheckResultByResultID(@PathVariable int healCheckResultId) {
        HealthCheckResultDTO healthCheckResultDTO = nurseService.getHealthCheckResult(healCheckResultId);
        return ResponseEntity.ok(healthCheckResultDTO);
    }

    @GetMapping("/vaccine-result/{vaccineResultId}")
    public ResponseEntity<VaccineResultDTO> getVaccineResultByResultID(@PathVariable int vaccineResultId) {
        VaccineResultDTO vaccineResultDTO = nurseService.getVaccineResult(vaccineResultId);
        return ResponseEntity.ok(vaccineResultDTO);
    }

    @GetMapping("/get=all-VaccineName")
    public ResponseEntity<List<VaccineNameDTO>> getAllVaccineNames() {
        List<VaccineNameDTO> list = parentService.getAllVaccineNames();
        return ResponseEntity.ok(list);
    }
}
