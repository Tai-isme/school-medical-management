package com.swp391.school_medical_management.modules.users.services.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.swp391.school_medical_management.modules.users.dtos.request.BlogRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.HealthCheckResultRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.MedicalEventRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.VaccineResultRequest;
import com.swp391.school_medical_management.modules.users.dtos.response.BlogResponse;
import com.swp391.school_medical_management.modules.users.dtos.response.ClassDTO;
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
import com.swp391.school_medical_management.modules.users.entities.BlogEntity;
import com.swp391.school_medical_management.modules.users.entities.FeedbackEntity;
import com.swp391.school_medical_management.modules.users.entities.FeedbackEntity.FeedbackStatus;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckFormEntity;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckResultEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalEventEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalRecordEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalRequestDetailEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalRequestEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalRequestEntity.MedicalRequestStatus;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity.UserRole;
import com.swp391.school_medical_management.modules.users.entities.VaccineFormEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineHistoryEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineProgramEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineResultEntity;
import com.swp391.school_medical_management.modules.users.repositories.BlogRepository;
import com.swp391.school_medical_management.modules.users.repositories.FeedbackRepository;
import com.swp391.school_medical_management.modules.users.repositories.HealthCheckFormRepository;
import com.swp391.school_medical_management.modules.users.repositories.HealthCheckResultRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalEventRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalRecordsRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalRequestRepository;
import com.swp391.school_medical_management.modules.users.repositories.StudentRepository;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineFormRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineHistoryRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineResultRepository;

@Service
public class NurseService {

    public static final String DEFAULT_VACCINE_HS_NOTE = "Chương trình vaccine tại trường!";

    @Autowired
    private MedicalRequestRepository medicalRequestRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private HealthCheckFormRepository healthCheckFormRepository;

    @Autowired
    private VaccineFormRepository vaccineFormRepository;

    @Autowired
    private MedicalEventRepository medicalEventRepository;

    @Autowired
    private HealthCheckResultRepository healthCheckResultRepository;

    @Autowired
    private VaccineResultRepository vaccineResultRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private MedicalRecordsRepository medicalRecordsRepository;

    @Autowired
    private VaccineHistoryRepository vaccineHistoryRepository;

    @Autowired
    private BlogRepository blogRepository;

    public List<MedicalRequestDTO> getPendingMedicalRequest() {
        List<MedicalRequestEntity> pendingMedicalRequestList = medicalRequestRepository
                .findByStatus(MedicalRequestStatus.PROCESSING);
        if (pendingMedicalRequestList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No processing medical requests found");
        }
        List<MedicalRequestDTO> medicalRequestDTOList = new ArrayList<>();
        for (MedicalRequestEntity medicalRequestEntity : pendingMedicalRequestList) {
            List<MedicalRequestDetailEntity> medicalRequestDetailEntityList = medicalRequestEntity
                    .getMedicalRequestDetailEntities();
            List<MedicalRequestDetailDTO> medicalRequestDetailDTOList = medicalRequestDetailEntityList.stream()
                    .map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity,
                            MedicalRequestDetailDTO.class))
                    .collect(Collectors.toList());
            MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequestEntity, MedicalRequestDTO.class);
            medicalRequestDTO.setMedicalRequestDetailDTO(medicalRequestDetailDTOList);
            medicalRequestDTOList.add(medicalRequestDTO);
        }
        return medicalRequestDTOList;
    }

    public List<MedicalRequestDTO> getAllMedicalRequest() {
        List<MedicalRequestEntity> medicalRequestEntityList = medicalRequestRepository.findAll();
        if (medicalRequestEntityList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No medical requests found");
        }
        List<MedicalRequestDTO> medicalRequestDTOList = new ArrayList<>();
        for (MedicalRequestEntity medicalRequestEntity : medicalRequestEntityList) {
            StudentEntity studentEntity = medicalRequestEntity.getStudent();
            StudentDTO studentDTO = modelMapper.map(studentEntity, StudentDTO.class);
            List<MedicalRequestDetailEntity> medicalRequestDetailEntityList = medicalRequestEntity
                    .getMedicalRequestDetailEntities();
            List<MedicalRequestDetailDTO> medicalRequestDetailDTOList = medicalRequestDetailEntityList.stream()
                    .map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity,
                            MedicalRequestDetailDTO.class))
                    .collect(Collectors.toList());
            MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequestEntity, MedicalRequestDTO.class);
            medicalRequestDTO.setStudentDTO(studentDTO);
            medicalRequestDTO.setMedicalRequestDetailDTO(medicalRequestDetailDTOList);
            medicalRequestDTOList.add(medicalRequestDTO);
        }
        return medicalRequestDTOList;
    }

    public List<MedicalRequestDetailDTO> getMedicalRequestDetail(int requestId) {
        Optional<MedicalRequestEntity> medicalRequestOpt = medicalRequestRepository
                .findMedicalRequestEntityByRequestId(requestId);
        if (medicalRequestOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical request not found");
        MedicalRequestEntity medicalRequest = medicalRequestOpt.get();
        List<MedicalRequestDetailEntity> medicalRequestDetailEntityList = medicalRequest
                .getMedicalRequestDetailEntities();
        if (medicalRequestDetailEntityList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No medical request details found for this request");
        return medicalRequestDetailEntityList.stream()
                .map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity,
                        MedicalRequestDetailDTO.class))
                .collect(Collectors.toList());
    }

    public MedicalRecordDTO getMedicalRecordByStudentId(Long studentId) {
        Optional<MedicalRecordEntity> optMedicalRecord = medicalRecordsRepository
                .findMedicalRecordByStudent_Id(studentId);
        if (optMedicalRecord.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found");
        MedicalRecordEntity medicalRecord = optMedicalRecord.get();
        return modelMapper.map(medicalRecord, MedicalRecordDTO.class);
    }

    public List<MedicalEventDTO> getMedicalEventsByStudent(Long studentId) {
        Optional<StudentEntity> studentOpt = studentRepository.findStudentById(studentId);
        if (studentOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");

        StudentEntity studentEntity = studentOpt.get();

        List<MedicalEventEntity> medicalEventEntitieList = medicalEventRepository.findByStudent(studentEntity);
        if (medicalEventEntitieList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found any medical event");

        return medicalEventEntitieList.stream()
            .map(event -> {
                MedicalEventDTO dto = modelMapper.map(event, MedicalEventDTO.class);

                if (event.getNurse() != null) {
                    UserDTO nurseDTO = modelMapper.map(event.getNurse(), UserDTO.class);
                    dto.setUserDTO(nurseDTO);
                }

                return dto;
            })
            .collect(Collectors.toList());
    }


    public MedicalRequestDTO updateMedicalRequestStatus(int requestId, String status) {
        Optional<MedicalRequestEntity> medicalRequestOpt = medicalRequestRepository
                .findMedicalRequestEntityByRequestId(requestId);
        if (medicalRequestOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical request not found");
        MedicalRequestEntity medicalRequest = medicalRequestOpt.get();

        MedicalRequestEntity.MedicalRequestStatus statusEnum = MedicalRequestEntity.MedicalRequestStatus
                .valueOf(status.toUpperCase());

        if (statusEnum == null || !(statusEnum == MedicalRequestStatus.SUBMITTED)
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

    public VaccineFormDTO getVaccinFormById(Long nurseId, Long vaccineFormId) {
        Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        if (nurseOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");
        Optional<VaccineFormEntity> vaccineFormOpt = vaccineFormRepository.findById(vaccineFormId);
        if (vaccineFormOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine form not found");
        VaccineFormEntity vaccineFormEntity = vaccineFormOpt.get();
        return modelMapper.map(vaccineFormEntity, VaccineFormDTO.class);
    }

    public List<VaccineFormDTO> getAllCommitedTrueVaccineForm(Long nurseId) {
        Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        if (nurseOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");
        List<VaccineFormEntity> vaccineFormEntitieList = vaccineFormRepository.findByCommitTrue();
        if (vaccineFormEntitieList.isEmpty())
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

    Optional<MedicalEventEntity> medicalEventOpt = medicalEventRepository
            .findByStudentAndTypeEventAndDescription(student, request.getTypeEvent(), request.getDescription());
    if (medicalEventOpt.isPresent())
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medical event already exists");

    MedicalEventEntity medicalEventEntity = new MedicalEventEntity();
    medicalEventEntity.setTypeEvent(request.getTypeEvent());
    medicalEventEntity.setDate(LocalDate.now());
    medicalEventEntity.setDescription(request.getDescription());
    medicalEventEntity.setStudent(student);
    medicalEventEntity.setNurse(nurse);

    medicalEventRepository.save(medicalEventEntity);

    MedicalEventDTO dto = modelMapper.map(medicalEventEntity, MedicalEventDTO.class);
    dto.setStudentId(student.getId());
    dto.setStudentName(student.getFullName());

    UserEntity parent = student.getParent();
    if (parent != null) {
        UserDTO userDTO = UserDTO.builder()
                .id(parent.getUserId())
                .fullName(parent.getFullName())
                .email(parent.getEmail())
                .phone(parent.getPhone())
                .address(parent.getAddress())
                .role(parent.getRole())
                .build();

        dto.setUserDTO(userDTO);
    }

    return dto;
}


    public MedicalEventDTO updateMedicalEvent(Long nurseId, Long medicalEventId, MedicalEventRequest request) {
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

    public MedicalEventDTO getMedicalEvent(Long medicalEventId) {
        Optional<MedicalEventEntity> medicalEventOpt = medicalEventRepository.findByEventId(medicalEventId);
        if (medicalEventOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical event not found");
        MedicalEventEntity medicalEventEntity = medicalEventOpt.get();
        return modelMapper.map(medicalEventEntity, MedicalEventDTO.class);
    }

    public List<MedicalEventDTO> getAllMedicalEvent() {
        List<MedicalEventEntity> events = medicalEventRepository.findAll();

        if (events.isEmpty()) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical event not found");
        }

        List<MedicalEventDTO> medicalEventDTOList = new ArrayList<>();

    for (MedicalEventEntity event : events) {
        MedicalEventDTO dto = new MedicalEventDTO();
        dto.setEventId(event.getEventId());
        dto.setTypeEvent(event.getTypeEvent());
        dto.setDate(event.getDate());
        dto.setDescription(event.getDescription());

        StudentEntity student = event.getStudent();
        if (student != null) {
            dto.setStudentId(student.getId());
            dto.setStudentName(student.getFullName());

            UserEntity parent = student.getParent();
            if (parent != null) {
                UserDTO userDTO = UserDTO.builder()
                        .id(parent.getUserId())
                        .fullName(parent.getFullName())
                        .email(parent.getEmail())
                        .phone(parent.getPhone()) 
                        .address(parent.getAddress())
                        .role(parent.getRole())
                        .build();

                dto.setUserDTO(userDTO);
            }
        }
        medicalEventDTOList.add(dto);
    }
    return medicalEventDTOList;
}


    public void deleteMedicalEvent(Long meidcalEventId) {
        Optional<MedicalEventEntity> medicalEventOpt = medicalEventRepository.findByEventId(meidcalEventId);
        if (medicalEventOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical event not found");
        MedicalEventEntity medicalEventEntity = medicalEventOpt.get();
        medicalEventRepository.delete(medicalEventEntity);
    }

    public HealthCheckResultDTO createHealthCheckResult(HealthCheckResultRequest request) {

        Optional<HealthCheckFormEntity> healCheckFormOpt = healthCheckFormRepository
                .findById(request.getHealthCheckFormId());
        if (healCheckFormOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check form not found");
        HealthCheckFormEntity healthCheckFormEntity = healCheckFormOpt.get();

        if (healthCheckFormEntity.getCommit() == null || !healthCheckFormEntity.getCommit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Parent has not committed the health check form yet");
        }

        HealthCheckFormDTO healthCheckFormDTO = modelMapper.map(healthCheckFormEntity, HealthCheckFormDTO.class);

        Optional<HealthCheckResultEntity> existingResultOpt = healthCheckResultRepository
                .findByHealthCheckFormEntity(healthCheckFormEntity);
        if (existingResultOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Health check result already exists for this student: "
                            + healthCheckFormEntity.getStudent().getId());
        }

        HealthCheckResultEntity healthCheckResultEntity = new HealthCheckResultEntity();
        healthCheckResultEntity.setDiagnosis(request.getDiagnosis());
        healthCheckResultEntity.setLevel(HealthCheckResultEntity.Level.valueOf(request.getLevel().toUpperCase()));
        healthCheckResultEntity.setNote(request.getNote());
        healthCheckResultEntity.setHealthCheckFormEntity(healthCheckFormEntity);
        healthCheckResultRepository.save(healthCheckResultEntity);

        HealthCheckResultDTO healthCheckResultDTO = modelMapper.map(healthCheckResultEntity,
                HealthCheckResultDTO.class);
        healthCheckResultDTO.setHealthCheckFormId(healthCheckFormDTO.getId());
        healthCheckResultDTO.setStudentId(healthCheckFormDTO.getStudentId());

        return healthCheckResultDTO;
    }

    public HealthCheckResultDTO updateHealthCheckResult(Long healCheckResultId, HealthCheckResultRequest request) {

        Optional<HealthCheckResultEntity> existingResultOpt = healthCheckResultRepository
                .findByHealthResultId(healCheckResultId);
        if (existingResultOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check result not found");
        }

        Optional<HealthCheckFormEntity> healCheckFormOpt = healthCheckFormRepository
                .findById(request.getHealthCheckFormId());
        if (healCheckFormOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check form not found");

        HealthCheckFormEntity healthCheckFormEntity = healCheckFormOpt.get();

        if (healthCheckFormEntity.getCommit() == null || !healthCheckFormEntity.getCommit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Parent has not committed the health check form yet");
        }

        HealthCheckFormDTO healthCheckFormDTO = modelMapper.map(healthCheckFormEntity, HealthCheckFormDTO.class);
        HealthCheckResultEntity healthCheckResultEntity = existingResultOpt.get();

        healthCheckResultEntity.setDiagnosis(request.getDiagnosis());
        healthCheckResultEntity.setLevel(HealthCheckResultEntity.Level.valueOf(request.getLevel().toUpperCase()));
        healthCheckResultEntity.setNote(request.getNote());
        healthCheckResultRepository.save(healthCheckResultEntity);

        HealthCheckResultDTO healthCheckResultDTO = modelMapper.map(healthCheckResultEntity,
                HealthCheckResultDTO.class);
        healthCheckResultDTO.setHealthCheckFormId(healthCheckFormDTO.getId());
        healthCheckResultDTO.setStudentId(healthCheckFormDTO.getStudentId());

        return healthCheckResultDTO;
    }

    public HealthCheckResultDTO getHealthCheckResult(Long healCheckResultId) {
        Optional<HealthCheckResultEntity> healthCheckResultOpt = healthCheckResultRepository
                .findByHealthResultId(healCheckResultId);
        if (healthCheckResultOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check result not found");

        HealthCheckResultEntity healthCheckResultEntity = healthCheckResultOpt.get();
        HealthCheckResultDTO healthCheckResultDTO = modelMapper.map(healthCheckResultEntity,
                HealthCheckResultDTO.class);

        HealthCheckFormEntity healthCheckFormEntity = healthCheckResultEntity.getHealthCheckFormEntity();
        HealthCheckFormDTO healthCheckFormDTO = modelMapper.map(healthCheckFormEntity, HealthCheckFormDTO.class);

        healthCheckResultDTO.setHealthCheckFormId(healthCheckFormDTO.getId());
        healthCheckResultDTO.setStudentId(healthCheckFormDTO.getStudentId());

        return healthCheckResultDTO;
    }

    public List<HealthCheckResultDTO> getAllHealthCheckResult() {
        List<HealthCheckResultEntity> healthCheckResultEntityList = healthCheckResultRepository.findAll();
        if (healthCheckResultEntityList.isEmpty())
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

    public void deleteHealthCheckResult(Long healthCheckResultId) {
        Optional<HealthCheckResultEntity> healthCheckResultOpt = healthCheckResultRepository
                .findByHealthResultId(healthCheckResultId);
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

        Optional<VaccineResultEntity> existingResultOpt = vaccineResultRepository
                .findByVaccineFormEntity(vaccineFormEntity);
        if (existingResultOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Vaccine result already exists for this student: " + vaccineFormEntity.getStudent().getId());
        }

        Optional<MedicalRecordEntity> medicalRecordOpt = medicalRecordsRepository
                .findMedicalRecordByStudent_Id(vaccineFormDTO.getStudentId());
        if (medicalRecordOpt.isEmpty())
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

    public void deleteVaccineResult(Long vaccineResultId) {
        Optional<VaccineResultEntity> vaccineResultOpt = vaccineResultRepository.findById(vaccineResultId);
        if (vaccineResultOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine result not found");
        VaccineResultEntity vaccineResultEntity = vaccineResultOpt.get();
        vaccineResultRepository.delete(vaccineResultEntity);
    }

    public List<StudentDTO> getAllStudent() {
        List<StudentEntity> studentEntityList = studentRepository.findAll();
        if (studentEntityList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No students found");

        List<StudentDTO> studentDTOList = studentEntityList.stream().map(student -> {
            StudentDTO dto = modelMapper.map(student, StudentDTO.class);
            if (student.getClassEntity() != null) {
                ClassDTO classDTO = modelMapper.map(student.getClassEntity(), ClassDTO.class);
                dto.setClassDTO(classDTO);
            }
            if (student.getParent() != null) {
                UserDTO userDTO = modelMapper.map(student.getParent(), UserDTO.class);
                dto.setUserDTO(userDTO);
            }
            return dto;
        }).collect(Collectors.toList());
        return studentDTOList;
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

        List<FeedbackDTO> feedbackDTOList = feedbackList.stream()
                .map(feedback -> modelMapper.map(feedback, FeedbackDTO.class)).collect(Collectors.toList());

        return feedbackDTOList;
    }

    public List<StudentDTO> getStudentsNotVaccinated(Long vaccineProgramId, String vaccineName) {
        if (vaccineName != null && vaccineName.trim().isEmpty())
            vaccineName = null;

        List<StudentEntity> students = studentRepository
                .findStudentsNeverVaccinatedByProgramOrName(vaccineProgramId, vaccineName);

        return students.stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    private String toSlug(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");
    }

    public BlogResponse create(BlogRequest request) {
        // Lấy thông tin người viết
        UserEntity author = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Tạo bài viết mới
        BlogEntity post = new BlogEntity();
        post.setTitle(request.getTitle());
        post.setSlug(toSlug(request.getTitle()));
        post.setCategory(request.getCategory());
        post.setContent(request.getContent());
        post.setAuthor(author);

        // Lưu vào database
        BlogEntity saved = blogRepository.save(post);

        return toResponse(saved);
    }

    public List<BlogResponse> getAll() {
        List<BlogEntity> posts = blogRepository.findAll();
        return posts.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public BlogResponse getBySlug(String slug) {
        BlogEntity post = blogRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        return toResponse(post);
    }

    public BlogResponse update(Long id, BlogRequest request) {
        BlogEntity post = blogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        UserEntity author = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        post.setTitle(request.getTitle());
        post.setSlug(toSlug(request.getTitle()));
        post.setCategory(request.getCategory());
        post.setContent(request.getContent());
        post.setAuthor(author);

        BlogEntity updated = blogRepository.save(post);
        return toResponse(updated);
    }

    public void delete(Long id) {
        blogRepository.deleteById(id);
    }

    private BlogResponse toResponse(BlogEntity post) {
        BlogResponse response = modelMapper.map(post, BlogResponse.class);
        response.setUserId(post.getAuthor().getUserId());
        return response;
    }

    public List<UserDTO> searchUsers(String keyword, UserRole roleFilter) {
    // Lấy toàn bộ danh sách user từ database
    List<UserEntity> userList = userRepository.findAll();

    // Lọc bỏ user có role là ADMIN và theo điều kiện keyword/roleFilter
    List<UserDTO> result = userList.stream()
            .filter(user -> user.getRole() != UserEntity.UserRole.ADMIN)
            .filter(user -> {
                boolean matchesKeyword = true;
                if (keyword != null && !keyword.isBlank()) {
                    String lowerKeyword = keyword.toLowerCase();
                    matchesKeyword = (user.getFullName() != null && user.getFullName().toLowerCase().contains(lowerKeyword))
                            || (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerKeyword))
                            || (user.getPhone() != null && user.getPhone().toLowerCase().contains(lowerKeyword));
                }
                boolean matchesRole = (roleFilter == null || user.getRole() == roleFilter);
                return matchesKeyword && matchesRole;
            })
            .map(user -> modelMapper.map(user, UserDTO.class))
            .collect(Collectors.toList());

    if (result.isEmpty()) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No users found");
    }

    return result;
}

    public List<StudentDTO> getStudentsByClass(Long classId) {
        List<StudentEntity> students = studentRepository.findByClassEntity_ClassId(classId);
        return students.stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }


    public List<MedicalRecordDTO> searchMedicalRecordsByStudentName(String keyword) {
    List<StudentEntity> students = studentRepository.findByFullNameContainingIgnoreCase(keyword);

    List<MedicalRecordDTO> medicalRecords = new ArrayList<>();

    for (StudentEntity student : students) {
        Optional<MedicalRecordEntity> recordOpt = medicalRecordsRepository
                .findMedicalRecordByStudent_Id(student.getId());

        recordOpt.ifPresent(record -> {
            MedicalRecordDTO dto = modelMapper.map(record, MedicalRecordDTO.class);
            dto.setStudentId(student.getId());
            medicalRecords.add(dto);
        });
    }

        return medicalRecords;
    }

    public List<MedicalRequestDTO> getByStatus(MedicalRequestEntity.MedicalRequestStatus status) {
        List<MedicalRequestEntity> entities = medicalRequestRepository.findByStatus(status);
        return entities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MedicalRequestDTO> getByDate(LocalDate date) {
        List<MedicalRequestEntity> entities = medicalRequestRepository.findAll()
                .stream()
                .filter(r -> r.getDate().isEqual(date))
                .collect(Collectors.toList());
        return entities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MedicalRequestDTO> getByClass(Long classId) {
        List<MedicalRequestEntity> entities = medicalRequestRepository.findAll()
                .stream()
                .filter(r -> r.getStudent().getClassEntity().getClassId() == classId)
                .collect(Collectors.toList());
        return entities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MedicalRequestDTO> searchByDateRange(LocalDate from, LocalDate to) {
        List<MedicalRequestEntity> entities = medicalRequestRepository.findAll()
                .stream()
                .filter(r -> !r.getDate().isBefore(from) && !r.getDate().isAfter(to))
                .collect(Collectors.toList());
        return entities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void updateStatus(int requestId, MedicalRequestEntity.MedicalRequestStatus newStatus) {
        MedicalRequestEntity entity = medicalRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Request not found"));
        entity.setStatus(newStatus);
        medicalRequestRepository.save(entity);
    }

    private MedicalRequestDTO convertToDTO(MedicalRequestEntity entity) {
        MedicalRequestDTO dto = modelMapper.map(entity, MedicalRequestDTO.class);
        dto.setStatus(entity.getStatus().name());
        dto.setParentId(entity.getParent().getUserId());
        return dto;
    }

}
