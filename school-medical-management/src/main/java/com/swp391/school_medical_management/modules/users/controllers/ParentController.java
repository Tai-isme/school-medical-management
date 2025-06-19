package com.swp391.school_medical_management.modules.users.controllers;

import com.swp391.school_medical_management.modules.users.dtos.request.*;
import com.swp391.school_medical_management.modules.users.dtos.response.FeedbackDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckFormDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalEventDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRecordDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRequestDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineFormDTO;
import com.swp391.school_medical_management.modules.users.services.impl.ParentService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;





@Validated
@RestController
@RequestMapping("api/parent")
@PreAuthorize("hasRole('ROLE_PARENT')")
public class ParentController {
    
    @Autowired
    private ParentService parentService;

    /**
     * Medical record for a student.
     * @param medicalRecordsRequest the request containing medical record details
     * @return ResponseEntity with the created MedicalRecordDTO
     */

    @PostMapping("/medical-records")
    public ResponseEntity<MedicalRecordDTO> createMedicalRecords(@RequestBody MedicalRecordsRequest medicalRecordsRequest) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        MedicalRecordDTO medicalRecordDTO = parentService.createMedicalRecord(Long.parseLong(parentId), medicalRecordsRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(medicalRecordDTO);
    }

    @PutMapping("/medical-records/{studentId}")
    public ResponseEntity<MedicalRecordDTO> updateMedicalRecords(@RequestBody MedicalRecordsRequest medicalRecordsRequest) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        MedicalRecordDTO medicalRecordDTO = parentService.updateMedicalRecord(Long.parseLong(parentId), medicalRecordsRequest);
        return ResponseEntity.ok(medicalRecordDTO);
    }

    @GetMapping("/medical-records")
    public ResponseEntity<List<MedicalRecordDTO>> getAllMedicalRecords() {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<MedicalRecordDTO> medicalRecordDTOList= parentService.getAllMedicalRecordByParentId(Long.parseLong(parentId));
        if(medicalRecordDTOList.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(medicalRecordDTOList);
    }
    
    @GetMapping("/medical-records/{studentId}")
    public ResponseEntity<MedicalRecordDTO> getMedicalRecordsByStudentId(@PathVariable long studentId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        MedicalRecordDTO medicalRecordDTO = parentService.getMedicalRecordByStudentId(Long.parseLong(parentId), studentId);
        if (medicalRecordDTO == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(medicalRecordDTO);
    }
    
    @DeleteMapping("/medical-records/{studentId}")
    public ResponseEntity<Void> deleteMedicalRecords(@PathVariable long studentId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        parentService.deleteMedicalRecord(Long.parseLong(parentId), studentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Medical request for a student.
     * @param request the request containing medical request details
     * @return ResponseEntity with the created MedicalRequestDTO
     */

    @PostMapping("/medical-request")
    public ResponseEntity<MedicalRequestDTO> createMedicalRequest(@RequestBody MedicalRequest request) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        MedicalRequestDTO medicalRequestDTO = parentService.createMedicalRequest(Long.parseLong(parentId), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(medicalRequestDTO);
    }

    @GetMapping("/medical-request/by-request/{requestId}")
    public ResponseEntity<MedicalRequestDTO> getMedicalRequestByRequestId(@PathVariable int requestId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        MedicalRequestDTO medicalRequestDTO = parentService.getMedicalRequestByRequestId(Long.parseLong(parentId) ,requestId);
        return ResponseEntity.ok(medicalRequestDTO);
    }

    @GetMapping("/medical-request/by-student/{studentId}")
    public ResponseEntity<List<MedicalRequestDTO>> getMedicalRequestByStudent(@PathVariable Long studentId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<MedicalRequestDTO> medicalRequestDtoList = parentService.getMedicalRequestByStudent(Long.parseLong(parentId) ,studentId);
        return ResponseEntity.ok(medicalRequestDtoList);
    }

    @GetMapping("/medical-request")
    public ResponseEntity<List<MedicalRequestDTO>> getMedicalRequestByParent(){
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<MedicalRequestDTO> medicalRequestDtoList = parentService.getMedicalRequestByParent(Long.parseLong(parentId));
        return ResponseEntity.ok(medicalRequestDtoList);
    }

    @PutMapping("/medical-request/{requestId}")
    public ResponseEntity<MedicalRequestDTO> updateMedicalRequest(@Valid @RequestBody MedicalRequest request, @PathVariable int requestId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        MedicalRequestDTO medicalRequestDTO = parentService.updateMedicalRequest(Long.parseLong(parentId), request, requestId);
        return ResponseEntity.ok(medicalRequestDTO);
    }
    
    @DeleteMapping("/medical-request/{requestId}")
    public ResponseEntity<Void> deleteMedicalRequest(@PathVariable int requestId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        parentService.deleteMedicalRequest(Long.parseLong(parentId), requestId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health-check-forms/student/{studentId}")
    public ResponseEntity<List<HealthCheckFormDTO>> getAllHealthCheckForm(@PathVariable Long studentId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<HealthCheckFormDTO> healthCheckFormDTOList = parentService.getAllHealthCheckForm(Long.parseLong(parentId), studentId);
        return ResponseEntity.ok(healthCheckFormDTOList);
    }

    @GetMapping("/vaccine-forms/student/{studentId}")
    public ResponseEntity<List<VaccineFormDTO>> getAllVaccineForm(@PathVariable Long studentId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<VaccineFormDTO> vaccineFormDTOList = parentService.getAllVaccineForm(Long.parseLong(parentId), studentId);
        return ResponseEntity.ok(vaccineFormDTOList);
    }

    @GetMapping("/health-check-forms/form/{healthCheckFormId}")
    public ResponseEntity<HealthCheckFormDTO> getHealthCheckForm(@PathVariable Long healthCheckFormId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        HealthCheckFormDTO healthCheckFormDTO = parentService.getHealthCheckForm(Long.parseLong(parentId), healthCheckFormId);
        return ResponseEntity.ok(healthCheckFormDTO);
    }

    @GetMapping("/vaccine-forms/form/{vaccineFormId}")
    public ResponseEntity<VaccineFormDTO> getVaccineForm(@PathVariable Long vaccineFormId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        VaccineFormDTO vaccineFormDTO = parentService.getVaccineForm(Long.parseLong(parentId), vaccineFormId);
        return ResponseEntity.ok(vaccineFormDTO);
    }
    

    @PatchMapping("/health-check-forms/{healCheckFormId}/commit")
    public ResponseEntity<Void> commitHealthCheckForm(@RequestBody CommitHealthCheckFormRequest request, @PathVariable Long healCheckFormId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        parentService.commitHealthCheckForm(Long.parseLong(parentId), healCheckFormId, request);        
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/vaccine-forms/{vaccineFormId}/commit")
    public ResponseEntity<Void> commitVaccineForm(@RequestBody CommitVaccineFormRequest request, @PathVariable Long vaccineFormId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        parentService.commitVaccineForm(Long.parseLong(parentId), vaccineFormId, request);        
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/feedback/submit")
    public ResponseEntity<String> submitFeedback(@RequestBody FeedbackRequest request) {
        parentService.submitFeedback(request);
        return ResponseEntity.ok("SUCCESSFULLY SUBMITTED");
    }

    @GetMapping("/getfeedback/{parentId}")
    public ResponseEntity<List<FeedbackDTO>> getParentFeedbacks(@PathVariable Long parentId) {
        return ResponseEntity.ok(parentService.getFeedbacksByParent(parentId));
    }


    @GetMapping("/medical-events/{studentId}")
    public ResponseEntity<List<MedicalEventDTO>> getMedicalEventsByStudent(@PathVariable Long studentId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<MedicalEventDTO> events = parentService.getMedicalEventsByStudent(Long.parseLong(parentId) ,studentId);
        return ResponseEntity.ok(events);
    }
}
