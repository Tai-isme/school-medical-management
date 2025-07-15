package com.swp391.school_medical_management.modules.users.services.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.swp391.school_medical_management.modules.users.dtos.request.BlogRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.HealthCheckResultRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.MedicalEventRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.ReplyFeedbackRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.UpdateMedicalRequestStatus;
import com.swp391.school_medical_management.modules.users.dtos.request.VaccineResultRequest;
import com.swp391.school_medical_management.modules.users.dtos.response.BlogResponse;
import com.swp391.school_medical_management.modules.users.dtos.response.ClassDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.ClassStudentDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.FeedbackDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckFormDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckProgramDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckResultDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalEventDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRecordDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRequestDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRequestDetailDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.OnGoingProgramDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.StudentDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.UserDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineFormDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineProgramDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineResultDTO;
import com.swp391.school_medical_management.modules.users.entities.BlogEntity;
import com.swp391.school_medical_management.modules.users.entities.ClassEntity;
import com.swp391.school_medical_management.modules.users.entities.FeedbackEntity;
import com.swp391.school_medical_management.modules.users.entities.FeedbackEntity.FeedbackStatus;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckFormEntity;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckFormEntity.HealthCheckFormStatus;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckProgramEntity;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckProgramEntity.HealthCheckProgramStatus;
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
import com.swp391.school_medical_management.modules.users.entities.VaccineFormEntity.VaccineFormStatus;
import com.swp391.school_medical_management.modules.users.entities.VaccineHistoryEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineNameEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineProgramEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineProgramEntity.VaccineProgramStatus;
import com.swp391.school_medical_management.modules.users.entities.VaccineResultEntity;
import com.swp391.school_medical_management.modules.users.repositories.BlogRepository;
import com.swp391.school_medical_management.modules.users.repositories.ClassRepository;
import com.swp391.school_medical_management.modules.users.repositories.FeedbackRepository;
import com.swp391.school_medical_management.modules.users.repositories.HealthCheckFormRepository;
import com.swp391.school_medical_management.modules.users.repositories.HealthCheckProgramRepository;
import com.swp391.school_medical_management.modules.users.repositories.HealthCheckResultRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalEventRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalRecordsRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalRequestRepository;
import com.swp391.school_medical_management.modules.users.repositories.StudentRepository;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineFormRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineHistoryRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineProgramRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineResultRepository;

@Service
public class NurseService {

    private static final Logger logger = LoggerFactory.getLogger(NurseService.class);

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
    private VaccineProgramRepository vaccineProgramRepository;

    @Autowired
    private HealthCheckProgramRepository healthCheckProgramRepository;

    @Autowired
    private HealthCheckFormRepository healthCheckFormRepository;

    @Autowired
    private VaccineFormRepository vaccineFormRepository;

    @Autowired
    private MedicalEventRepository medicalEventRepository;

    @Autowired
    private HealthCheckResultRepository healthCheckResultRepository;

    @Autowired
    private ClassRepository classRepository;

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

    public List<MedicalEventDTO> getMedicalEventsByStudent(Long studentId) {
        Optional<StudentEntity> studentOpt = studentRepository.findStudentById(studentId);
        if (studentOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");

        StudentEntity studentEntity = studentOpt.get();
        UserEntity parent = studentEntity.getParent();
        List<MedicalEventEntity> medicalEventEntitieList = medicalEventRepository.findByStudent(studentEntity);
        if (medicalEventEntitieList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found any medical event");
        List<MedicalEventDTO> medicalEventDTOList = medicalEventEntitieList.stream()
                .map(event -> {
                    MedicalEventDTO dto = modelMapper.map(event, MedicalEventDTO.class);

                    if (event.getNurse() != null) {
                        UserDTO nurseDTO = modelMapper.map(event.getNurse(), UserDTO.class);
                        dto.setNurseDTO(nurseDTO);
                    }
                    if (event.getStudent() != null) {
                        StudentDTO studentDTO = modelMapper.map(event.getStudent(), StudentDTO.class);
                        dto.setStudentDTO(studentDTO);
                    }
                    if (parent != null) {
                        UserDTO parentDTO = modelMapper.map(parent, UserDTO.class);
                        dto.setParentDTO(parentDTO);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
        return medicalEventDTOList;
    }

    public MedicalRequestDTO updateMedicalRequestStatus(int requestId, UpdateMedicalRequestStatus request) {
        Optional<MedicalRequestEntity> medicalRequestOpt = medicalRequestRepository
                .findMedicalRequestEntityByRequestId(requestId);
        if (medicalRequestOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical request not found");
        MedicalRequestEntity medicalRequest = medicalRequestOpt.get();
        StudentEntity student = medicalRequest.getStudent();
        logger.info(medicalRequest.getStatus().toString());
        MedicalRequestStatus statusEnum;
        try {
            statusEnum = MedicalRequestEntity.MedicalRequestStatus.valueOf(request.getStatus().name().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        }

        if (medicalRequest.getStatus() == MedicalRequestStatus.PROCESSING
                && (statusEnum == MedicalRequestStatus.PROCESSING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot update PROCESSING to PROCESSING");
        }

        if (medicalRequest.getStatus() == MedicalRequestStatus.PROCESSING
                && (statusEnum == MedicalRequestStatus.COMPLETED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot update PROCESSING to COMPLETED");
        }

        if (medicalRequest.getStatus() == MedicalRequestStatus.PROCESSING
                && (statusEnum == MedicalRequestStatus.CANCELLED)) {

            if (medicalRequest.getReason() == null || request.getReason_rejected().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Reason is required when cancelling a PROCESSING request");
            }

            medicalRequest.setReason(request.getReason_rejected());
        }

        if (medicalRequest.getStatus() == MedicalRequestStatus.SUBMITTED
                && (statusEnum == MedicalRequestStatus.SUBMITTED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot update SUBMITTED to SUBMITTED");
        }

        if (medicalRequest.getStatus() == MedicalRequestStatus.SUBMITTED
                && (statusEnum == MedicalRequestStatus.PROCESSING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot update SUBMITTED to PROCESSING");
        }

        if (medicalRequest.getStatus() == MedicalRequestStatus.SUBMITTED
                && (statusEnum == MedicalRequestStatus.CANCELLED)) {

            if (request.getReason_rejected() == null || request.getReason_rejected().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Reason is required when cancelling a SUBMITTED request");
            }
            medicalRequest.setReason(request.getReason_rejected());
        }

        if (medicalRequest.getStatus() == MedicalRequestStatus.CANCELLED
                && (statusEnum == MedicalRequestStatus.CANCELLED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot update CANCELLED to CANCELLED");
        }

        if (medicalRequest.getStatus() == MedicalRequestStatus.CANCELLED &&
                (statusEnum == MedicalRequestStatus.PROCESSING ||
                        statusEnum == MedicalRequestStatus.SUBMITTED ||
                        statusEnum == MedicalRequestStatus.CANCELLED ||
                        statusEnum == MedicalRequestStatus.COMPLETED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot update CANCELLED to CANCELLED, PROCESSING, SUBMITTED, or COMPLETED");
        }

        if (medicalRequest.getStatus() == MedicalRequestStatus.COMPLETED &&
                (statusEnum == MedicalRequestStatus.PROCESSING ||
                        statusEnum == MedicalRequestStatus.COMPLETED ||
                        statusEnum == MedicalRequestStatus.CANCELLED ||
                        statusEnum == MedicalRequestStatus.SUBMITTED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot update COMPLETED to COMPLETED, PROCESSING or SUBMITTED");
        }

        medicalRequest.setStatus(statusEnum);
        medicalRequestRepository.save(medicalRequest);
        List<MedicalRequestDetailDTO> detailDTOs = medicalRequest.getMedicalRequestDetailEntities()
                .stream()
                .map(entity -> modelMapper.map(entity, MedicalRequestDetailDTO.class))
                .collect(Collectors.toList());
        MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequest, MedicalRequestDTO.class);
        medicalRequestDTO.setMedicalRequestDetailDTO(detailDTOs);
        medicalRequestDTO.setStudentDTO(modelMapper.map(student, StudentDTO.class));
        return medicalRequestDTO;
    }

    public HealthCheckFormDTO getHealthCheckFormById(Long healthCheckFormId) {
        Optional<HealthCheckFormEntity> healthCheckFormOpt = healthCheckFormRepository.findById(healthCheckFormId);
        if (healthCheckFormOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check form not found");

        HealthCheckFormEntity healthCheckFormEntity = healthCheckFormOpt.get();

        return modelMapper.map(healthCheckFormEntity, HealthCheckFormDTO.class);
    }

    public List<HealthCheckFormDTO> getHealthCheckFormsByProgram(Long programId, Boolean committed) {
        List<HealthCheckFormEntity> healthCheckForms;
        if (Boolean.TRUE.equals(committed)) {
            healthCheckForms = healthCheckFormRepository.findByCommitTrueAndHealthCheckProgram_Id(programId);
        } else {
            healthCheckForms = healthCheckFormRepository.findByHealthCheckProgram_Id(programId);
        }

        if (healthCheckForms.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No health check forms found");

        return healthCheckForms.stream()
                .map(entity -> modelMapper.map(entity, HealthCheckFormDTO.class))
                .collect(Collectors.toList());
    }

    public List<VaccineFormDTO> getVaccineFormsByProgram(Long programId, Boolean committed) {
        List<VaccineFormEntity> vaccineForms;

        if (Boolean.TRUE.equals(committed)) {
            vaccineForms = vaccineFormRepository.findByCommitTrueAndVaccineProgram_VaccineId(programId);
        } else {
            vaccineForms = vaccineFormRepository.findByVaccineProgram_VaccineId(programId);
        }

        if (vaccineForms.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No vaccine forms found");

        return vaccineForms.stream()
                .map(form -> modelMapper.map(form, VaccineFormDTO.class))
                .collect(Collectors.toList());
    }

    // public Map<String, Long> countDraftFormByProgram(Long programId) {
    //     long vaccineForm = vaccineFormRepository.countByVaccineProgram_IdAndStatusAndCommitFalse(programId, VaccineFormStatus.DRAFT);
    //     long healthCheckForm = healthCheckFormRepository.countByHealthCheckProgram_IdAndStatusAndCommitFalse(programId, HealthCheckFormStatus.DRAFT);
    //     return Map.of(
    //             "vaccineForm", vaccineForm,
    //             "healthCheckForm", healthCheckForm);
    // }

    public VaccineFormDTO getVaccinFormById(Long vaccineFormId) {
        Optional<VaccineFormEntity> vaccineFormOpt = vaccineFormRepository.findById(vaccineFormId);
        if (vaccineFormOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine form not found");
        VaccineFormEntity vaccineFormEntity = vaccineFormOpt.get();
        return modelMapper.map(vaccineFormEntity, VaccineFormDTO.class);
    }

    public List<VaccineFormDTO> getNotSentVaccineFormsByProgram(Long programId) {
        List<VaccineFormEntity> vaccineForms = vaccineFormRepository.findByStatusAndVaccineProgram_VaccineId(
                VaccineFormStatus.DRAFT,
                programId);
        return vaccineForms.stream()
                .map(form -> modelMapper.map(form, VaccineFormDTO.class))
                .collect(Collectors.toList());
    }

    public List<HealthCheckFormDTO> getNotSentHealthCheckFormsByProgram(Long programId) {
        List<HealthCheckFormEntity> healthCheckForms = healthCheckFormRepository
                .findByStatusAndHealthCheckProgram_Id(HealthCheckFormStatus.DRAFT, programId);

        return healthCheckForms.stream()
                .map(form -> modelMapper.map(form, HealthCheckFormDTO.class))
                .collect(Collectors.toList());
    }

    public OnGoingProgramDTO getOnGoingPrograms() {
        List<VaccineProgramDTO> vaccinePrograms = vaccineProgramRepository.findByStatus(VaccineProgramStatus.ON_GOING)
                .stream()
                .map(vaccineprogram -> modelMapper.map(vaccineprogram, VaccineProgramDTO.class))
                .collect(Collectors.toList());

        List<HealthCheckProgramDTO> healthCheckPrograms = healthCheckProgramRepository
                .findByStatus(HealthCheckProgramStatus.ON_GOING)
                .stream()
                .map(healthCheckProgram -> modelMapper.map(healthCheckProgram, HealthCheckProgramDTO.class))
                .collect(Collectors.toList());

        return new OnGoingProgramDTO(vaccinePrograms, healthCheckPrograms);
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
        dto.setStudentDTO(modelMapper.map(student, StudentDTO.class));

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

            dto.setParentDTO(userDTO);
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
        MedicalEventDTO medicalEventDTO = modelMapper.map(medicalEventEntity, MedicalEventDTO.class);
        if (medicalEventEntity.getNurse() != null) {
            UserDTO nurseDTO = modelMapper.map(medicalEventEntity.getNurse(), UserDTO.class);
            medicalEventDTO.setNurseDTO(nurseDTO);
        }
        if (medicalEventEntity.getStudent() != null) {
            StudentDTO studentDTO = modelMapper.map(medicalEventEntity.getStudent(), StudentDTO.class);
            medicalEventDTO.setStudentDTO(studentDTO);

            if (medicalEventEntity.getStudent().getParent() != null) {
                UserDTO parentDTO = modelMapper.map(medicalEventEntity.getStudent().getParent(), UserDTO.class);
                medicalEventDTO.setParentDTO(parentDTO);
            }
        }
        return medicalEventDTO;
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
            if (event.getNurse() != null) {
                UserDTO nurseDTO = modelMapper.map(event.getNurse(), UserDTO.class);
                dto.setNurseDTO(nurseDTO);
            }
            StudentEntity student = event.getStudent();
            if (student != null) {
                dto.setStudentDTO(modelMapper.map(student, StudentDTO.class));
                ClassEntity classEntity = student.getClassEntity();
                dto.setClassDTO(modelMapper.map(classEntity, ClassDTO.class));
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

                    dto.setParentDTO(userDTO);
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
        healthCheckResultDTO.setHealthCheckFormDTO(healthCheckFormDTO);
        healthCheckResultDTO.setStudentDTO(
                modelMapper.map(healthCheckFormEntity.getStudent(), StudentDTO.class));

        return healthCheckResultDTO;
    }

    public List<HealthCheckResultDTO> createDefaultHealthCheckResultsForAllCommittedForms() {
        List<HealthCheckFormEntity> committedForms = healthCheckFormRepository.findByCommitTrue();
        List<HealthCheckResultDTO> resultList = new ArrayList<>();

        for (HealthCheckFormEntity form : committedForms) {
            boolean exists = healthCheckResultRepository.findByHealthCheckFormEntity(form).isPresent();
            if (exists)
                continue;

            HealthCheckResultRequest defaultRequest = new HealthCheckResultRequest();
            defaultRequest.setDiagnosis("GOOD");
            defaultRequest.setLevel("GOOD");
            defaultRequest.setNote("No problem");
            defaultRequest.setHealthCheckFormId(form.getId());

            try {
                HealthCheckResultDTO resultDTO = createHealthCheckResult(defaultRequest);
                resultList.add(resultDTO);
            } catch (ResponseStatusException ex) {
                System.out.println("Lỗi tạo result cho formId=" + form.getId() + ": " + ex.getReason());
            }
        }
        return resultList;
    }

    public List<HealthCheckResultDTO> createDefaultHealthCheckResultsByProgramId(Long programId) {
        List<HealthCheckFormEntity> committedForms = healthCheckFormRepository.findCommittedFormsByProgramId(programId);
        List<HealthCheckResultDTO> resultList = new ArrayList<>();

        for (HealthCheckFormEntity form : committedForms) {
            boolean exists = healthCheckResultRepository.findByHealthCheckFormEntity(form).isPresent();
            if (exists)
                continue;

            HealthCheckResultRequest defaultRequest = new HealthCheckResultRequest();
            defaultRequest.setDiagnosis("GOOD");
            defaultRequest.setLevel("GOOD");
            defaultRequest.setNote("No problem");
            defaultRequest.setHealthCheckFormId(form.getId());

            try {
                HealthCheckResultDTO resultDTO = createHealthCheckResult(defaultRequest);
                resultList.add(resultDTO);
            } catch (ResponseStatusException ex) {
                System.out.println("Lỗi tạo result cho formId=" + form.getId() + ": " + ex.getReason());
            }
        }

        return resultList;
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
        healthCheckResultEntity.setVision(request.getVision());
        healthCheckResultEntity.setHearing(request.getHearing());
        healthCheckResultEntity.setWeight(request.getWeight());
        healthCheckResultEntity.setHeight(request.getHeight());
        healthCheckResultRepository.save(healthCheckResultEntity);

        HealthCheckResultDTO healthCheckResultDTO = modelMapper.map(healthCheckResultEntity,
                HealthCheckResultDTO.class);
        healthCheckResultDTO.setHealthCheckFormDTO(healthCheckFormDTO);
        healthCheckResultDTO.setStudentDTO(
                modelMapper.map(healthCheckFormEntity.getStudent(), StudentDTO.class));

        return healthCheckResultDTO;
    }

    public HealthCheckResultDTO getHealthCheckResult(Long healthCheckResultId) {
        HealthCheckResultEntity healthCheckResultEntity = healthCheckResultRepository
                .findByHealthResultId(healthCheckResultId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check result not found"));
        HealthCheckResultDTO dto = modelMapper.map(healthCheckResultEntity, HealthCheckResultDTO.class);

        HealthCheckFormEntity formEntity = healthCheckResultEntity.getHealthCheckFormEntity();
        HealthCheckFormDTO formDTO = modelMapper.map(formEntity, HealthCheckFormDTO.class);
        dto.setHealthCheckFormDTO(formDTO);

        StudentEntity student = formEntity.getStudent();
        StudentDTO studentDTO = modelMapper.map(student, StudentDTO.class);

        if (student.getClassEntity() != null) {
            studentDTO.setClassDTO(modelMapper.map(student.getClassEntity(), ClassDTO.class));
        }

        if (student.getParent() != null) {
            studentDTO.setUserDTO(modelMapper.map(student.getParent(), UserDTO.class));
        }

        dto.setStudentDTO(studentDTO);

        return dto;
    }

    public List<HealthCheckResultDTO> getHealthCheckResultByProgram(Long programId) {
        List<HealthCheckResultEntity> resultEntityList = healthCheckResultRepository
                .findByHealthCheckFormEntity_HealthCheckProgram_Id(programId);

        if (resultEntityList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No health check result found for this program");
        }

        List<HealthCheckResultDTO> dtoList = new ArrayList<>();

        for (HealthCheckResultEntity entity : resultEntityList) {
            HealthCheckResultDTO dto = modelMapper.map(entity, HealthCheckResultDTO.class);

            HealthCheckFormEntity form = entity.getHealthCheckFormEntity();
            dto.setHealthCheckFormDTO(modelMapper.map(form, HealthCheckFormDTO.class));

            StudentEntity student = form.getStudent();
            StudentDTO studentDTO = modelMapper.map(student, StudentDTO.class);

            if (student.getClassEntity() != null)
                studentDTO.setClassDTO(modelMapper.map(student.getClassEntity(), ClassDTO.class));

            if (student.getParent() != null)
                studentDTO.setUserDTO(modelMapper.map(student.getParent(), UserDTO.class));

            dto.setStudentDTO(studentDTO);
            dtoList.add(dto);
        }

        return dtoList;
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

        if (Boolean.FALSE.equals(vaccineFormEntity.getCommit())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent has not committed the vaccine form yet");
        }

        Optional<VaccineResultEntity> existingResultOpt = vaccineResultRepository
                .findByVaccineFormEntity(vaccineFormEntity);
        if (existingResultOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Vaccine result already exists for this student: " + vaccineFormEntity.getStudent().getId());
        }

        Long studentId = vaccineFormEntity.getStudent().getId();
        MedicalRecordEntity medicalRecordEntity = medicalRecordsRepository
                .findMedicalRecordByStudent_Id(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Not found medical record by student"));

        VaccineResultEntity vaccineResultEntity = new VaccineResultEntity();
        vaccineResultEntity.setStatusHealth(request.getStatusHealth());
        vaccineResultEntity.setResultNote(request.getResultNote());
        vaccineResultEntity.setReaction(request.getReaction());
        vaccineResultEntity.setCreatedAt(LocalDateTime.now());
        vaccineResultEntity.setVaccineFormEntity(vaccineFormEntity);
        vaccineResultRepository.save(vaccineResultEntity);

        VaccineProgramEntity program = vaccineFormEntity.getVaccineProgram();

        VaccineHistoryEntity history = new VaccineHistoryEntity();
        VaccineNameEntity vaccineNameEntity = program.getVaccineName();

        if (vaccineNameEntity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Vaccine program missing vaccine name entity");
        }

        history.setVaccineNameEntity(vaccineNameEntity);
        history.setNote(DEFAULT_VACCINE_HS_NOTE);
        history.setMedicalRecord(medicalRecordEntity);

        vaccineHistoryRepository.save(history);

        VaccineResultDTO vaccineResultDTO = modelMapper.map(vaccineResultEntity, VaccineResultDTO.class);
        vaccineResultDTO.setVaccineFormDTO(modelMapper.map(vaccineFormEntity, VaccineFormDTO.class));
        vaccineResultDTO.setStudentDTO(modelMapper.map(vaccineFormEntity.getStudent(), StudentDTO.class));
        return vaccineResultDTO;
    }

    public List<VaccineResultDTO> createDefaultVaccineResultsForAllCommittedForms() {
        List<VaccineFormEntity> committedForms = vaccineFormRepository.findByCommitTrue();
        List<VaccineResultDTO> resultList = new ArrayList<>();

        for (VaccineFormEntity form : committedForms) {
            boolean exists = vaccineResultRepository.findByVaccineFormEntity(form).isPresent();
            if (exists)
                continue;

            VaccineResultRequest defaultRequest = new VaccineResultRequest();
            defaultRequest.setReaction("No reaction");
            defaultRequest.setStatusHealth("GOOD");
            defaultRequest.setResultNote("No problem");
            defaultRequest.setVaccineFormId(form.getId());

            try {
                VaccineResultDTO resultDTO = createVaccineResult(defaultRequest);
                resultList.add(resultDTO);
            } catch (ResponseStatusException ex) {
                System.out.println("Lỗi tạo result cho formId=" + form.getId() + ": " + ex.getReason());
            }
        }

        return resultList;
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

        VaccineResultEntity vaccineResultEntity = existingResultOpt.get();

        vaccineResultEntity.setStatusHealth(request.getStatusHealth());
        vaccineResultEntity.setResultNote(request.getResultNote());
        vaccineResultEntity.setReaction(request.getReaction());
        vaccineResultEntity.setCreatedAt(LocalDateTime.now());
        vaccineResultEntity.setVaccineFormEntity(vaccineFormEntity);
        vaccineResultRepository.save(vaccineResultEntity);

        VaccineResultDTO vaccineResultDTO = modelMapper.map(vaccineResultEntity, VaccineResultDTO.class);
        vaccineResultDTO.setVaccineFormDTO(modelMapper.map(vaccineFormEntity, VaccineFormDTO.class));
        vaccineResultDTO.setStudentDTO(modelMapper.map(vaccineFormEntity.getStudent(), StudentDTO.class));

        return vaccineResultDTO;
    }

    public VaccineResultDTO getVaccineResult(Long vaccineResultId) {
        VaccineResultEntity vaccineResultEntity = vaccineResultRepository.findById(vaccineResultId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine result not found"));

        VaccineResultDTO dto = modelMapper.map(vaccineResultEntity, VaccineResultDTO.class);

        VaccineFormEntity formEntity = vaccineResultEntity.getVaccineFormEntity();
        VaccineFormDTO formDTO = modelMapper.map(formEntity, VaccineFormDTO.class);
        dto.setVaccineFormDTO(formDTO);

        StudentEntity student = formEntity.getStudent();
        StudentDTO studentDTO = modelMapper.map(student, StudentDTO.class);

        if (student.getClassEntity() != null) {
            studentDTO.setClassDTO(modelMapper.map(student.getClassEntity(), ClassDTO.class));
        }

        if (student.getParent() != null) {
            studentDTO.setUserDTO(modelMapper.map(student.getParent(), UserDTO.class));
        }

        dto.setStudentDTO(studentDTO);

        return dto;
    }

    public List<VaccineResultDTO> getVaccineResultByProgram(Long programId) {
        List<VaccineResultEntity> resultEntities = vaccineResultRepository
                .findByVaccineFormEntity_VaccineProgram_VaccineId(programId);

        if (resultEntities.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No vaccine result found for this program");

        List<VaccineResultDTO> dtoList = new ArrayList<>();
        for (VaccineResultEntity entity : resultEntities) {
            VaccineResultDTO dto = modelMapper.map(entity, VaccineResultDTO.class);

            VaccineFormEntity form = entity.getVaccineFormEntity();
            dto.setVaccineFormDTO(modelMapper.map(form, VaccineFormDTO.class));

            StudentEntity student = form.getStudent();
            StudentDTO studentDTO = modelMapper.map(student, StudentDTO.class);

            if (student.getClassEntity() != null)
                studentDTO.setClassDTO(modelMapper.map(student.getClassEntity(), ClassDTO.class));

            if (student.getParent() != null)
                studentDTO.setUserDTO(modelMapper.map(student.getParent(), UserDTO.class));

            dto.setStudentDTO(studentDTO);
            dtoList.add(dto);
        }

        return dtoList;
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

    public FeedbackDTO replyToFeedback(Integer feedbackId, ReplyFeedbackRequest request) {
        FeedbackEntity feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DON'T FIND TO RESPONSE."));

        if (feedback.getStatus() == FeedbackStatus.REPLIED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RESPONSE WAS REPLIED");
        }

        feedback.setResponseNurse(request.getResponse());
        feedback.setStatus(FeedbackStatus.REPLIED);

        if (request.getNurseId() != null) {
            UserEntity nurse = userRepository.findById(request.getNurseId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NURSE NOT FOUND"));
            feedback.setNurse(nurse);
        }

        feedbackRepository.save(feedback);
        FeedbackDTO dto = modelMapper.map(feedback, FeedbackDTO.class);
        return dto;
    }

    public List<FeedbackDTO> getFeedbacksForNurse(Integer nurseId) {
        UserEntity nurse = userRepository.findById(nurseId.longValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NURSE NOT FOUND."));
        List<FeedbackEntity> feedbackList = feedbackRepository.findByNurse(nurse);

        List<FeedbackDTO> feedbackDTOList = feedbackList.stream()
                .map(feedback -> modelMapper.map(feedback, FeedbackDTO.class)).collect(Collectors.toList());

        return feedbackDTOList;
    }

    public List<FeedbackDTO> getAllFeedbacks() {
        List<FeedbackEntity> feedbackList = feedbackRepository.findAll();

        List<FeedbackDTO> feedbackDTOList = feedbackList.stream()
                .map(feedback -> {
                    FeedbackDTO dto = modelMapper.map(feedback, FeedbackDTO.class);
                    dto.setParentId(feedback.getParent() != null ? feedback.getParent().getUserId() : null);
                    dto.setNurseId(feedback.getNurse() != null ? feedback.getNurse().getUserId() : null);
                    dto.setVaccineResultId(
                            feedback.getVaccineResult() != null ? feedback.getVaccineResult().getVaccineResultId()
                                    : null);
                    dto.setHealthResultId(
                            feedback.getHealthResult() != null ? feedback.getHealthResult().getHealthResultId() : null);
                    return dto;
                })
                .collect(Collectors.toList());

        return feedbackDTOList;
    }

    public List<StudentDTO> getStudentsNotVaccinated(Long vaccineProgramId, Long vaccineNameId) {
        List<StudentEntity> students;

        if (vaccineProgramId != null) {
            students = studentRepository.findStudentsNeverVaccinatedByProgramId(vaccineProgramId);
        } else if (vaccineNameId != null) {
            students = studentRepository.findStudentsNeverVaccinatedByVaccineNameId(vaccineNameId);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing filter condition");
        }

        return students.stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    public List<ClassStudentDTO> getAllStudenttt() {
        List<ClassEntity> classList = classRepository.findAll();

        if (classList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No classes found");
        }
        List<ClassStudentDTO> classDTOList = new ArrayList<>();
        for (ClassEntity classEntity : classList) {
            List<StudentEntity> students = studentRepository.findByClassEntity(classEntity);

            List<StudentDTO> studentDTOs;
            if (students.isEmpty()) {
                studentDTOs = new ArrayList<>();
            } else {
                studentDTOs = students.stream().map(student -> {
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
            }

            ClassStudentDTO classDTO = modelMapper.map(classEntity, ClassStudentDTO.class);
            classDTO.setStudents(studentDTOs);

            classDTOList.add(classDTO);
        }

        return classDTOList;

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
                        matchesKeyword = (user.getFullName() != null
                                && user.getFullName().toLowerCase().contains(lowerKeyword))
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

    public void createFormsForHealthCheckProgram(Long programId) {
        HealthCheckProgramEntity program = healthCheckProgramRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Program not found"));

        List<HealthCheckFormEntity> existingForms = healthCheckFormRepository.findByHealthCheckProgram_Id(programId);
        for (HealthCheckFormEntity form : existingForms) {
            if (form.getStatus() == HealthCheckFormEntity.HealthCheckFormStatus.SENT) {
                throw new RuntimeException("Health check forms already created for this program");
            } else {
                form.setStatus(HealthCheckFormEntity.HealthCheckFormStatus.SENT);
                healthCheckFormRepository.save(form);
            }
        }

        // List<StudentEntity> students = studentRepository.findAllWithParent();

        // List<HealthCheckFormEntity> forms = new ArrayList<>();

        // for (StudentEntity student : students) {
        // HealthCheckFormEntity form = new HealthCheckFormEntity();
        // form.setHealthCheckProgram(program);
        // form.setStudent(student);
        // form.setParent(student.getParent());
        // form.setFormDate(LocalDate.now());
        // form.setStatus(HealthCheckFormEntity.HealthCheckFormStatus.SENT);
        // form.setCommit(false);
        // forms.add(form);
        // }

        // healthCheckFormRepository.saveAll(forms);
    }

    public void createFormsForVaccineProgram(Long vaccineProgramId) {
        VaccineProgramEntity program = vaccineProgramRepository.findById(vaccineProgramId)
                .orElseThrow(() -> new RuntimeException("Vaccine program not found"));

        List<StudentEntity> students = studentRepository.findAllWithParent();

        List<VaccineFormEntity> forms = new ArrayList<>();

        for (StudentEntity student : students) {
            VaccineFormEntity form = new VaccineFormEntity();
            form.setVaccineProgram(program);
            form.setStudent(student);
            form.setParent(student.getParent());
            form.setFormDate(LocalDate.now());
            form.setStatus(VaccineFormEntity.VaccineFormStatus.SENT);
            form.setNote(null);
            forms.add(form);
        }

        vaccineFormRepository.saveAll(forms);
    }

    public List<HealthCheckResultDTO> createResultsByProgramId(Long programId) {
        List<HealthCheckFormEntity> committedForms = healthCheckFormRepository.findCommittedFormsByProgramId(programId);
        List<HealthCheckResultDTO> resultList = new ArrayList<>();

        for (HealthCheckFormEntity form : committedForms) {
            Optional<HealthCheckResultEntity> existingResult = healthCheckResultRepository
                    .findByHealthCheckFormEntity(form);
            if (existingResult.isPresent())
                continue;

            HealthCheckResultEntity result = new HealthCheckResultEntity();
            result.setHealthCheckFormEntity(form);
            result.setDiagnosis("GOOD");
            result.setLevel(HealthCheckResultEntity.Level.GOOD);
            result.setVision("10/10");
            result.setHearing("Normal");
            result.setWeight(50.0);
            result.setHeight(160.0);
            result.setNote("No issue detected");

            HealthCheckResultEntity savedResult = healthCheckResultRepository.save(result);

            HealthCheckResultDTO dto = new HealthCheckResultDTO();
            dto.setHealthResultId(savedResult.getHealthResultId());
            dto.setDiagnosis(savedResult.getDiagnosis());
            dto.setLevel(savedResult.getLevel().name());
            dto.setNote(savedResult.getNote());

            HealthCheckFormDTO formDTO = new HealthCheckFormDTO();
            formDTO.setId(form.getId());
            formDTO.setFormDate(form.getFormDate());
            formDTO.setCommit(form.getCommit());
            formDTO.setStatus(form.getStatus() != null ? form.getStatus().name() : null);
            dto.setHealthCheckFormDTO(formDTO);

            StudentDTO studentDTO = modelMapper.map(form.getStudent(), StudentDTO.class);
            dto.setStudentDTO(studentDTO);
            resultList.add(dto);
        }
        Optional<HealthCheckProgramEntity> programOpt = healthCheckProgramRepository.findById(programId);
        programOpt.ifPresent(program -> {
            program.setStatus(HealthCheckProgramStatus.COMPLETED);
            healthCheckProgramRepository.save(program);
        });
        return resultList;
    }
}
