package com.swp391.school_medical_management.modules.users.services.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.swp391.school_medical_management.modules.users.dtos.response.*;
import com.swp391.school_medical_management.modules.users.entities.*;
import com.swp391.school_medical_management.modules.users.entities.FeedbackEntity.FeedbackStatus;
import com.swp391.school_medical_management.modules.users.entities.MedicalRequestEntity.MedicalRequestStatus;
import com.swp391.school_medical_management.modules.users.repositories.*;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.swp391.school_medical_management.modules.users.dtos.request.HealthCheckResultRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.MedicalEventRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.VaccineResultRequest;

@Service
public class NurseService {

    public static final String DEFAULT_VACCINE_HS_NOTE = "Chương trình vaccine tại trường!";

    @Autowired private MedicalRequestRepository medicalRequestRepository;

    @Autowired private ModelMapper modelMapper;

    @Autowired private UserRepository userRepository;

    @Autowired private StudentRepository studentRepository;

    @Autowired private HealthCheckFormRepository healthCheckFormRepository;

    @Autowired private VaccineFormRepository vaccineFormRepository;

    @Autowired private MedicalEventRepository medicalEventRepository;

    @Autowired private HealthCheckResultRepository healthCheckResultRepository;

    @Autowired private VaccineResultRepository vaccineResultRepository;

    @Autowired private FeedbackRepository feedbackRepository;

    @Autowired private MedicalRecordsRepository medicalRecordsRepository;

    @Autowired private VaccineHistoryRepository vaccineHistoryRepository;

    public List<MedicalRequestDTO> getPendingMedicalRequest() {
        List<MedicalRequestEntity> pendingMedicalRequestList = medicalRequestRepository.findByStatus(MedicalRequestStatus.PROCESSING);
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

        MedicalRequestEntity.MedicalRequestStatus statusEnum = MedicalRequestEntity.MedicalRequestStatus.valueOf(status.toUpperCase());

        if(statusEnum == null || !(statusEnum == MedicalRequestStatus.SUBMITTED)
                        || !(statusEnum == MedicalRequestStatus.COMPLETED)
                        || !(statusEnum == MedicalRequestStatus.CANCELLED))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        medicalRequest.setStatus(statusEnum);
        medicalRequestRepository.save(medicalRequest);
        return modelMapper.map(medicalRequest, MedicalRequestDTO.class);
    }

    public HealthCheckFormDTO getHealthCheckFormById(Long nurseId, Long healthCheckFormId) {
        Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        if (nurseOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");
        
        Optional<HealthCheckFormEntity> healthCheckFormOpt = healthCheckFormRepository.findById(healthCheckFormId);
        if (healthCheckFormOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check form not found");
        
        HealthCheckFormEntity healthCheckFormEntity = healthCheckFormOpt.get();
        
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

    public VaccineFormDTO getVaccinFormById(Long nurseId, Long vaccineFormId){
        Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        if (nurseOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");
        Optional<VaccineFormEntity> vaccineFormOpt = vaccineFormRepository.findById(vaccineFormId);
        if(vaccineFormOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine form not found");
        VaccineFormEntity vaccineFormEntity = vaccineFormOpt.get();
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
        healthCheckResultEntity.setLevel(HealthCheckResultEntity.Level.valueOf(request.getLevel().toUpperCase()));
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
        healthCheckResultEntity.setLevel(HealthCheckResultEntity.Level.valueOf(request.getLevel().toUpperCase()));
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

        Optional<MedicalRecordEntity> medicalRecordOpt = medicalRecordsRepository.findMedicalRecordByStudent_Id(vaccineFormDTO.getStudentId());
        if(medicalRecordOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not found medical record by student");
        MedicalRecordEntity medicalRecordEntity = medicalRecordOpt.get();

        VaccineResultEntity vaccineResultEntity = new VaccineResultEntity();
        vaccineResultEntity.setStatusHealth(request.getStatusHealth());
        vaccineResultEntity.setResultNote(request.getResultNote());
        vaccineResultEntity.setReaction(request.getReaction());
        vaccineResultEntity.setCreatedAt(LocalDateTime.now());
        vaccineResultEntity.setVaccineFormEntity(vaccineFormEntity);
        vaccineResultRepository.save(vaccineResultEntity);

        VaccineProgramEntity program = vaccineFormEntity.getVaccineProgram();

        VaccineHistoryEntity history = new VaccineHistoryEntity();
        history.setVaccineName(program.getVaccineName());
        history.setNote(DEFAULT_VACCINE_HS_NOTE);
        history.setMedicalRecord(medicalRecordEntity);
        history.setVaccineProgram(program);
        vaccineHistoryRepository.save(history);

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

        Optional<VaccineFormEntity> vaccineFormOpt = vaccineFormRepository.findById(request.getVaccineFormId());
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
                dto.setVaccineFormId(vaccineResultEntity.getVaccineFormEntity().getId());
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
        
        if (feedback.getStatus() == FeedbackStatus.REPLIED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RESPONSE WAS REPLIED");
        }
        feedback.setResponseNurse(response);
        feedback.setStatus(FeedbackStatus.REPLIED);
        feedbackRepository.save(feedback);
    }

    public List<FeedbackDTO> getFeedbacksForNurse(Integer nurseId) {
        UserEntity nurse = userRepository.findById(nurseId.longValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NURSE NOT FOUND."));
        List<FeedbackEntity> feedbackList = feedbackRepository.findByNurse(nurse);

        List<FeedbackDTO> feedbackDTOList = feedbackList.stream().map(feedback -> modelMapper.map(feedback, FeedbackDTO.class)).collect(Collectors.toList());
        
        return feedbackDTOList;
    }

    public List<StudentDTO> getStudentsNotVaccinated(Long vaccineProgramId, String vaccineName) {
        if (vaccineName != null && vaccineName.trim().isEmpty()) vaccineName = null;

        List<StudentEntity> students = studentRepository
                .findStudentsNeverVaccinatedByProgramOrName(vaccineProgramId, vaccineName);

        return students.stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

}

