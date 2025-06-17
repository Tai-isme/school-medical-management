package com.swp391.school_medical_management.modules.users.services.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.swp391.school_medical_management.modules.users.dtos.response.*;
import com.swp391.school_medical_management.modules.users.entities.*;
import com.swp391.school_medical_management.modules.users.repositories.*;
import com.swp391.school_medical_management.service.NotificationService;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.swp391.school_medical_management.modules.users.dtos.request.HealthCheckFormCreateRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.HealthCheckFormUpdateRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.HealthCheckResultRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.MedicalEventRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.VaccineFormCreateRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.VaccineFormUpdateRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.VaccineResultRequest;

@Service
public class NurseService {

    @Autowired private MedicalRequestRepository medicalRequestRepository;

    @Autowired private ModelMapper modelMapper;

    @Autowired private UserRepository userRepository;

    @Autowired private StudentRepository studentRepository;

    @Autowired private HealthCheckProgramRepository healthCheckProgramRepository;

    @Autowired private HealthCheckFormRepository healthCheckFormRepository;

    @Autowired private VaccineProgramRepository vaccineProgramRepository;

    @Autowired private VaccineFormRepository vaccineFormRepository;

    @Autowired private MedicalEventRepository medicalEventRepository;

    @Autowired private HealthCheckResultRepository healthCheckResultRepository;

    @Autowired private VaccineResultRepository vaccineResultRepository;

    @Autowired private FeedbackRepository feedbackRepository;

    public List<MedicalRequestDTO> getPendingMedicalRequest() {
        String status = "PROCESSING";
        List<MedicalRequestEntity> pendingMedicalRequestList = medicalRequestRepository.findMedicalRequestEntitiesByStatusIgnoreCase(status);
        if(pendingMedicalRequestList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No processing medical requests found");
        }
        List<MedicalRequestDTO> medicalRequestDTOList = new ArrayList<>();
        for (MedicalRequestEntity medicalRequestEntity : pendingMedicalRequestList) {
            List<MedicalRequestDetailEntity> medicalRequestDetailEntityList = medicalRequestEntity.getMedicalRequestDetailEntities();
            List<MedicalRequestDetailDTO> medicalRequestDetailDTOList = medicalRequestDetailEntityList.stream()
                    .map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity, MedicalRequestDetailDTO.class))
                    .collect(Collectors.toList());
            MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequestEntity, MedicalRequestDTO.class);
            medicalRequestDTO.setMedicalRequestDetailDTO(medicalRequestDetailDTOList);
            medicalRequestDTOList.add(medicalRequestDTO);
        }
       return medicalRequestDTOList;
    }

    public List<MedicalRequestDTO> getAllMedicalRequest() {
        List<MedicalRequestEntity> medicalRequestEntityList = medicalRequestRepository.findAll();
        if(medicalRequestEntityList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No medical requests found");
        }
        List<MedicalRequestDTO> medicalRequestDTOList = new ArrayList<>();
        for (MedicalRequestEntity medicalRequestEntity : medicalRequestEntityList) {
            StudentEntity studentEntity = medicalRequestEntity.getStudent();
            StudentDTO studentDTO = modelMapper.map(studentEntity, StudentDTO.class);
            List<MedicalRequestDetailEntity> medicalRequestDetailEntityList = medicalRequestEntity.getMedicalRequestDetailEntities();
            List<MedicalRequestDetailDTO> medicalRequestDetailDTOList = medicalRequestDetailEntityList.stream()
                    .map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity, MedicalRequestDetailDTO.class))
                    .collect(Collectors.toList());
            MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequestEntity, MedicalRequestDTO.class);
            medicalRequestDTO.setStudentDTO(studentDTO);
            medicalRequestDTO.setMedicalRequestDetailDTO(medicalRequestDetailDTOList);
            medicalRequestDTOList.add(medicalRequestDTO);
        }
       return medicalRequestDTOList;
    }

    public List<MedicalRequestDetailDTO> getMedicalRequestDetail(int requestId) {
        Optional<MedicalRequestEntity> medicalRequestOpt = medicalRequestRepository.findMedicalRequestEntityByRequestId(requestId);
        if(medicalRequestOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical request not found");
        MedicalRequestEntity medicalRequest = medicalRequestOpt.get();
        List<MedicalRequestDetailEntity> medicalRequestDetailEntityList = medicalRequest.getMedicalRequestDetailEntities();
        if(medicalRequestDetailEntityList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No medical request details found for this request");
        return medicalRequestDetailEntityList.stream()
                .map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity, MedicalRequestDetailDTO.class))
                .collect(Collectors.toList());
    } 

    public MedicalRequestDTO updateMedicalRequestStatus(int requestId, String status) {
        Optional<MedicalRequestEntity> medicalRequestOpt = medicalRequestRepository.findMedicalRequestEntityByRequestId(requestId);
        if(medicalRequestOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical request not found");
        MedicalRequestEntity medicalRequest = medicalRequestOpt.get();
        if(status == null || !status.toUpperCase().equals("SUBMITTED")
                        || !status.toUpperCase().equals("COMPLETED")
                        || !status.toUpperCase().equals("CANCELLED"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        medicalRequest.setStatus(status.toUpperCase());
        medicalRequestRepository.save(medicalRequest);
        return modelMapper.map(medicalRequest, MedicalRequestDTO.class);
    }

    public List<HealthCheckFormDTO> createHealthCheckForm(Long nurseId, HealthCheckFormCreateRequest request) {
        Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        if (nurseOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");
        }

        UserEntity nurse = nurseOpt.get();


        Optional<HealthCheckProgramEntity> healthCheckProgramOpt = 
        healthCheckProgramRepository.findHealthCheckProgramEntityById(request.getHealthCheckProgramId());

        if(healthCheckProgramOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check program not found");
        }

        HealthCheckProgramEntity healthCheckProgramEntity = healthCheckProgramOpt.get();

        if(healthCheckProgramEntity.getStatus().equals("ON_GOING") || healthCheckProgramEntity.getStatus().equals("COMPLETED")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Health check program already started or completed");
        }

        List<HealthCheckFormDTO> result = new ArrayList<>();

        for(Long studentId : request.getStudentIds()){
            Optional<StudentEntity> studentOpt = studentRepository.findStudentById(studentId);
            if(studentOpt.isEmpty())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found" + studentId);
            StudentEntity student = studentOpt.get();
            
            UserEntity parent = student.getParent();
            if(parent == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent not found for student with ID: " + studentId);

            List<HealthCheckFormEntity> existingFormList =
                healthCheckFormRepository.findHealthCheckFormEntityByHealthCheckProgramAndStudent(healthCheckProgramEntity, student);

            boolean hasUncommittedForm = existingFormList.stream()
                .anyMatch(form -> form.getCommit() == null);

            if (hasUncommittedForm) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A health check form has already been submitted but not yet committed by parent for student with ID: " + studentId);
            }

            HealthCheckFormEntity healthCheckFormEntity = new HealthCheckFormEntity();
            healthCheckFormEntity.setStudent(student);
            healthCheckFormEntity.setParent(parent);
            healthCheckFormEntity.setNurse(nurse);
            // healthCheckFormEntity.setFormDate(request.getFormDate());
            healthCheckFormEntity.setFormDate(LocalDate.now());
            healthCheckFormEntity.setNotes(null);
            healthCheckFormEntity.setCommit(null);
            healthCheckFormEntity.setHealthCheckProgram(healthCheckProgramEntity);



            healthCheckFormRepository.save(healthCheckFormEntity);

            result.add(modelMapper.map(healthCheckFormEntity, HealthCheckFormDTO.class));
        }
        return result;
    }

    public Map<String, Object> updateHealthCheckForm(Long nurseId, HealthCheckFormUpdateRequest request) {

        Optional<HealthCheckProgramEntity> healthCheckProgramOpt = healthCheckProgramRepository.findHealthCheckProgramEntityById(request.getHealthCheckProgramId());

        if(healthCheckProgramOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check program not found");

        HealthCheckProgramEntity healthCheckProgramEntity = healthCheckProgramOpt.get();

        if(healthCheckProgramEntity.getStatus().equals("ON_GOING") || healthCheckProgramEntity.getStatus().equals("COMPLETED"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Health check program already started or completed");

        List<HealthCheckFormDTO> successList = new ArrayList<>();
        List<Map<String, Object>> errorList = new ArrayList<>();

        for(Long healthCheckFormId : request.getHealthCheckFormIds()) {
            try{
            Optional<HealthCheckFormEntity> healthCheckFormOpt = healthCheckFormRepository.findById(healthCheckFormId);
            if(healthCheckFormOpt.isEmpty()) 
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check form not found with ID: " + healthCheckFormId);
            
            HealthCheckFormEntity healthCheckFormEntity = healthCheckFormOpt.get();

            if(!healthCheckFormEntity.getNurse().getUserId().equals(nurseId)) 
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied for health check form with ID: " + healthCheckFormId);

            if(healthCheckFormEntity.getCommit() != null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Health check form already committed by parent");

            healthCheckFormEntity.setHealthCheckProgram(healthCheckProgramEntity);
            healthCheckFormEntity.setFormDate(LocalDate.now());
            healthCheckFormRepository.save(healthCheckFormEntity);

            successList.add(modelMapper.map(healthCheckFormEntity, HealthCheckFormDTO.class));

            } catch (ResponseStatusException e) {
                Map<String, Object> error = new HashMap<>();
                error.put("formId", healthCheckFormId);
                error.put("error", e.getReason());
                errorList.add(error);
            }
            
        }
        Map<String, Object> result = new LinkedHashMap();
        result.put("errors", errorList);
        result.put("success", successList);
        return result;
    }

    public List<HealthCheckFormDTO> getAllHealthCheckForms(Long nurseId) {
        Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        if (nurseOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");
        
        List<HealthCheckFormEntity> healthCheckFormEntitieList = healthCheckFormRepository.findAll();

        if (healthCheckFormEntitieList.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No health check forms found");
        
        List<HealthCheckFormDTO> healthCheckFormDTOList = healthCheckFormEntitieList
                .stream()
                .map(healthCheckFormEntity -> modelMapper.map(healthCheckFormEntity, HealthCheckFormDTO.class))
                .collect(Collectors.toList());

        return healthCheckFormDTOList;
    }

    public HealthCheckFormDTO getHealthCheckFormById(Long nurseId, Long healthCheckFormId) {
        Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        if (nurseOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");
        
        Optional<HealthCheckFormEntity> healthCheckFormOpt = healthCheckFormRepository.findById(healthCheckFormId);
        if (healthCheckFormOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check form not found");
        
        HealthCheckFormEntity healthCheckFormEntity = healthCheckFormOpt.get();
        if (!healthCheckFormEntity.getNurse().getUserId().equals(nurseId)) 
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied for health check form ");
        
        return modelMapper.map(healthCheckFormEntity, HealthCheckFormDTO.class);
    }

    public List<HealthCheckFormDTO> getAllCommitedTrueHealthCheckForm(Long nurseId) {
        Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        if (nurseOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");
        
        List<HealthCheckFormEntity> healthCheckFormEntitieList = healthCheckFormRepository.findByCommitTrue();

        if (healthCheckFormEntitieList.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No committed health check forms found");
        
        List<HealthCheckFormDTO> healthCheckFormDTOList = healthCheckFormEntitieList
                .stream()
                .map(healthCheckFormEntity -> modelMapper.map(healthCheckFormEntity, HealthCheckFormDTO.class))
                .collect(Collectors.toList());

        return healthCheckFormDTOList;
    }

    public void deleteHealthCheckForm(Long healthCheckFormId) {
        Optional<HealthCheckFormEntity> healthCheckFormOpt = healthCheckFormRepository.findById(healthCheckFormId);
        if (healthCheckFormOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check form not found");
        // if(healthCheckFormOpt.get().getCommit() != null) 
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Health check form already committed by parent");
        HealthCheckFormEntity healthCheckFormEntity = healthCheckFormOpt.get();
        healthCheckFormRepository.delete(healthCheckFormEntity);
    }

    public List<VaccineFormDTO> createVaccineForm(Long nurseId, VaccineFormCreateRequest request){
        Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        if (nurseOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");
        }

        UserEntity nurse = nurseOpt.get();


        Optional<VaccineProgramEntity> vaccineProgramOpt = 
        vaccineProgramRepository.findVaccineProgramByVaccineId(request.getVaccineProgramId());

        if(vaccineProgramOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine program not found");
        }

        VaccineProgramEntity vaccineProgramEntity = vaccineProgramOpt.get();

        if(vaccineProgramEntity.getStatus().equals("ON_GOING") || vaccineProgramEntity.getStatus().equals("COMPLETED")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vaccine program already started or completed");
        }

        List<VaccineFormDTO> result = new ArrayList<>();

        for(Long studentId : request.getStudentIds()){
            Optional<StudentEntity> studentOpt = studentRepository.findStudentById(studentId);
            if(studentOpt.isEmpty())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found" + studentId);

            StudentEntity student = studentOpt.get();
            
            UserEntity parent = student.getParent();
            if(parent == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent not found for student with ID: " + studentId);

            Optional<VaccineFormEntity> existingFormOpt = vaccineFormRepository.findVaccineFormEntityByVaccineProgramAndStudent(vaccineProgramEntity, student);
            
            if(existingFormOpt.isPresent()) {
                if(existingFormOpt.get().getCommit() != null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vaccine form already committed by parent for student with ID: " + studentId);
                }
            }

            VaccineFormEntity vaccineFormEntity = new VaccineFormEntity();

            vaccineFormEntity.setStudent(student);
            vaccineFormEntity.setNote(null);
            vaccineFormEntity.setCommit(null);
            vaccineFormEntity.setNurse(nurse);
            vaccineFormEntity.setFormDate(LocalDate.now());
            vaccineFormEntity.setParent(parent);
            vaccineFormEntity.setVaccineProgram(vaccineProgramEntity);
            vaccineFormRepository.save(vaccineFormEntity);

            result.add(modelMapper.map(vaccineFormEntity, VaccineFormDTO.class));
        }
        return result;
    }

    public Map<String, Object> updateVaccineForm(Long nurseId, VaccineFormUpdateRequest request){
         Optional<VaccineProgramEntity> vaccineProgramOpt = vaccineProgramRepository.findVaccineProgramByVaccineId(request.getVaccineProgramId());

        if(vaccineProgramOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine program not found");

        VaccineProgramEntity vaccineProgramEntity = vaccineProgramOpt.get();

        if(vaccineProgramEntity.getStatus().equals("ON_GOING") || vaccineProgramEntity.getStatus().equals("COMPLETED"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vaccine program already started or completed");

        List<VaccineFormDTO> successList = new ArrayList<>();
        List<Map<String, Object>> errorList = new ArrayList<>();

        for(Long vaccineFormId : request.getVaccineFormIds()) {
            try{
            Optional<VaccineFormEntity> vaccineFormOpt = vaccineFormRepository.findVaccineFormEntityByvaccineFormId(vaccineFormId);
            if(vaccineFormOpt.isEmpty()) 
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine form not found with ID: " + vaccineFormId);
            
            VaccineFormEntity vaccineFormEntity = vaccineFormOpt.get();

            if(!vaccineFormEntity.getNurse().getUserId().equals(nurseId)) 
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied for vaccine form with ID: " + vaccineFormId);

            if(vaccineFormEntity.getCommit() != null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vaccine form already committed by parent");

            vaccineFormEntity.setVaccineProgram(vaccineProgramEntity);
            vaccineFormEntity.setFormDate(LocalDate.now());
            vaccineFormRepository.save(vaccineFormEntity);

            successList.add(modelMapper.map(vaccineFormEntity, VaccineFormDTO.class));

            } catch (ResponseStatusException e) {
                Map<String, Object> error = new HashMap<>();
                error.put("formId", vaccineFormId);
                error.put("error", e.getReason());
                errorList.add(error);
            }
            
        }
        Map<String, Object> result = new LinkedHashMap();
        result.put("errors", errorList);
        result.put("success", successList);
        return result;
    }

    public List<VaccineFormDTO> getAllVaccinForm(Long nurseId){
        Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        if (nurseOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");
        List<VaccineFormEntity> vaccineFormEntitieList = vaccineFormRepository.findAll();
        if(vaccineFormEntitieList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No vaccine forms found");
        List<VaccineFormDTO> vaccineFormDTOList = vaccineFormEntitieList
                .stream()
                .map(vaccineFormEntity -> modelMapper.map(vaccineFormEntity, VaccineFormDTO.class))
                .collect(Collectors.toList());
        return vaccineFormDTOList;
    }

    public VaccineFormDTO getVaccinFormById(Long nurseId, Long vaccineFormId){
        Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        if (nurseOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");
        Optional<VaccineFormEntity> vaccineFormOpt = vaccineFormRepository.findVaccineFormEntityByvaccineFormId(vaccineFormId);
        if(vaccineFormOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine form not found");
        VaccineFormEntity vaccineFormEntity = vaccineFormOpt.get();
        if(!vaccineFormEntity.getNurse().getUserId().equals(nurseId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied for vaccine form");
        return modelMapper.map(vaccineFormEntity, VaccineFormDTO.class);
    }

    public List<VaccineFormDTO> getAllCommitedTrueVaccineForm(Long nurseId){
        Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        if (nurseOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");
        List<VaccineFormEntity> vaccineFormEntitieList = vaccineFormRepository.findByCommitTrue();
        if(vaccineFormEntitieList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No commited vaccine forms found");
        List<VaccineFormDTO> vaccineFormDTOList = vaccineFormEntitieList
                .stream()
                .map(vaccineFormEntity -> modelMapper.map(vaccineFormEntity, VaccineFormDTO.class))
                .collect(Collectors.toList());
        return vaccineFormDTOList;
    }

    public void deleteVaccineForm(long vaccineFormId){
        Optional<VaccineFormEntity> vaccineFormOpt = vaccineFormRepository.findVaccineFormEntityByvaccineFormId(vaccineFormId);
        if(vaccineFormOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine form not found");
            
        VaccineFormEntity vaccineFormEntity = vaccineFormOpt.get();
        vaccineFormRepository.delete(vaccineFormEntity);
    }

    public MedicalEventDTO createMedicalEvent(Long nurseId, MedicalEventRequest request) {
        Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        if (nurseOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");
    
        UserEntity nurse = nurseOpt.get();

        Optional<StudentEntity> studentOpt = studentRepository.findStudentById(request.getStudentId());
        if (studentOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        
        StudentEntity student = studentOpt.get();

        Optional<MedicalEventEntity> medicalEventOpt = medicalEventRepository.findByStudentAndTypeEventAndDescription(student, request.getTypeEvent(), request.getDescription());
        if (medicalEventOpt.isPresent()) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medical event already exists");

        MedicalEventEntity medicalEventEntity = new MedicalEventEntity();
        medicalEventEntity.setTypeEvent(request.getTypeEvent());
        medicalEventEntity.setDate(LocalDate.now());
        medicalEventEntity.setDescription(request.getDescription());
        medicalEventEntity.setStudent(student);
        medicalEventEntity.setNurse(nurse);

        medicalEventRepository.save(medicalEventEntity);

        return modelMapper.map(medicalEventEntity, MedicalEventDTO.class);
    }

    public MedicalEventDTO updateMedicalEvent(Long nurseId, Long medicalEventId, MedicalEventRequest request){
        Optional<MedicalEventEntity> medicalEventOpt = medicalEventRepository.findByEventId(medicalEventId);
        if (medicalEventOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical event not found");

        MedicalEventEntity medicalEventEntity = medicalEventOpt.get();

        Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        if (nurseOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");
        
        UserEntity nurse = nurseOpt.get();

        Optional<StudentEntity> studentOpt = studentRepository.findStudentById(request.getStudentId());
        if (studentOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        
        StudentEntity student = studentOpt.get();

        medicalEventEntity.setTypeEvent(request.getTypeEvent()); 
        medicalEventEntity.setDescription(request.getDescription());
        medicalEventEntity.setStudent(student);
        medicalEventEntity.setDate(LocalDate.now());
        medicalEventEntity.setNurse(nurse);

        medicalEventRepository.save(medicalEventEntity);

        return modelMapper.map(medicalEventEntity, MedicalEventDTO.class);
    }

    public MedicalEventDTO getMedicalEvent(Long medicalEventId){
        Optional<MedicalEventEntity> medicalEventOpt = medicalEventRepository.findByEventId(medicalEventId);
        if (medicalEventOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical event not found");
        MedicalEventEntity medicalEventEntity = medicalEventOpt.get();
        return modelMapper.map(medicalEventEntity, MedicalEventDTO.class);
    }

    public List<MedicalEventDTO> getAllMedicalEvent(){
        List<MedicalEventEntity> medicalEventOpt = medicalEventRepository.findAll();
        if(medicalEventOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical event not found");
        List<MedicalEventDTO> medicalEventDTOList = medicalEventOpt
                .stream()
                .map(medicalEventEntity -> modelMapper.map(medicalEventEntity, MedicalEventDTO.class))
                .collect(Collectors.toList());
        return medicalEventDTOList;
    }

    public void deleteMedicalEvent(Long meidcalEventId){
        Optional<MedicalEventEntity> medicalEventOpt = medicalEventRepository.findByEventId(meidcalEventId);
        if (medicalEventOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical event not found");
        MedicalEventEntity medicalEventEntity = medicalEventOpt.get();
        medicalEventRepository.delete(medicalEventEntity);
    }

    public HealthCheckResultDTO createHealthCheckResult(HealthCheckResultRequest request) {

        Optional<HealthCheckFormEntity> healCheckFormOpt = healthCheckFormRepository.findById(request.getHealthCheckFormId());
        if (healCheckFormOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check form not found");
        HealthCheckFormEntity healthCheckFormEntity = healCheckFormOpt.get();

        if (healthCheckFormEntity.getCommit() == null || !healthCheckFormEntity.getCommit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent has not committed the health check form yet");
        }
        
        HealthCheckFormDTO healthCheckFormDTO = modelMapper.map(healthCheckFormEntity, HealthCheckFormDTO.class);

        Optional<HealthCheckResultEntity> existingResultOpt = healthCheckResultRepository.findByHealthCheckFormEntity(healthCheckFormEntity);
        if (existingResultOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Health check result already exists for this student: " + healthCheckFormEntity.getStudent().getId());
        }

        HealthCheckResultEntity healthCheckResultEntity = new HealthCheckResultEntity();
        healthCheckResultEntity.setDiagnosis(request.getDiagnosis());
        healthCheckResultEntity.setLevel(request.getLevel());
        healthCheckResultEntity.setNote(request.getNote());
        healthCheckResultEntity.setHealthCheckFormEntity(healthCheckFormEntity);
        healthCheckResultRepository.save(healthCheckResultEntity);

        HealthCheckResultDTO healthCheckResultDTO = modelMapper.map(healthCheckResultEntity, HealthCheckResultDTO.class);
        healthCheckResultDTO.setHealthCheckFormId(healthCheckFormDTO.getId());
        healthCheckResultDTO.setStudentId(healthCheckFormDTO.getStudentId());

        return healthCheckResultDTO;
    }

    public HealthCheckResultDTO updateHealthCheckResult(Long healCheckResultId, HealthCheckResultRequest request) {

        Optional<HealthCheckResultEntity> existingResultOpt = healthCheckResultRepository.findByHealthResultId(healCheckResultId);
        if (existingResultOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check result not found");
        }

        Optional<HealthCheckFormEntity> healCheckFormOpt = healthCheckFormRepository.findById(request.getHealthCheckFormId());
        if (healCheckFormOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check form not found");
            
        HealthCheckFormEntity healthCheckFormEntity = healCheckFormOpt.get();

        if (healthCheckFormEntity.getCommit() == null || !healthCheckFormEntity.getCommit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent has not committed the health check form yet");
        }

        HealthCheckFormDTO healthCheckFormDTO = modelMapper.map(healthCheckFormEntity, HealthCheckFormDTO.class);
        HealthCheckResultEntity healthCheckResultEntity = existingResultOpt.get();

        healthCheckResultEntity.setDiagnosis(request.getDiagnosis());
        healthCheckResultEntity.setLevel(request.getLevel());
        healthCheckResultEntity.setNote(request.getNote());
        healthCheckResultRepository.save(healthCheckResultEntity);

        HealthCheckResultDTO healthCheckResultDTO = modelMapper.map(healthCheckResultEntity, HealthCheckResultDTO.class);
        healthCheckResultDTO.setHealthCheckFormId(healthCheckFormDTO.getId());
        healthCheckResultDTO.setStudentId(healthCheckFormDTO.getStudentId());

        return healthCheckResultDTO;
    }

    public HealthCheckResultDTO getHealthCheckResult(Long healCheckResultId) {
        Optional<HealthCheckResultEntity> healthCheckResultOpt = healthCheckResultRepository.findByHealthResultId(healCheckResultId);
        if (healthCheckResultOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check result not found");
            
        HealthCheckResultEntity healthCheckResultEntity = healthCheckResultOpt.get();
        HealthCheckResultDTO healthCheckResultDTO = modelMapper.map(healthCheckResultEntity, HealthCheckResultDTO.class);

        HealthCheckFormEntity healthCheckFormEntity = healthCheckResultEntity.getHealthCheckFormEntity();
        HealthCheckFormDTO healthCheckFormDTO = modelMapper.map(healthCheckFormEntity, HealthCheckFormDTO.class);

        healthCheckResultDTO.setHealthCheckFormId(healthCheckFormDTO.getId());
        healthCheckResultDTO.setStudentId(healthCheckFormDTO.getStudentId());

        return healthCheckResultDTO;
    }

    public List<HealthCheckResultDTO> getAllHealthCheckResult(){
        List<HealthCheckResultEntity> healthCheckResultEntityList = healthCheckResultRepository.findAll();
        if(healthCheckResultEntityList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No health check result found");
        List<HealthCheckResultDTO> healthCheckResultDTOList = healthCheckResultEntityList
            .stream()
            .map(healthCheckResultEntity -> {
                HealthCheckResultDTO dto = modelMapper.map(healthCheckResultEntity, HealthCheckResultDTO.class);
                dto.setStudentId(healthCheckResultEntity.getHealthCheckFormEntity().getStudent().getId());
                dto.setHealthCheckFormId(healthCheckResultEntity.getHealthCheckFormEntity().getId());
                return dto;
            }).collect(Collectors.toList());

        return healthCheckResultDTOList;
    }
    
    public void deleteHealthCheckResult(Long healthCheckResultId){
        Optional<HealthCheckResultEntity> healthCheckResultOpt = healthCheckResultRepository.findByHealthResultId(healthCheckResultId);
        if (healthCheckResultOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check result not found");
        HealthCheckResultEntity healthCheckResultEntity = healthCheckResultOpt.get();
        healthCheckResultRepository.delete(healthCheckResultEntity);
    }

    public VaccineResultDTO createVaccineResult(VaccineResultRequest request) {
        Optional<VaccineFormEntity> vaccineFormOpt = vaccineFormRepository.findById(request.getVaccineFormId());
        if (vaccineFormOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine form not found");
        VaccineFormEntity vaccineFormEntity = vaccineFormOpt.get();

        if (vaccineFormEntity.getCommit() == null || !vaccineFormEntity.getCommit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent has not committed the vaccine form yet");
        }

        VaccineFormDTO vaccineFormDTO = modelMapper.map(vaccineFormEntity, VaccineFormDTO.class);

        Optional<VaccineResultEntity> existingResultOpt = vaccineResultRepository.findByVaccineFormEntity(vaccineFormEntity);
        if (existingResultOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vaccine result already exists for this student: " + vaccineFormEntity.getStudent().getId());
        }

        VaccineResultEntity vaccineResultEntity = new VaccineResultEntity();
        vaccineResultEntity.setStatusHealth(request.getStatusHealth());
        vaccineResultEntity.setResultNote(request.getResultNote());
        vaccineResultEntity.setReaction(request.getReaction());
        vaccineResultEntity.setCreatedAt(LocalDateTime.now());
        vaccineResultEntity.setVaccineFormEntity(vaccineFormEntity);
        vaccineResultRepository.save(vaccineResultEntity);

        VaccineResultDTO vaccineResultDTO = modelMapper.map(vaccineResultEntity, VaccineResultDTO.class);
        vaccineResultDTO.setVaccineFormId(vaccineFormDTO.getId());
        vaccineResultDTO.setStudentId(vaccineFormDTO.getStudentId());
        return vaccineResultDTO;
    }

    public VaccineResultDTO updateVaccineResult(Long vaccineResultId, VaccineResultRequest request) {
        Optional<VaccineResultEntity> existingResultOpt = vaccineResultRepository.findById(vaccineResultId);
        if (existingResultOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine result not found");
        }

        Optional<VaccineFormEntity> vaccineFormOpt = vaccineFormRepository.findVaccineFormEntityByvaccineFormId(request.getVaccineFormId());
        if (vaccineFormOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine form not found");
            
        VaccineFormEntity vaccineFormEntity = vaccineFormOpt.get();

        if (vaccineFormEntity.getCommit() == null || !vaccineFormEntity.getCommit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent has not committed the vaccine form yet");
        }

        VaccineFormDTO vaccineFormDTO = modelMapper.map(vaccineFormEntity, VaccineFormDTO.class);
        VaccineResultEntity vaccineResultEntity = existingResultOpt.get();

        vaccineResultEntity.setStatusHealth(request.getStatusHealth());
        vaccineResultEntity.setResultNote(request.getResultNote());
        vaccineResultEntity.setReaction(request.getReaction());
        vaccineResultEntity.setCreatedAt(LocalDateTime.now());
        vaccineResultEntity.setVaccineFormEntity(vaccineFormEntity);
        vaccineResultRepository.save(vaccineResultEntity);

        VaccineResultDTO vaccineResultDTO = modelMapper.map(vaccineResultEntity, VaccineResultDTO.class);
        vaccineResultDTO.setVaccineFormId(vaccineFormDTO.getId());
        vaccineResultDTO.setStudentId(vaccineFormDTO.getStudentId());

        return vaccineResultDTO;
    }

    public VaccineResultDTO getVaccineResult(Long vaccineResultId) {
        Optional<VaccineResultEntity> vaccineResultOpt = vaccineResultRepository.findById(vaccineResultId);
        if (vaccineResultOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine result not found");
        VaccineResultEntity vaccineResultEntity = vaccineResultOpt.get();
        VaccineResultDTO vaccineResultDTO = modelMapper.map(vaccineResultEntity, VaccineResultDTO.class);

        VaccineFormEntity vaccineFormEntity = vaccineResultEntity.getVaccineFormEntity();
        VaccineFormDTO vaccineFormDTO = modelMapper.map(vaccineFormEntity, VaccineFormDTO.class);

        vaccineResultDTO.setVaccineFormId(vaccineFormDTO.getId());
        vaccineResultDTO.setStudentId(vaccineFormDTO.getStudentId());

        return vaccineResultDTO;
    }

    public List<VaccineResultDTO> getAllVaccineResult() {
        List<VaccineResultEntity> vaccineResultEntityList = vaccineResultRepository.findAll();
        if (vaccineResultEntityList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No vaccine result found");
        List<VaccineResultDTO> vaccineResultDTOList = vaccineResultEntityList
            .stream()
            .map(vaccineResultEntity -> {
                VaccineResultDTO dto = modelMapper.map(vaccineResultEntity, VaccineResultDTO.class);
                dto.setStudentId(vaccineResultEntity.getVaccineFormEntity().getStudent().getId());
                dto.setVaccineFormId(vaccineResultEntity.getVaccineFormEntity().getVaccineFormId());
                return dto;
            }).collect(Collectors.toList());
        
        return vaccineResultDTOList;
    }

    public void deleteVaccineResult(Long vaccineResultId){
        Optional<VaccineResultEntity> vaccineResultOpt = vaccineResultRepository.findById(vaccineResultId);
        if (vaccineResultOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine result not found");
        VaccineResultEntity vaccineResultEntity = vaccineResultOpt.get();
        vaccineResultRepository.delete(vaccineResultEntity);
    }

    public void replyToFeedback(Integer feedbackId, String response) {
        FeedbackEntity feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DON'T FIND TO RESPONSE."));
        if ("REPLIED".equalsIgnoreCase(feedback.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RESPONSE WAS REPLIED");
        }
        feedback.setResponseNurse(response);
        feedback.setStatus("REPLIED");
        feedbackRepository.save(feedback);
    }

    public List<FeedbackDTO> getFeedbacksForNurse(Integer nurseId) {
        UserEntity nurse = userRepository.findById(nurseId.longValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NURSE NOT FOUND."));
        List<FeedbackEntity> feedbackList = feedbackRepository.findByNurse(nurse);

        List<FeedbackDTO> feedbackDTOList = feedbackList.stream().map(feedback -> modelMapper.map(feedback, FeedbackDTO.class)).collect(Collectors.toList());
        
        return feedbackDTOList;
    }
}

