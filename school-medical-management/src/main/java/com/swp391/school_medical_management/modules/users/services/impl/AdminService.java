package com.swp391.school_medical_management.modules.users.services.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
import com.swp391.school_medical_management.modules.users.dtos.response.ParticipateClassDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.ParticipationDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.StudentDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.UserDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineFormStatsDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineNameDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineProgramDTO;
import com.swp391.school_medical_management.modules.users.entities.ClassEntity;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckFormEntity;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckProgramEntity;
import com.swp391.school_medical_management.modules.users.entities.ParticipateClassEntity;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineProgramEntity;
import com.swp391.school_medical_management.modules.users.repositories.ClassRepository;
import com.swp391.school_medical_management.modules.users.repositories.HealthCheckFormRepository;
import com.swp391.school_medical_management.modules.users.repositories.HealthCheckProgramRepository;
import com.swp391.school_medical_management.modules.users.repositories.HealthCheckResultRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalEventRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalRecordsRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalRequestRepository;
import com.swp391.school_medical_management.modules.users.repositories.ParticipateClassRepository;
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

    @Autowired
    private ParticipateClassRepository participateClassRepository;

    public HealthCheckProgramDTO createHealthCheckProgram(HealthCheckProgramRequest request, int adminId) {
        UserEntity admin = userRepository.findById(request.getAdminId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Admin"));

        UserEntity nurse = userRepository.findById(request.getNurseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Y tá"));

        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ngày bắt đầu phải là hôm nay hoặc trong tương lai");
        }

        Optional<HealthCheckProgramEntity> existingProgramOpt = healthCheckProgramRepository
                .findByHealthCheckNameAndStatus(request.getHealthCheckName(),
                        HealthCheckProgramEntity.HealthCheckProgramStatus.NOT_STARTED);

        if (existingProgramOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Chương trình '" + request.getHealthCheckName() + "' đã tồn tại và chưa bắt đầu.");
        }

        HealthCheckProgramEntity healthCheckProgramEntity = new HealthCheckProgramEntity();
        healthCheckProgramEntity.setHealthCheckName(request.getHealthCheckName());
        healthCheckProgramEntity.setDescription(request.getDescription());
        healthCheckProgramEntity.setStartDate(request.getStartDate());
        healthCheckProgramEntity.setDateSendForm(request.getDateSendForm());
        healthCheckProgramEntity.setLocation(request.getLocation());
        healthCheckProgramEntity.setStatus(request.getStatus());
        healthCheckProgramEntity.setAdmin(admin);
        healthCheckProgramEntity.setNurse(nurse);

        healthCheckProgramRepository.save(healthCheckProgramEntity);

        if (request.getClassIds() != null && !request.getClassIds().isEmpty()) {
            for (Integer classId : request.getClassIds()) {
                ClassEntity clazz = classRepository.findById(classId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Không tìm thấy lớp với ID: " + classId));

                ParticipateClassEntity participate = new ParticipateClassEntity();
                participate.setClazz(clazz);
                participate.setHealthCheckProgram(healthCheckProgramEntity);
                participate.setType("HEALTH_CHECK");

                participateClassRepository.save(participate);
            }
        }

        return modelMapper.map(healthCheckProgramEntity, HealthCheckProgramDTO.class);
    }

    public HealthCheckProgramDTO updateHealthCheckProgram(int id, HealthCheckProgramRequest request) {
        HealthCheckProgramEntity existingProgram = healthCheckProgramRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy chương trình kiểm tra sức khỏe"));

        if (existingProgram.getStatus() == HealthCheckProgramEntity.HealthCheckProgramStatus.COMPLETED
                || existingProgram.getStatus() == HealthCheckProgramEntity.HealthCheckProgramStatus.ON_GOING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Không thể cập nhật chương trình đã hoàn thành hoặc đang diễn ra");
        }

        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ngày bắt đầu phải là hôm nay hoặc trong tương lai");
        }

        if (!existingProgram.getHealthCheckName().equals(request.getHealthCheckName())) {
            Optional<HealthCheckProgramEntity> duplicateProgramOpt = healthCheckProgramRepository
                    .findByHealthCheckNameAndStatus(request.getHealthCheckName(),
                            HealthCheckProgramEntity.HealthCheckProgramStatus.NOT_STARTED);
            if (duplicateProgramOpt.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Chương trình '" + request.getHealthCheckName() + "' đã tồn tại và chưa bắt đầu.");
            }
        }

        UserEntity admin = userRepository.findById(request.getAdminId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Admin"));
        existingProgram.setAdmin(admin);

        UserEntity nurse = userRepository.findById(request.getNurseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Y tá"));
        existingProgram.setNurse(nurse);

        existingProgram.setHealthCheckName(request.getHealthCheckName());
        existingProgram.setDescription(request.getDescription());
        existingProgram.setStartDate(request.getStartDate());
        existingProgram.setDateSendForm(request.getDateSendForm());
        existingProgram.setLocation(request.getLocation());
        existingProgram.setStatus(request.getStatus());

        healthCheckProgramRepository.save(existingProgram);

        participateClassRepository.deleteByHealthCheckProgramId(existingProgram.getId());

        if (request.getClassIds() != null && !request.getClassIds().isEmpty()) {
            for (Integer classId : request.getClassIds()) {
                ClassEntity clazz = classRepository.findById(classId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Không tìm thấy lớp với ID: " + classId));

                ParticipateClassEntity participate = new ParticipateClassEntity();
                participate.setClazz(clazz);
                participate.setHealthCheckProgram(existingProgram);
                participate.setType("HEALTH_CHECK");

                participateClassRepository.save(participate);
            }
        }

        return modelMapper.map(existingProgram, HealthCheckProgramDTO.class);
    }

    public HealthCheckProgramDTO updateHealthCheckProgramStatus(int id, String status) {
        HealthCheckProgramEntity healthCheckProgramEntity = healthCheckProgramRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy chương trình kiểm tra sức khỏe"));

        HealthCheckProgramEntity.HealthCheckProgramStatus newStatus;
        try {
            newStatus = HealthCheckProgramEntity.HealthCheckProgramStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trạng thái không hợp lệ: " + status);
        }

        if (healthCheckProgramEntity.getStatus() == HealthCheckProgramEntity.HealthCheckProgramStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Chương trình đã hoàn thành và không thể cập nhật");
        }

        if (healthCheckProgramEntity.getStatus() == newStatus) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Chương trình đã ở trạng thái: " + newStatus);
        }

        if (newStatus == HealthCheckProgramEntity.HealthCheckProgramStatus.FORM_SENT) {
            createHealthCheckForm(healthCheckProgramEntity);
        }
     
        healthCheckProgramEntity.setStatus(newStatus);
        healthCheckProgramRepository.save(healthCheckProgramEntity);

        return modelMapper.map(healthCheckProgramEntity, HealthCheckProgramDTO.class);
    }

    public void createHealthCheckForm(HealthCheckProgramEntity programEntity) {
        String nurseEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity nurse = userRepository.findUserByEmail(nurseEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy y tá với email: " + nurseEmail));

        List<StudentEntity> students = studentRepository.findAll();

        for (StudentEntity student : students) {
            UserEntity parent = student.getParent();
            if (parent == null)
                continue;

            List<HealthCheckFormEntity> existingForms = healthCheckFormRepository
                    .findHealthCheckFormEntityByHealthCheckProgramAndStudent(programEntity, student);
            boolean hasUncommittedForm = existingForms.stream()
                    .anyMatch(form -> form.getCommit() == null);
            if (hasUncommittedForm)
                continue;

            HealthCheckFormEntity healthCheckFormEntity = new HealthCheckFormEntity();
            healthCheckFormEntity.setStudent(student);
            healthCheckFormEntity.setParent(parent);
            healthCheckFormEntity.setNotes(null);
            healthCheckFormEntity.setCommit(null);
            healthCheckFormEntity.setExpDate(programEntity.getStartDate().minusDays(7));
            healthCheckFormEntity.setHealthCheckProgram(programEntity);
            healthCheckFormEntity.setNurse(nurse);

            healthCheckFormRepository.save(healthCheckFormEntity);
        }
    }

    public List<HealthCheckProgramDTO> getAllHealthCheckProgram(int adminId) {
        List<HealthCheckProgramEntity> healthCheckProgramEntityList = healthCheckProgramRepository
                .findByAdmin_UserId(adminId);

        if (healthCheckProgramEntityList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chương trình kiểm tra sức khỏe");
        }

        List<HealthCheckProgramDTO> healthCheckProgramDTOList = healthCheckProgramEntityList.stream()
                .map(entity -> {
                    HealthCheckProgramDTO dto = modelMapper.map(entity, HealthCheckProgramDTO.class);

                    dto.setAdminId(entity.getAdmin().getUserId());
                    dto.setNurseId(entity.getNurse() != null ? entity.getNurse().getUserId() : null);
                    dto.setStatus(entity.getStatus().name());

                    UserDTO adminDTO = modelMapper.map(entity.getAdmin(), UserDTO.class);
                    dto.setAdminDTO(adminDTO);

                    if (entity.getNurse() != null) {
                        UserDTO nurseDTO = modelMapper.map(entity.getNurse(), UserDTO.class);
                        dto.setNurseDTO(nurseDTO);
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        return healthCheckProgramDTOList;
    }

    public HealthCheckProgramDTO getHealthCheckProgramById(int id) {
        Optional<HealthCheckProgramEntity> healthCheckProgramOpt = healthCheckProgramRepository.findById(id);

        if (healthCheckProgramOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chương trình kiểm tra sức khỏe");
        }

        HealthCheckProgramEntity entity = healthCheckProgramOpt.get();

        HealthCheckProgramDTO dto = modelMapper.map(entity, HealthCheckProgramDTO.class);

        // Set thêm các field phụ
        dto.setAdminId(entity.getAdmin().getUserId());
        dto.setNurseId(entity.getNurse() != null ? entity.getNurse().getUserId() : null);
        dto.setStatus(entity.getStatus().name());

        // Map adminDTO
        dto.setAdminDTO(modelMapper.map(entity.getAdmin(), UserDTO.class));

        // Map nurseDTO nếu có
        if (entity.getNurse() != null) {
            dto.setNurseDTO(modelMapper.map(entity.getNurse(), UserDTO.class));
        }

        // (Tùy chọn) Nếu bạn muốn map thêm healthCheckFormDTOs:
        List<HealthCheckFormEntity> formEntities = healthCheckFormRepository.findAllByHealthCheckProgram_Id(id);
        List<HealthCheckFormDTO> formDTOs = formEntities.stream()
                .map(form -> modelMapper.map(form, HealthCheckFormDTO.class))
                .collect(Collectors.toList());
        dto.setHealthCheckFormDTOs(formDTOs);

        // (Tùy chọn) Nếu bạn muốn map thêm participateClasses:
        List<ParticipateClassEntity> participateEntities = participateClassRepository
                .findAllByHealthCheckProgram_Id(id);
        List<ParticipateClassDTO> participateDTOs = participateEntities.stream()
                .map(pc -> modelMapper.map(pc, ParticipateClassDTO.class))
                .collect(Collectors.toList());
        dto.setParticipateClasses(participateDTOs);

        return dto;
    }

    public void deleteHealthCheckProgram(int id) {
        Optional<HealthCheckProgramEntity> healthCheckProgramOpt = healthCheckProgramRepository.findById(id);

        if (healthCheckProgramOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chương trình kiểm tra sức khỏe");
        }

        HealthCheckProgramEntity healthCheckProgramEntity = healthCheckProgramOpt.get();

        HealthCheckProgramEntity.HealthCheckProgramStatus status = healthCheckProgramEntity.getStatus();
        if (status == HealthCheckProgramEntity.HealthCheckProgramStatus.ON_GOING
                || status == HealthCheckProgramEntity.HealthCheckProgramStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Không thể xoá chương trình đang diễn ra hoặc đã hoàn thành");
        }

        healthCheckProgramRepository.delete(healthCheckProgramEntity);
    }

    public VaccineProgramDTO createVaccineProgram(VaccineProgramRequest request, long adminId) {
        // UserEntity admin = userRepository.findUserByUserId(adminId)
        // .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin
        // not found"));

        // VaccineNameEntity vaccineNameEntity =
        // vaccineNameRepository.findById(request.getVaccineNameId())
        // .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
        // "Vaccine name not found"));

        // Optional<VaccineProgramEntity> existingProgramOpt = vaccineProgramRepository
        // .findByVaccineNameAndStatus(vaccineNameEntity,
        // VaccineProgramStatus.NOT_STARTED);

        // // Optional<VaccineProgramEntity> lastVaccineProgramOpt =
        // // vaccineProgramRepository
        // //
        // .findTopByVaccineDateLessThanEqualOrderByVaccineDateDesc(request.getVaccineDate());

        // // if (lastVaccineProgramOpt.isPresent()) {
        // // VaccineProgramEntity lastVaccineProgramEntity =
        // lastVaccineProgramOpt.get();
        // // logger.info(lastVaccineProgramEntity.getVaccineName().getVaccineName() + "
        // "
        // // + lastVaccineProgramEntity.getStatus());
        // // if
        // //
        // (!VaccineProgramStatus.COMPLETED.equals(lastVaccineProgramEntity.getStatus()))
        // // {
        // // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Last vaccine is
        // // not COMPLETED!");
        // // }
        // // }

        // if (existingProgramOpt.isPresent()) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        // "Vaccine program with name '" + vaccineNameEntity.getVaccineName()
        // + "' already exists and not started.");
        // }

        // VaccineProgramEntity entity = new VaccineProgramEntity();
        // entity.setVaccineName(vaccineNameEntity);
        // entity.setDescription(request.getDescription());
        // entity.setVaccineDate(request.getVaccineDate());
        // entity.setNote(request.getNote());
        // entity.setStatus(VaccineProgramStatus.NOT_STARTED);
        // entity.setAdmin(admin);

        // vaccineProgramRepository.save(entity);

        // VaccineNameDTO vaccineNameDTO = new VaccineNameDTO();
        // vaccineNameDTO.setId(vaccineNameEntity.getVaccineNameId());
        // vaccineNameDTO.setVaccineName(vaccineNameEntity.getVaccineName());
        // vaccineNameDTO.setManufacture(vaccineNameEntity.getManufacture());
        // vaccineNameDTO.setUrl(vaccineNameEntity.getUrl());
        // vaccineNameDTO.setNote(vaccineNameEntity.getNote());

        // VaccineProgramDTO dto = new VaccineProgramDTO();
        // dto.setVaccineId(entity.getVaccineId());
        // dto.setVaccineName(vaccineNameDTO);
        // dto.setDescription(entity.getDescription());
        // dto.setVaccineDate(entity.getVaccineDate());
        // dto.setStatus(entity.getStatus().name());
        // dto.setNote(entity.getNote());

        // return dto;
        return null;
    }

    public VaccineProgramDTO updateVaccineProgram(VaccineProgramRequest request, long id) {
        // Optional<VaccineProgramEntity> existingProgramOpt =
        // vaccineProgramRepository.findVaccineProgramByVaccineId(id);
        // if (existingProgramOpt.isEmpty()) {
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine program not
        // found");
        // }

        // VaccineProgramEntity existingProgram = existingProgramOpt.get();

        // if (existingProgram.getStatus() == VaccineProgramStatus.COMPLETED
        // || existingProgram.getStatus() == VaccineProgramStatus.ON_GOING) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        // "Cannot update 'COMPLETED' or 'ON_GOING' program");
        // }

        // VaccineNameEntity vaccineNameEntity =
        // vaccineNameRepository.findById(request.getVaccineNameId())
        // .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
        // "Vaccine name not found"));

        // existingProgram.setVaccineName(vaccineNameEntity);
        // existingProgram.setDescription(request.getDescription());
        // existingProgram.setVaccineDate(request.getVaccineDate());
        // existingProgram.setNote(request.getNote());

        // vaccineProgramRepository.save(existingProgram);

        // return modelMapper.map(existingProgram, VaccineProgramDTO.class);
        return null;
    }

    public VaccineProgramDTO updateVaccineProgramStatus(Long id, String status) {
        // Optional<VaccineProgramEntity> vaccineProgramOpt =
        // vaccineProgramRepository.findById(id);
        // if (vaccineProgramOpt.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine program not
        // found");
        // VaccineProgramEntity vaccineProgramEntity = vaccineProgramOpt.get();

        // VaccineProgramStatus newStatus;
        // try {
        // newStatus = VaccineProgramStatus.valueOf(status.toUpperCase());
        // } catch (IllegalArgumentException e) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: "
        // + status);
        // }

        // if (vaccineProgramEntity.getStatus() == VaccineProgramStatus.COMPLETED) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        // "Program is already COMPLETED and cannot be updated");
        // }

        // if (vaccineProgramEntity.getStatus() == (newStatus)) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Program is already
        // in status: " + newStatus);
        // }

        // if (newStatus == VaccineProgramStatus.COMPLETED
        // && vaccineProgramEntity.getStatus() != VaccineProgramStatus.ON_GOING) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        // "Cannot mark as COMPLETED unless the program is ON_GOING");
        // }

        // vaccineProgramEntity.setStatus(newStatus);
        // vaccineProgramRepository.save(vaccineProgramEntity);

        // if (newStatus == VaccineProgramStatus.ON_GOING) {
        // createVaccineForm(vaccineProgramEntity);
        // }

        // return modelMapper.map(vaccineProgramEntity, VaccineProgramDTO.class);
        return null;
    }

    public void createVaccineForm(VaccineProgramEntity programEntity) {
        // VaccineNameEntity vaccineNameEntity = programEntity.getVaccineName();
        // Long programId = programEntity.getVaccineId();

        // List<StudentEntity> students =
        // studentRepository.findStudentsNeverVaccinatedByProgramId(programId);

        // for (StudentEntity studentEntity : students) {
        // UserEntity parent = studentEntity.getParent();
        // if (parent == null)
        // continue;

        // List<VaccineFormEntity> existingForms = vaccineFormRepository
        // .findVaccineFormEntityByVaccineProgramAndStudent(programEntity,
        // studentEntity);

        // boolean hasUncommittedForm = existingForms.stream()
        // .anyMatch(form -> form.getCommit() == null);
        // if (hasUncommittedForm)
        // continue;

        // VaccineFormEntity vaccineFormEntity = new VaccineFormEntity();
        // vaccineFormEntity.setStudent(studentEntity);
        // vaccineFormEntity.setParent(parent);
        // vaccineFormEntity.setNote(null);
        // vaccineFormEntity.setCommit(null);
        // vaccineFormEntity.setFormDate(programEntity.getVaccineDate().minusDays(7));
        // vaccineFormEntity.setStatus(VaccineFormStatus.DRAFT);
        // vaccineFormEntity.setVaccineProgram(programEntity);

        // vaccineFormRepository.save(vaccineFormEntity);
        // }
    }

    public List<VaccineProgramDTO> getAllVaccineProgram() {
        // List<VaccineProgramEntity> vaccineProgramEntitieList =
        // vaccineProgramRepository.findAll();
        // if (vaccineProgramEntitieList.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No vaccine programs
        // found");
        // List<VaccineProgramDTO> vaccineProgramDTOList =
        // vaccineProgramEntitieList.stream()
        // .map(entity -> modelMapper.map(entity, VaccineProgramDTO.class))
        // .toList();
        // for (VaccineProgramDTO v : vaccineProgramDTOList) {
        // Long programId = v.getVaccineId();
        // List<VaccineFormEntity> vaccineFormEntityList = vaccineFormRepository
        // .findAllByVaccineProgram_VaccineId(programId);
        // if (vaccineFormEntityList.isEmpty()) {
        // v.setSended(0);
        // } else {
        // boolean allStatusOne = vaccineFormEntityList.stream()
        // .allMatch(form -> form.getStatus().equals(VaccineFormStatus.DRAFT));
        // v.setSended(allStatusOne ? 0 : 1);
        // }
        // }
        // return vaccineProgramDTOList;
        return null;
    }

    public VaccineProgramDTO getVaccineProgramById(long id) {
        // Optional<VaccineProgramEntity> vaccineProgramOpt =
        // vaccineProgramRepository.findVaccineProgramByVaccineId(id);
        // if (vaccineProgramOpt.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine program not
        // found");
        // VaccineProgramEntity vaccineProgramEntity = vaccineProgramOpt.get();
        // return modelMapper.map(vaccineProgramEntity, VaccineProgramDTO.class);
        return null;
    }

    public void deleteVaccineProgram(long id) {
        // Optional<VaccineProgramEntity> vaccineProgramOpt =
        // vaccineProgramRepository.findVaccineProgramByVaccineId(id);
        // if (vaccineProgramOpt.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine program not
        // found");
        // VaccineProgramEntity vaccineProgramEntity = vaccineProgramOpt.get();
        // vaccineProgramRepository.delete(vaccineProgramEntity);
    }

    public List<ClassDTO> getAllClass() {
        // List<ClassEntity> classEntityList = classRepository.findAll();
        // if (classEntityList.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found");
        // List<ClassDTO> classDTOList = classEntityList.stream()
        // .map(classEntity -> modelMapper.map(classEntity, ClassDTO.class))
        // .collect(Collectors.toList());
        // return classDTOList;
        return null;
    }

    public List<StudentDTO> getAllStudentInClass(Long classId) {
        // Optional<ClassEntity> classOpt = classRepository.findByClassId(classId);
        // if (classOpt.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found");
        // ClassEntity classEntity = classOpt.get();

        // List<StudentEntity> studentEntitieList =
        // studentRepository.findByClassEntity(classEntity);
        // if (studentEntitieList.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Class is empty");

        // List<StudentDTO> studentDTOList = studentEntitieList.stream()
        // .map(studentEntitie -> modelMapper.map(studentEntitie, StudentDTO.class))
        // .collect(Collectors.toList());
        // return studentDTOList;
        return null;
    }

    public MedicalRecordDTO getMedicalRecordByStudentId(Long parentId, Long studentId) {
        // Optional<MedicalRecordEntity> optMedicalRecord = medicalRecordsRepository
        // .findMedicalRecordByStudent_Id(studentId);
        // if (optMedicalRecord.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not
        // found");
        // MedicalRecordEntity medicalRecord = optMedicalRecord.get();
        // return modelMapper.map(medicalRecord, MedicalRecordDTO.class);
        return null;
    }

    public UserDTO createAccount(NurseAccountRequest request) {
        // if (userRepository.existsByEmail(request.getEmail())) {
        // throw new RuntimeException("Email already exists!");
        // }
        // String password = passwordService.generateStrongRandomPassword();
        // String encodedPassword = passwordEncoder.encode(password);
        // UserEntity user = new UserEntity();
        // user.setEmail(request.getEmail());
        // user.setFullName(request.getName());
        // user.setPassword(encodedPassword);
        // user.setPhone(request.getPhone());
        // user.setAddress(request.getAddress());
        // user.setActive(true);
        // user.setRole(UserRole.NURSE);
        // userRepository.save(user);
        // emailService.sendEmail(
        // request.getEmail(),
        // "Thông tin tài khoản",
        // "Tài khoản của bạn đã được tạo thành công.\n\n"
        // + "Email: " + request.getEmail() + "\n"
        // + "Password: " + password + "\n\n"
        // + "Vui lòng đăng nhập và đổi mật khẩu ngay để bảo mật tài khoản.");
        // return modelMapper.map(user, UserDTO.class);
        return null;
    }

    public List<UserDTO> getAllAccounts() {
        // List<UserEntity> userEntities = userRepository.findAll();
        // if (userEntities.isEmpty()) {
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No accounts found");
        // }
        // return userEntities.stream()
        // .map(user -> modelMapper.map(user, UserDTO.class))
        // .collect(Collectors.toList());
        return null;
    }

    public UserDTO updateAccount(long userId, UpdateProfileRequest request) {
        // UserEntity user = userRepository.findUserByUserId(userId)
        // .orElseThrow(() -> new RuntimeException("User not found with ID: " +
        // userId));

        // user.setFullName(request.getFullName());
        // // if (!user.getEmail().equals(request.getEmail())) {
        // // boolean emailExists = userRepository.existsByEmail(request.getEmail());
        // // if (emailExists) {
        // // throw new RuntimeException("Email already in use");
        // // }
        // user.setEmail(request.getEmail());
        // // }

        // // if (!user.getPhone().equals(request.getPhone())) {
        // // boolean phoneExists = userRepository.existsByPhone(request.getPhone());
        // // if (phoneExists) {
        // // throw new RuntimeException("Phone already in use");
        // // }
        // user.setPhone(request.getPhone());
        // // }
        // user.setAddress(request.getAddress());

        // userRepository.save(user);
        // UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        // return userDTO;
        return null;
    }

    public void deleteAccount(Long userId, String token) {
        // Optional<UserEntity> userOpt = userRepository.findUserByUserId(userId);
        // if (userOpt.isEmpty()) {
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        // }
        // UserEntity user = userOpt.get();
        // if (user.getRole() == UserRole.ADMIN) {
        // throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không thể xoá tài
        // khoản ADMIN");
        // }

        // if (user.getRole() == UserRole.PARENT) {
        // throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không thể xoá tài
        // khoản PHỤ HUYNH");
        // }

        // BlacklistTokenRequest blacklistRequest = new BlacklistTokenRequest();
        // blacklistRequest.setToken(token);

        // refreshTokenRepository.deleteByUser(user);
        // blacklistService.create(blacklistRequest);

        // userRepository.delete(user);
    }

    @Transactional
    public void importStudentFromExcel(MultipartFile file) {
        // try (InputStream is = file.getInputStream(); Workbook workbook = new
        // XSSFWorkbook(is)) {
        // Sheet sheet = workbook.getSheetAt(0);
        // DataFormatter formatter = new DataFormatter();
        // int importedCount = 0;

        // for (Row row : sheet) {
        // int rowNum = row.getRowNum();
        // if (rowNum == 0)
        // continue;
        // if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK)
        // continue;

        // String studentName = row.getCell(0).getStringCellValue();
        // LocalDate dob = row.getCell(1).getLocalDateTimeCellValue().toLocalDate();
        // String gender = row.getCell(2).getStringCellValue();
        // String relationship = row.getCell(3).getStringCellValue();
        // String className = row.getCell(4).getStringCellValue();
        // String teacherName = row.getCell(5).getStringCellValue();
        // String parentName = row.getCell(6).getStringCellValue();
        // String parentEmail = row.getCell(7).getStringCellValue();
        // String parentPhone = formatter.formatCellValue(row.getCell(8));
        // String parentAddress = row.getCell(9).getStringCellValue();

        // Optional<ClassEntity> classOpt =
        // classRepository.findByClassNameAndTeacherName(className, teacherName);
        // ClassEntity classEntity = classOpt.orElseGet(() -> {
        // ClassEntity newClass = new ClassEntity();
        // newClass.setClassName(className);
        // newClass.setTeacherName(teacherName);
        // newClass.setQuantity(0);
        // return classRepository.save(newClass);
        // });

        // // kiểm tra trùng
        // Optional<UserEntity> parentOpt =
        // userRepository.findUserByEmailOrPhone(parentEmail, parentPhone);
        // if (parentOpt.isPresent()) {
        // UserEntity existing = parentOpt.get();
        // if (existing.getEmail().equalsIgnoreCase(parentEmail)) {
        // throw new RuntimeException("Email " + parentEmail + " đã bị trùng.");
        // } else {
        // throw new RuntimeException("Số điện thoại " + parentPhone + " đã bị trùng.");
        // }
        // }

        // UserEntity parent = new UserEntity();
        // parent.setFullName(parentName);
        // parent.setEmail(parentEmail);
        // parent.setPhone(parentPhone);
        // parent.setAddress(parentAddress);
        // parent.setActive(true);
        // parent.setRole(UserRole.PARENT);
        // parent = userRepository.save(parent);

        // StudentEntity student = new StudentEntity();
        // student.setFullName(studentName);
        // student.setDob(dob);
        // student.setGender(gender);
        // student.setRelationship(relationship);
        // student.setClassEntity(classEntity);
        // student.setParent(parent);

        // studentRepository.save(student);
        // importedCount++;
        // }

        // if (importedCount == 0) {
        // throw new RuntimeException("File không hợp lệ.");
        // }
        // } catch (Exception e) {
        // throw new RuntimeException("Import thất bại: " + e.getMessage());
        // }
    }

    public long countStudents() {
        // return studentRepository.count();
        return 0;
    }

    public long countMedicalRecords() {
        // return medicalRecordsRepository.count();
        return 0;
    }

    public long countVaccineProgram() {
        // return vaccineProgramRepository
        // .countByStatusIn(List.of(VaccineProgramStatus.NOT_STARTED,
        // VaccineProgramStatus.ON_GOING));
        return 0;
    }

    public long countHealthCheckProgram() {
        // return healthCheckProgramRepository
        // .countByStatusIn(List.of(HealthCheckProgramStatus.NOT_STARTED,
        // HealthCheckProgramStatus.ON_GOING));
        return 0;
    }

    public long countProcessingMedicalRequest() {
        // return
        // medicalRequestRepository.countByStatusIn(List.of(MedicalRequestStatus.PROCESSING));
        return 0;
    }

    // Thien
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
        // return null;
    }

    // Thien
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
        // return null;
    }

    public List<HealthCheckResultStatsDTO> getHealthCheckResultStatusStatsByProgram() {
        // List<HealthCheckResultByProgramStatsRaw> rawList =
        // healthCheckResultRepository
        // .getHealthCheckResultStatusStatsByProgram();

        // return rawList.stream()
        // .map(row -> new HealthCheckResultStatsDTO(
        // row.getProgramId(),
        // row.getProgramName(),
        // row.getStatusHealth(),
        // row.getCount()))
        // .collect(Collectors.toList());
        return null;
    }

    public ParticipationDTO getLatestParticipation() {
        ParticipationDTO participationDTO = new ParticipationDTO();
        Optional<VaccineProgramEntity> lastestVaccineProgramOpt = vaccineProgramRepository
                .findTopByStatusOrderByStartDateDesc(VaccineProgramEntity.VaccineProgramStatus.COMPLETED);
        Optional<HealthCheckProgramEntity> latestHealthCheckProgramOpt = healthCheckProgramRepository
                .findTopByStatusOrderByStartDateDesc(HealthCheckProgramEntity.HealthCheckProgramStatus.COMPLETED);

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
        // return null;
    }

    public VaccineFormStatsDTO getFormStatsByProgram(Long vaccineProgramId) {
        // long total =
        // vaccineFormRepository.countByVaccineProgram_VaccineId(vaccineProgramId);
        // long committed =
        // vaccineFormRepository.countByVaccineProgram_VaccineIdAndCommitTrue(vaccineProgramId);

        // return new VaccineFormStatsDTO(total, committed);
        return null;
    }

    public List<VaccineNameDTO> getAllVaccineNames() {
        // return vaccineNameRepository.findAll()
        // .stream()
        // .map(entity -> modelMapper.map(entity, VaccineNameDTO.class))
        // .collect(Collectors.toList());
        return null;
    }

    public VaccineNameDTO createVaccineName(VaccineNameRequest request) {
        // if
        // (vaccineNameRepository.findByVaccineName(request.getVaccineName()).isPresent())
        // {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vaccine name
        // already exists");
        // }

        // VaccineNameEntity entity = modelMapper.map(request, VaccineNameEntity.class);
        // VaccineNameEntity saved = vaccineNameRepository.save(entity);

        // return modelMapper.map(saved, VaccineNameDTO.class);
        return null;
    }

    public void importVaccineNameFromExcel(MultipartFile file) {
        // try (InputStream is = file.getInputStream(); Workbook workbook = new
        // XSSFWorkbook(is)) {
        // Sheet sheet = workbook.getSheetAt(0);
        // int importedCount = 0;
        // for (Row row : sheet) {
        // int rowNum = row.getRowNum();
        // if (rowNum == 0)
        // continue;
        // if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK)
        // continue;
        // try {

        // String vaccineName = row.getCell(0).getStringCellValue();
        // String manufacture = row.getCell(1).getStringCellValue();
        // String url = row.getCell(2).getStringCellValue();
        // String note = row.getCell(3).getStringCellValue();

        // VaccineNameEntity vaccine = new VaccineNameEntity();
        // vaccine.setVaccineName(vaccineName);
        // vaccine.setManufacture(manufacture);
        // vaccine.setUrl(url);
        // vaccine.setNote(note);

        // // student
        // Optional<VaccineNameEntity> vaccineNameOpt = vaccineNameRepository
        // .findByVaccineNameAndManufactureAndUrlAndNote(vaccineName, manufacture, url,
        // note);
        // VaccineNameEntity vaccineNameEntity;
        // if (vaccineNameOpt.isPresent()) {
        // vaccineNameEntity = vaccineNameOpt.get();
        // } else {
        // vaccineNameEntity = new VaccineNameEntity();
        // vaccineNameEntity.setVaccineName(vaccineName);
        // vaccineNameEntity.setManufacture(manufacture);
        // vaccineNameEntity.setUrl(url);
        // vaccineNameEntity.setNote(note);
        // }

        // vaccineNameRepository.save(vaccineNameEntity);
        // importedCount++;
        // } catch (Exception e) {
        // throw new RuntimeException("Lỗi ở dòng Excel số " + rowNum + ": "
        // + row.getCell(0).getStringCellValue() + " - " + e.getMessage());
        // }
        // }
        // if (importedCount == 0) {
        // throw new RuntimeException("File không hợp lệ.");
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e.getMessage());
        // }
    }
}
