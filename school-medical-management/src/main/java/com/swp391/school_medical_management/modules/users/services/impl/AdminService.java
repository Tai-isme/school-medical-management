package com.swp391.school_medical_management.modules.users.services.impl;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.swp391.school_medical_management.modules.users.dtos.request.BlacklistTokenRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.HealthCheckProgramRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.NurseAccountRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.UpdateProfileRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.VaccineNameRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.VaccineProgramRequest;
import com.swp391.school_medical_management.modules.users.dtos.response.ClassDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.CommitedPercentDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckFormDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckProgramDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckResultStatsDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRecordDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.ParticipationDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.StudentDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.UserDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineFormStatsDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineNameDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineProgramDTO;
import com.swp391.school_medical_management.modules.users.entities.ClassEntity;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckFormEntity;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckFormEntity.HealthCheckFormStatus;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckProgramEntity;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckProgramEntity.HealthCheckProgramStatus;
import com.swp391.school_medical_management.modules.users.entities.MedicalRecordEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalRequestEntity.MedicalRequestStatus;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity.UserRole;
import com.swp391.school_medical_management.modules.users.entities.VaccineFormEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineFormEntity.VaccineFormStatus;
import com.swp391.school_medical_management.modules.users.entities.VaccineNameEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineProgramEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineProgramEntity.VaccineProgramStatus;
import com.swp391.school_medical_management.modules.users.repositories.ClassRepository;
import com.swp391.school_medical_management.modules.users.repositories.HealthCheckFormRepository;
import com.swp391.school_medical_management.modules.users.repositories.HealthCheckProgramRepository;
import com.swp391.school_medical_management.modules.users.repositories.HealthCheckResultRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalEventRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalRecordsRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalRequestRepository;
import com.swp391.school_medical_management.modules.users.repositories.RefreshTokenRepository;
import com.swp391.school_medical_management.modules.users.repositories.StudentRepository;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineFormRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineNameRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineProgramRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineResultRepository;
import com.swp391.school_medical_management.modules.users.repositories.projection.EventStatRaw;
import com.swp391.school_medical_management.modules.users.repositories.projection.HealthCheckResultByProgramStatsRaw;
import com.swp391.school_medical_management.modules.users.repositories.projection.ParticipationRateRaw;
import com.swp391.school_medical_management.service.EmailService;
import com.swp391.school_medical_management.service.PasswordService;

@Service
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private HealthCheckProgramRepository healthCheckProgramRepository;

    @Autowired
    private VaccineProgramRepository vaccineProgramRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MedicalRecordsRepository medicalRecordsRepository;

    @Autowired
    private HealthCheckFormRepository healthCheckFormRepository;

    @Autowired
    private VaccineFormRepository vaccineFormRepository;

    @Autowired
    private MedicalEventRepository medicalEventRepository;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private MedicalRequestRepository medicalRequestRepository;

    @Autowired
    private VaccineResultRepository vaccineResultRepository;

    @Autowired
    private HealthCheckResultRepository healthCheckResultRepository;

    @Autowired
    private VaccineNameRepository vaccineNameRepository;

    public HealthCheckProgramDTO createHealthCheckProgram(HealthCheckProgramRequest request, long adminId) {
        UserEntity admin = userRepository.findUserByUserId(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));

        Optional<HealthCheckProgramEntity> existingProgramOpt = healthCheckProgramRepository
                .findByHealthCheckNameAndStatus(request.getHealthCheckName(), HealthCheckProgramStatus.NOT_STARTED);

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date");
        }

        if (existingProgramOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Health check program with name '"
                    + request.getHealthCheckName() + "' already exists and not started.");
        }

        // kiểm tra chương trình gần nhất trước đó có COMPLETED chưa
        // Optional<HealthCheckProgramEntity> lastProgramOpt =
        // healthCheckProgramRepository
        // .findTopByStartDateLessThanOrderByStartDateDesc(request.getStartDate());

        // if (lastProgramOpt.isPresent()) {
        // HealthCheckProgramEntity last = lastProgramOpt.get();
        // if (!HealthCheckProgramStatus.COMPLETED.equals(last.getStatus())) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        // "The last health check program is not COMPLETED.");
        // }
        // }

        // kiểm tra có chương trình nào vẫn còn hiệu lực vào thời điểm bắt đầu chương
        // trình mới

        // List<HealthCheckProgramEntity> overlappingPrograms =
        // healthCheckProgramRepository
        // .findByEndDateAfter(request.getStartDate());

        // boolean hasUncompleted = overlappingPrograms.stream()
        // .anyMatch(p -> !HealthCheckProgramStatus.COMPLETED.equals(p.getStatus()));

        // if (hasUncompleted) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        // "Another health check program is still active and not COMPLETED.");
        // }

        HealthCheckProgramEntity healthCheckProgramEntity = new HealthCheckProgramEntity();
        healthCheckProgramEntity.setHealthCheckName(request.getHealthCheckName());
        healthCheckProgramEntity.setDescription(request.getDescription());
        healthCheckProgramEntity.setStartDate(request.getStartDate());
        healthCheckProgramEntity.setEndDate(request.getEndDate());
        healthCheckProgramEntity.setNote(request.getNote());
        healthCheckProgramEntity.setAdmin(admin);
        healthCheckProgramEntity.setStatus(HealthCheckProgramStatus.NOT_STARTED);
        healthCheckProgramRepository.save(healthCheckProgramEntity);
        return modelMapper.map(healthCheckProgramEntity, HealthCheckProgramDTO.class);
    }

    public HealthCheckProgramDTO updateHealthCheckProgram(Long id, HealthCheckProgramRequest request) {
        Optional<HealthCheckProgramEntity> existingProgramOpt = healthCheckProgramRepository.findById(id);

        if (existingProgramOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check program not found");

        HealthCheckProgramEntity existingProgram = existingProgramOpt.get();

        if (existingProgram.getStatus() == HealthCheckProgramStatus.COMPLETED
                || existingProgram.getStatus() == HealthCheckProgramStatus.ON_GOING)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot update 'COMPLETED' or 'ON_GOING' program");

        if (request.getStartDate().isAfter(request.getEndDate()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date");

        if (!existingProgram.getHealthCheckName().equals(request.getHealthCheckName())) {
            Optional<HealthCheckProgramEntity> duplicateProgramOpt = healthCheckProgramRepository
                    .findByHealthCheckNameAndStatus(request.getHealthCheckName(), HealthCheckProgramStatus.NOT_STARTED);
            if (duplicateProgramOpt.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Health check program with name '" + request.getHealthCheckName()
                                + "' already exists and not started.");
            }
        }

        existingProgram.setHealthCheckName(request.getHealthCheckName());
        existingProgram.setDescription(request.getDescription());
        existingProgram.setStartDate(request.getStartDate());
        existingProgram.setEndDate(request.getEndDate());
        existingProgram.setNote(request.getNote());

        healthCheckProgramRepository.save(existingProgram);
        return modelMapper.map(existingProgram, HealthCheckProgramDTO.class);
    }

    public HealthCheckProgramDTO updateHealthCheckProgramStatus(Long id, String status) {
        Optional<HealthCheckProgramEntity> healthCheckProgramOpt = healthCheckProgramRepository.findById(id);
        if (healthCheckProgramOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check program not found");
        HealthCheckProgramEntity healthCheckProgramEntity = healthCheckProgramOpt.get();

        HealthCheckProgramStatus newStatus;
        try {
            newStatus = HealthCheckProgramStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + status);
        }

        if (healthCheckProgramEntity.getStatus() == HealthCheckProgramStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Program is already COMPLETED and cannot be updated");
        }

        if (healthCheckProgramEntity.getStatus() == (newStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Program is already in status: " + newStatus);
        }

        if (newStatus == HealthCheckProgramStatus.COMPLETED
                && healthCheckProgramEntity.getStatus() != HealthCheckProgramStatus.ON_GOING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot mark as COMPLETED unless the program is ON_GOING");
        }

        healthCheckProgramEntity.setStatus(newStatus);
        healthCheckProgramRepository.save(healthCheckProgramEntity);

        if (newStatus == HealthCheckProgramStatus.ON_GOING) {
            createHealthCheckForm(healthCheckProgramEntity);
        }

        return modelMapper.map(healthCheckProgramEntity, HealthCheckProgramDTO.class);
    }

    public void createHealthCheckForm(HealthCheckProgramEntity programEntity) {

        List<StudentEntity> students = studentRepository.findAll();

        for (StudentEntity studentEntity : students) {
            UserEntity parent = studentEntity.getParent();
            if (parent == null)
                continue;

            List<HealthCheckFormEntity> existingForms = healthCheckFormRepository
                    .findHealthCheckFormEntityByHealthCheckProgramAndStudent(programEntity, studentEntity);
            boolean hasUncommittedForm = existingForms.stream()
                    .anyMatch(form -> form.getCommit() == null);
            if (hasUncommittedForm)
                continue;

            HealthCheckFormEntity healthCheckFormEntity = new HealthCheckFormEntity();
            healthCheckFormEntity.setStudent(studentEntity);
            healthCheckFormEntity.setParent(parent);
            healthCheckFormEntity.setNotes(null);
            healthCheckFormEntity.setCommit(null);
            healthCheckFormEntity.setFormDate(programEntity.getStartDate().minusDays(3));
            healthCheckFormEntity.setStatus(HealthCheckFormStatus.DRAFT);
            healthCheckFormEntity.setHealthCheckProgram(programEntity);
            healthCheckFormRepository.save(healthCheckFormEntity);
        }
    }

    public List<HealthCheckProgramDTO> getAllHealthCheckProgram(long adminId) {
        List<HealthCheckProgramEntity> healthCheckProgramEntityList = healthCheckProgramRepository.findAll();
        if (healthCheckProgramEntityList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No health check programs found");
        List<HealthCheckProgramDTO> healthCheckProgramDTOList = healthCheckProgramEntityList.stream()
                .map(entity -> modelMapper.map(entity, HealthCheckProgramDTO.class))
                .toList();
        for (HealthCheckProgramDTO healthCheckProgramDTO : healthCheckProgramDTOList) {
            Long programId = healthCheckProgramDTO.getId();
            List<HealthCheckFormEntity> healthCheckFormEntitieList = healthCheckFormRepository
                    .findAllByHealthCheckProgram_Id(programId);
            if (healthCheckFormEntitieList.isEmpty()) {
                healthCheckProgramDTO.setSended(0);
            } else {
                healthCheckProgramDTO.setSended(1);
            }
        }
        return healthCheckProgramDTOList;
    }

    public HealthCheckProgramDTO getHealthCheckProgramById(Long id) {
        Optional<HealthCheckProgramEntity> healthCheckProgramOpt = healthCheckProgramRepository.findById(id);
        if (healthCheckProgramOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check program not found");
        HealthCheckProgramEntity healthCheckProgramEntity = healthCheckProgramOpt.get();
        return modelMapper.map(healthCheckProgramEntity, HealthCheckProgramDTO.class);
    }

    public void deleteHealthCheckProgram(Long id) {
        Optional<HealthCheckProgramEntity> healthCheckProgramOpt = healthCheckProgramRepository.findById(id);
        if (healthCheckProgramOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check program not found");

        HealthCheckProgramEntity healthCheckProgramEntity = healthCheckProgramOpt.get();

        if (healthCheckProgramEntity.getStatus() == HealthCheckProgramStatus.COMPLETED
                || healthCheckProgramEntity.getStatus() == HealthCheckProgramStatus.ON_GOING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot delete 'COMPLETED' or 'ON_GOING' program");
        }

        healthCheckProgramRepository.delete(healthCheckProgramEntity);
    }

    public VaccineProgramDTO createVaccineProgram(VaccineProgramRequest request, long adminId) {
        UserEntity admin = userRepository.findUserByUserId(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));

        VaccineNameEntity vaccineNameEntity = vaccineNameRepository.findById(request.getVaccineNameId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vaccine name not found"));

        Optional<VaccineProgramEntity> existingProgramOpt = vaccineProgramRepository
                .findByVaccineNameAndStatus(vaccineNameEntity, VaccineProgramStatus.NOT_STARTED);

        // Optional<VaccineProgramEntity> lastVaccineProgramOpt =
        // vaccineProgramRepository
        // .findTopByVaccineDateLessThanEqualOrderByVaccineDateDesc(request.getVaccineDate());

        // if (lastVaccineProgramOpt.isPresent()) {
        // VaccineProgramEntity lastVaccineProgramEntity = lastVaccineProgramOpt.get();
        // logger.info(lastVaccineProgramEntity.getVaccineName().getVaccineName() + " "
        // + lastVaccineProgramEntity.getStatus());
        // if
        // (!VaccineProgramStatus.COMPLETED.equals(lastVaccineProgramEntity.getStatus()))
        // {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Last vaccine is
        // not COMPLETED!");
        // }
        // }

        if (existingProgramOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Vaccine program with name '" + vaccineNameEntity.getVaccineName()
                            + "' already exists and not started.");
        }

        VaccineProgramEntity entity = new VaccineProgramEntity();
        entity.setVaccineName(vaccineNameEntity);
        entity.setDescription(request.getDescription());
        entity.setVaccineDate(request.getVaccineDate());
        entity.setNote(request.getNote());
        entity.setStatus(VaccineProgramStatus.NOT_STARTED);
        entity.setAdmin(admin);

        vaccineProgramRepository.save(entity);

        VaccineNameDTO vaccineNameDTO = new VaccineNameDTO();
        vaccineNameDTO.setId(vaccineNameEntity.getVaccineNameId());
        vaccineNameDTO.setVaccineName(vaccineNameEntity.getVaccineName());
        vaccineNameDTO.setManufacture(vaccineNameEntity.getManufacture());
        vaccineNameDTO.setUrl(vaccineNameEntity.getUrl());
        vaccineNameDTO.setNote(vaccineNameEntity.getNote());

        VaccineProgramDTO dto = new VaccineProgramDTO();
        dto.setVaccineId(entity.getVaccineId());
        dto.setVaccineName(vaccineNameDTO);
        dto.setDescription(entity.getDescription());
        dto.setVaccineDate(entity.getVaccineDate());
        dto.setStatus(entity.getStatus().name());
        dto.setNote(entity.getNote());

        return dto;

    }

    public VaccineProgramDTO updateVaccineProgram(VaccineProgramRequest request, long id) {
        Optional<VaccineProgramEntity> existingProgramOpt = vaccineProgramRepository.findVaccineProgramByVaccineId(id);
        if (existingProgramOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine program not found");
        }

        VaccineProgramEntity existingProgram = existingProgramOpt.get();

        if (existingProgram.getStatus() == VaccineProgramStatus.COMPLETED
                || existingProgram.getStatus() == VaccineProgramStatus.ON_GOING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot update 'COMPLETED' or 'ON_GOING' program");
        }

        VaccineNameEntity vaccineNameEntity = vaccineNameRepository.findById(request.getVaccineNameId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vaccine name not found"));

        existingProgram.setVaccineName(vaccineNameEntity);
        existingProgram.setDescription(request.getDescription());
        existingProgram.setVaccineDate(request.getVaccineDate());
        existingProgram.setNote(request.getNote());

        vaccineProgramRepository.save(existingProgram);

        return modelMapper.map(existingProgram, VaccineProgramDTO.class);
    }

    public VaccineProgramDTO updateVaccineProgramStatus(Long id, String status) {
        Optional<VaccineProgramEntity> vaccineProgramOpt = vaccineProgramRepository.findById(id);
        if (vaccineProgramOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine program not found");
        VaccineProgramEntity vaccineProgramEntity = vaccineProgramOpt.get();

        VaccineProgramStatus newStatus;
        try {
            newStatus = VaccineProgramStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + status);
        }

        if (vaccineProgramEntity.getStatus() == VaccineProgramStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Program is already COMPLETED and cannot be updated");
        }

        if (vaccineProgramEntity.getStatus() == (newStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Program is already in status: " + newStatus);
        }

        if (newStatus == VaccineProgramStatus.COMPLETED
                && vaccineProgramEntity.getStatus() != VaccineProgramStatus.ON_GOING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot mark as COMPLETED unless the program is ON_GOING");
        }

        vaccineProgramEntity.setStatus(newStatus);
        vaccineProgramRepository.save(vaccineProgramEntity);

        if (newStatus == VaccineProgramStatus.ON_GOING) {
            createVaccineForm(vaccineProgramEntity);
        }

        return modelMapper.map(vaccineProgramEntity, VaccineProgramDTO.class);
    }

    public void createVaccineForm(VaccineProgramEntity programEntity) {
        VaccineNameEntity vaccineNameEntity = programEntity.getVaccineName();
        Long programId = programEntity.getVaccineId();

        List<StudentEntity> students = studentRepository.findStudentsNeverVaccinatedByProgramId(programId);

        for (StudentEntity studentEntity : students) {
            UserEntity parent = studentEntity.getParent();
            if (parent == null)
                continue;

            List<VaccineFormEntity> existingForms = vaccineFormRepository
                    .findVaccineFormEntityByVaccineProgramAndStudent(programEntity, studentEntity);

            boolean hasUncommittedForm = existingForms.stream()
                    .anyMatch(form -> form.getCommit() == null);
            if (hasUncommittedForm)
                continue;

            VaccineFormEntity vaccineFormEntity = new VaccineFormEntity();
            vaccineFormEntity.setStudent(studentEntity);
            vaccineFormEntity.setParent(parent);
            vaccineFormEntity.setNote(null);
            vaccineFormEntity.setCommit(null);
            vaccineFormEntity.setFormDate(programEntity.getVaccineDate().minusDays(3));
            vaccineFormEntity.setStatus(VaccineFormStatus.DRAFT);
            vaccineFormEntity.setVaccineProgram(programEntity);

            vaccineFormRepository.save(vaccineFormEntity);
        }
    }

    public List<VaccineProgramDTO> getAllVaccineProgram() {
        List<VaccineProgramEntity> vaccineProgramEntitieList = vaccineProgramRepository.findAll();
        if (vaccineProgramEntitieList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No vaccine programs found");
        List<VaccineProgramDTO> vaccineProgramDTOList = vaccineProgramEntitieList.stream()
                .map(entity -> modelMapper.map(entity, VaccineProgramDTO.class))
                .toList();
        for (VaccineProgramDTO v : vaccineProgramDTOList) {
            Long programId = v.getVaccineId();
            List<VaccineFormEntity> vaccineFormEntityList = vaccineFormRepository
                    .findAllByVaccineProgram_VaccineId(programId);
            if (vaccineFormEntityList.isEmpty()) {
                v.setSended(0);
            } else {
                v.setSended(1);
            }
        }
        return vaccineProgramDTOList;
    }

    public VaccineProgramDTO getVaccineProgramById(long id) {
        Optional<VaccineProgramEntity> vaccineProgramOpt = vaccineProgramRepository.findVaccineProgramByVaccineId(id);
        if (vaccineProgramOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine program not found");
        VaccineProgramEntity vaccineProgramEntity = vaccineProgramOpt.get();
        return modelMapper.map(vaccineProgramEntity, VaccineProgramDTO.class);
    }

    public void deleteVaccineProgram(long id) {
        Optional<VaccineProgramEntity> vaccineProgramOpt = vaccineProgramRepository.findVaccineProgramByVaccineId(id);
        if (vaccineProgramOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine program not found");
        VaccineProgramEntity vaccineProgramEntity = vaccineProgramOpt.get();
        vaccineProgramRepository.delete(vaccineProgramEntity);
    }

    public List<ClassDTO> getAllClass() {
        List<ClassEntity> classEntityList = classRepository.findAll();
        if (classEntityList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found");
        List<ClassDTO> classDTOList = classEntityList.stream()
                .map(classEntity -> modelMapper.map(classEntity, ClassDTO.class))
                .collect(Collectors.toList());
        return classDTOList;
    }

    public List<StudentDTO> getAllStudentInClass(Long classId) {
        Optional<ClassEntity> classOpt = classRepository.findByClassId(classId);
        if (classOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found");
        ClassEntity classEntity = classOpt.get();

        List<StudentEntity> studentEntitieList = studentRepository.findByClassEntity(classEntity);
        if (studentEntitieList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Class is empty");

        List<StudentDTO> studentDTOList = studentEntitieList.stream()
                .map(studentEntitie -> modelMapper.map(studentEntitie, StudentDTO.class))
                .collect(Collectors.toList());
        return studentDTOList;
    }

    public MedicalRecordDTO getMedicalRecordByStudentId(Long parentId, Long studentId) {
        Optional<MedicalRecordEntity> optMedicalRecord = medicalRecordsRepository
                .findMedicalRecordByStudent_Id(studentId);
        if (optMedicalRecord.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found");
        MedicalRecordEntity medicalRecord = optMedicalRecord.get();
        return modelMapper.map(medicalRecord, MedicalRecordDTO.class);
    }

    public UserDTO createAccount(NurseAccountRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }
        String password = passwordService.generateStrongRandomPassword();
        String encodedPassword = passwordEncoder.encode(password);
        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setFullName(request.getName());
        user.setPassword(encodedPassword);
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setActive(true);
        user.setRole(UserRole.NURSE);
        userRepository.save(user);
        emailService.sendEmail(request.getEmail(), "Account: ",
                "\nEmail: " + request.getEmail()
                        + "\nPassword: " + password);
        return modelMapper.map(user, UserDTO.class);
    }

    public List<UserDTO> getAllAccounts() {
        List<UserEntity> userEntities = userRepository.findAll();
        if (userEntities.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No accounts found");
        }
        return userEntities.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    public UserDTO updateAccount(long userId, UpdateProfileRequest request) {
        UserEntity user = userRepository.findUserByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        user.setFullName(request.getFullName());
        if (!user.getEmail().equals(request.getEmail())) {
            boolean emailExists = userRepository.existsByEmail(request.getEmail());
            if (emailExists) {
                throw new RuntimeException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }

        if (!user.getPhone().equals(request.getPhone())) {
            boolean phoneExists = userRepository.existsByPhone(request.getPhone());
            if (phoneExists) {
                throw new RuntimeException("Phone already in use");
            }
            user.setPhone(request.getPhone());
        }
        user.setAddress(request.getAddress());

        userRepository.save(user);
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return userDTO;
    }

    public void deleteAccount(Long userId, String token) {
        Optional<UserEntity> userOpt = userRepository.findUserByUserId(userId);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        UserEntity user = userOpt.get();
        if (user.getRole() == UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không thể xoá tài khoản ADMIN");
        }

        if (user.getRole() == UserRole.PARENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không thể xoá tài khoản PHỤ HUYNH");
        }

        BlacklistTokenRequest blacklistRequest = new BlacklistTokenRequest();
        blacklistRequest.setToken(token);

        refreshTokenRepository.deleteByUser(user);
        blacklistService.create(blacklistRequest);

        userRepository.delete(user);
    }

    public void importFromExcel(MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            for (Row row : sheet) {
                int rowNum = row.getRowNum();
                if (rowNum == 0)
                    continue; // skip header
                try {

                    // student
                    String studentName = row.getCell(0).getStringCellValue();
                    LocalDate dob = row.getCell(1).getLocalDateTimeCellValue().toLocalDate();
                    String gender = row.getCell(2).getStringCellValue();
                    String relationship = row.getCell(3).getStringCellValue();

                    // class
                    String className = row.getCell(4).getStringCellValue();
                    String teacherName = row.getCell(5).getStringCellValue();

                    // parent
                    String parentName = row.getCell(6).getStringCellValue();
                    String parentEmail = row.getCell(7).getStringCellValue();
                    String parentPhone = formatter.formatCellValue(row.getCell(8));
                    String parentAddress = row.getCell(9).getStringCellValue();

                    // class
                    Optional<ClassEntity> classOpt = classRepository.findByClassNameAndTeacherName(className,
                            teacherName);
                    ClassEntity classEntity;
                    if (classOpt.isPresent()) {
                        classEntity = classOpt.get();
                    } else {
                        classEntity = new ClassEntity();
                        classEntity.setClassName(className);
                        classEntity.setTeacherName(teacherName);
                        classEntity.setQuantity(0);
                        classEntity = classRepository.save(classEntity);
                    }

                    // parent
                    Optional<UserEntity> parentOpt = userRepository.findUserByEmail(parentEmail);
                    UserEntity parent;
                    if (parentOpt.isPresent()) {
                        parent = parentOpt.get();
                    } else {
                        parent = new UserEntity();
                        parent.setFullName(parentName);
                        parent.setEmail(parentEmail);
                        parent.setPhone(parentPhone);
                        parent.setAddress(parentAddress);
                        parent.setActive(true);
                        parent.setRole(UserRole.PARENT);
                        parent = userRepository.save(parent);
                    }

                    // student
                    Optional<StudentEntity> studentOpt = studentRepository
                            .findByFullNameAndDobAndGenderAndRelationshipAndClassEntityAndParent(
                                    studentName, dob, gender, relationship, classEntity, parent);
                    StudentEntity student;
                    if (studentOpt.isPresent()) {
                        student = studentOpt.get();
                    } else {
                        student = new StudentEntity();
                        student.setFullName(studentName);
                        student.setDob(dob);
                        student.setGender(gender);
                        student.setRelationship(relationship);
                        student.setClassEntity(classEntity);
                        student.setParent(parent);
                    }

                    studentRepository.save(student);
                } catch (Exception e) {
                    throw new RuntimeException("Lỗi ở dòng Excel số " + (rowNum + 1) + ": "
                            + row.getCell(0).getStringCellValue() + " - " + e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }

    public long countStudents() {
        return studentRepository.count();
    }

    public long countMedicalRecords() {
        return medicalRecordsRepository.count();
    }

    public long countVaccineProgram() {
        return vaccineProgramRepository
                .countByStatusIn(List.of(VaccineProgramStatus.NOT_STARTED, VaccineProgramStatus.ON_GOING));
    }

    public long countHealthCheckProgram() {
        return healthCheckProgramRepository
                .countByStatusIn(List.of(HealthCheckProgramStatus.NOT_STARTED, HealthCheckProgramStatus.ON_GOING));
    }

    public long countProcessingMedicalRequest() {
        return medicalRequestRepository.countByStatusIn(List.of(MedicalRequestStatus.PROCESSING));
    }

    public List<Map<String, Object>> getEventStatsByMonth(int year) {
        List<EventStatRaw> rawList = medicalEventRepository.getEventStatsByMonth(year);
        Map<Integer, Map<String, Object>> grouped = new TreeMap<>();

        for (EventStatRaw raw : rawList) {
            Integer month = raw.getMonth(); // 1 → 12
            String type = raw.getTypeEvent();
            int count = raw.getCount().intValue();

            Map<String, Object> row = grouped.computeIfAbsent(month, k -> {
                Map<String, Object> newRow = new HashMap<>();
                newRow.put("month", "T" + k); // Đổi tên theo format T1, T2,...
                return newRow;
            });

            row.put(type, count);
        }

        return new ArrayList<>(grouped.values());
    }

    public List<HealthCheckResultStatsDTO> getVaccineResultStatusStatsByProgram() {
        List<HealthCheckResultByProgramStatsRaw> rawList = vaccineResultRepository
                .getVaccineResultStatusStatsByProgram();

        return rawList.stream()
                .map(row -> new HealthCheckResultStatsDTO(
                        row.getProgramId(),
                        row.getProgramName(),
                        row.getStatusHealth(),
                        row.getCount()))
                .collect(Collectors.toList());
    }

    public List<HealthCheckResultStatsDTO> getHealthCheckResultStatusStatsByProgram() {
        List<HealthCheckResultByProgramStatsRaw> rawList = healthCheckResultRepository
                .getHealthCheckResultStatusStatsByProgram();

        return rawList.stream()
                .map(row -> new HealthCheckResultStatsDTO(
                        row.getProgramId(),
                        row.getProgramName(),
                        row.getStatusHealth(),
                        row.getCount()))
                .collect(Collectors.toList());
    }

    public ParticipationDTO getLatestParticipation() {
        ParticipationDTO participationDTO = new ParticipationDTO();
        Optional<VaccineProgramEntity> lastestVaccineProgramOpt = vaccineProgramRepository
                .findTopByStatusOrderByVaccineDateDesc(VaccineProgramStatus.COMPLETED);
        Optional<HealthCheckProgramEntity> latestHealthCheckProgramOpt = healthCheckProgramRepository
                .findTopByStatusOrderByEndDateDesc(HealthCheckProgramStatus.COMPLETED);

        if (!lastestVaccineProgramOpt.isPresent()) {
            participationDTO.setVaccination(new CommitedPercentDTO(null, 0L, 0L));
        } else {
            VaccineProgramEntity latestVaccineProgram = lastestVaccineProgramOpt.get();
            ParticipationRateRaw vaccine = vaccineFormRepository
                    .getParticipationRateByVaccineId(latestVaccineProgram.getVaccineId());
            participationDTO
                    .setVaccination(new CommitedPercentDTO(latestVaccineProgram.getVaccineName().getVaccineName(),
                            vaccine.getCommittedCount(), vaccine.getTotalSent()));
        }

        if (!latestHealthCheckProgramOpt.isPresent()) {
            participationDTO.setHealthCheck(new CommitedPercentDTO(null, 0L, 0L));
        } else {
            HealthCheckProgramEntity latestHealthCheckProgram = latestHealthCheckProgramOpt.get();
            ParticipationRateRaw healthCheck = healthCheckFormRepository
                    .getParticipationRateByHealthCheckId(latestHealthCheckProgram.getId());
            participationDTO.setHealthCheck(new CommitedPercentDTO(latestHealthCheckProgram.getHealthCheckName(),
                    healthCheck.getCommittedCount(), healthCheck.getTotalSent()));
        }
        return participationDTO;
    }

    public VaccineFormStatsDTO getFormStatsByProgram(Long vaccineProgramId) {
        long total = vaccineFormRepository.countByVaccineProgram_VaccineId(vaccineProgramId);
        long committed = vaccineFormRepository.countByVaccineProgram_VaccineIdAndCommitTrue(vaccineProgramId);

        return new VaccineFormStatsDTO(total, committed);
    }

    public List<VaccineNameDTO> getAllVaccineNames() {
        return vaccineNameRepository.findAll()
                .stream()
                .map(entity -> modelMapper.map(entity, VaccineNameDTO.class))
                .collect(Collectors.toList());
    }

    public VaccineNameDTO createVaccineName(VaccineNameRequest request) {
        if (vaccineNameRepository.findByVaccineName(request.getVaccineName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vaccine name already exists");
        }

        VaccineNameEntity entity = modelMapper.map(request, VaccineNameEntity.class);
        VaccineNameEntity saved = vaccineNameRepository.save(entity);

        return modelMapper.map(saved, VaccineNameDTO.class);
    }
}
