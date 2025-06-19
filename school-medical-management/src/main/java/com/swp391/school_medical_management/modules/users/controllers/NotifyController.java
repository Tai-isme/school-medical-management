package com.swp391.school_medical_management.modules.users.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.swp391.school_medical_management.modules.users.dtos.request.NotifyToParentRequest;
import com.swp391.school_medical_management.modules.users.dtos.response.NotificationMessageDTO;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckFormEntity;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineFormEntity;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckFormEntity.HealthCheckFormStatus;
import com.swp391.school_medical_management.modules.users.entities.VaccineFormEntity.VaccineFormStatus;
import com.swp391.school_medical_management.modules.users.repositories.HealthCheckFormRepository;
import com.swp391.school_medical_management.modules.users.repositories.StudentRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineFormRepository;
import com.swp391.school_medical_management.service.NotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api")
public class NotifyController {

    @Autowired NotificationService notificationService;

    @Autowired StudentRepository studentRepository;

    @Autowired HealthCheckFormRepository healthCheckFormRepository;

    @Autowired VaccineFormRepository vaccineFormRepository;

    @PostMapping("/notify-health-check")
    public ResponseEntity<?> notifyHealthCheckToParents(@RequestBody NotifyToParentRequest request ) {
        List<Long> formIds = request.getFormIds();
        for (Long formId : formIds) {

            Optional<HealthCheckFormEntity> healthCheckFormOpt = healthCheckFormRepository.findById(formId);
            if(healthCheckFormOpt.isEmpty())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not found health check form with id: " + formId);

            HealthCheckFormEntity healthCheckFormEntity = healthCheckFormOpt.get();
            
            StudentEntity studentEntity = healthCheckFormEntity.getStudent();
            if(studentEntity == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found in form");

            UserEntity parentEntity = studentEntity.getParent();

            if(parentEntity == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent not found with student ID: " + studentEntity.getId());

            if(healthCheckFormEntity.getStatus() == HealthCheckFormStatus.SENT)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This form already sent");

            notificationService.sendNotificationToParent(
                parentEntity.getUserId(),
                "Thông báo chương trình khám sức khỏe định kỳ",
                "Bạn có phiếu thông báo khám sức khỏe định kỳ mới cần xác nhận.",
                formId
            );
           
            healthCheckFormEntity.setStatus(HealthCheckFormStatus.SENT);
            healthCheckFormRepository.save(healthCheckFormEntity);
        }
        return ResponseEntity.ok("Đã gửi thông báo");
    }

    @PostMapping("/notify-vaccine")
    public ResponseEntity<?> notifyVaccineToParents(@RequestBody NotifyToParentRequest request ) {
        List<Long> formIds = request.getFormIds();
        for (Long formId : formIds) {

            Optional<VaccineFormEntity> vaccineFormOpt = vaccineFormRepository.findById(formId);
            if(vaccineFormOpt.isEmpty())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not found vaccine form with id: " + formId);

            VaccineFormEntity vaccineFormEntity = vaccineFormOpt.get();
            
            StudentEntity studentEntity = vaccineFormEntity.getStudent();
            if(studentEntity == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found in form");

            UserEntity parentEntity = studentEntity.getParent();

            if(parentEntity == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent not found with student ID: " + studentEntity.getId());
            
            if(vaccineFormEntity.getStatus() == VaccineFormStatus.SENT)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This form already sent");

            notificationService.sendNotificationToParent(
                parentEntity.getUserId(),
                "Thông báo chương trình tiêm vaccine cho học sinh " + parentEntity.getFullName(),
                "Bạn có phiếu thông báo tiêm chủng vaccine mới cần xác nhận.",
                formId
            );

            vaccineFormEntity.setStatus(VaccineFormStatus.SENT);
            vaccineFormRepository.save(vaccineFormEntity);
        }
        return ResponseEntity.ok("Đã gửi thông báo");
    }

    @GetMapping("/notify/{studentId}")
    public ResponseEntity<List<NotificationMessageDTO>> getAllNotify(@PathVariable Long studentId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<NotificationMessageDTO> notificationMessageDTOList = notificationService.getNotificationByUserId(Long.parseLong(parentId));
        return ResponseEntity.ok(notificationMessageDTOList);
    }
    
}