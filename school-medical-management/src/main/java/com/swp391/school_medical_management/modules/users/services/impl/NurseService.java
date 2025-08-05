package com.swp391.school_medical_management.modules.users.services.impl;

import com.swp391.school_medical_management.modules.users.dtos.request.*;
import com.swp391.school_medical_management.modules.users.dtos.response.*;
import com.swp391.school_medical_management.modules.users.entities.*;
import com.swp391.school_medical_management.modules.users.entities.UserEntity.UserRole;
import com.swp391.school_medical_management.modules.users.repositories.*;
import com.swp391.school_medical_management.service.EmailService;
import com.swp391.school_medical_management.service.NotificationService;
import com.swp391.school_medical_management.service.UploadImageFile;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NurseService {

    private static final Logger logger = LoggerFactory.getLogger(NurseService.class);

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

    @Autowired
    private ParticipateClassRepository participateClassRepository;

    @Autowired
    private VaccineUnitRepository vaccineUnitRepository;

    @Autowired
    private MedicalRequestDetailRepository medicalRequestDetailRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UploadImageFile uploadImageFile;

    @Autowired
    private NotificationService notificationService;

    public List<MedicalRequestDTO> getPendingMedicalRequest() {
        // List<MedicalRequestEntity> pendingMedicalRequestList =
        // medicalRequestRepository
        // .findByStatus(MedicalRequestStatus.PROCESSING);
        // if (pendingMedicalRequestList.isEmpty()) {
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No processing
        // medical requests found");
        // }
        // List<MedicalRequestDTO> medicalRequestDTOList = new ArrayList<>();
        // for (MedicalRequestEntity medicalRequestEntity : pendingMedicalRequestList) {
        // List<MedicalRequestDetailEntity> medicalRequestDetailEntityList =
        // medicalRequestEntity
        // .getMedicalRequestDetailEntities();
        // List<MedicalRequestDetailDTO> medicalRequestDetailDTOList =
        // medicalRequestDetailEntityList.stream()
        // .map(medicalRequestDetailEntity ->
        // modelMapper.map(medicalRequestDetailEntity,
        // MedicalRequestDetailDTO.class))
        // .collect(Collectors.toList());
        // MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequestEntity,
        // MedicalRequestDTO.class);
        // medicalRequestDTO.setMedicalRequestDetailDTO(medicalRequestDetailDTOList);
        // medicalRequestDTOList.add(medicalRequestDTO);
        // }
        // return medicalRequestDTOList;
        return null;
    }

    public MedicalRequestDTO createMedicalRequest(int nurseId, MedicalRequest request, MultipartFile image) {

        String imageUrl = null;
        try {
            imageUrl = uploadImageFile.uploadImage(image);
        } catch (Exception e) {
            // TODO: handle exception
        }

        logger.info("imageUrl: {}", imageUrl);

        Optional<StudentEntity> studentOpt = studentRepository.findStudentById(request.getStudentId());
        if (studentOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tim thấy học sinh");
        StudentEntity student = studentOpt.get();
        UserEntity nurse = userRepository.findById(nurseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phụ huynh"));

        if (request.getMedicalRequestDetailRequests() == null ||
                request.getMedicalRequestDetailRequests().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chi tiết đơn thuốc không được để trống!");
        }

        if (request.getDate() == null ||
                request.getDate().isBefore(java.time.LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày gửi thuốc phải là ngày hôm nay hoặc trong tương lai!");
        }

        MedicalRequestEntity medicalRequestEntity = new MedicalRequestEntity();
        medicalRequestEntity.setRequestName(request.getRequestName());
        medicalRequestEntity.setDate(request.getDate());
        medicalRequestEntity.setStatus(MedicalRequestEntity.MedicalRequestStatus.CONFIRMED);
        medicalRequestEntity.setNote(request.getNote());
        medicalRequestEntity.setStudent(student);
        medicalRequestEntity.setParent(student.getParent());
        medicalRequestEntity.setNurse(nurse);
        medicalRequestEntity.setImage(imageUrl);

        medicalRequestEntity.setMedicalRequestDetailEntities(new ArrayList<>());

        for (MedicalRequestDetailRequest details : request.getMedicalRequestDetailRequests()) {
            MedicalRequestDetailEntity medicalRequestDetailEntity = new MedicalRequestDetailEntity();
            medicalRequestDetailEntity.setMedicineName(details.getMedicineName());
            medicalRequestDetailEntity.setQuantity(details.getQuantity());
            medicalRequestDetailEntity.setType(details.getType());
            medicalRequestDetailEntity.setMethod(details.getMethod());
            medicalRequestDetailEntity.setTimeSchedule(details.getTimeSchedule());
            medicalRequestDetailEntity.setStatus(MedicalRequestDetailEntity.Status.NOT_TAKEN);
            medicalRequestDetailEntity.setNote(details.getNote());
            medicalRequestDetailEntity.setMedicalRequest(medicalRequestEntity);

            medicalRequestEntity.getMedicalRequestDetailEntities().add(medicalRequestDetailEntity);
        }

        medicalRequestRepository.save(medicalRequestEntity);
        MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequestEntity,
                MedicalRequestDTO.class);
        List<MedicalRequestDetailDTO> medicalRequestDetailDTOList = medicalRequestEntity
                .getMedicalRequestDetailEntities()
                .stream()
                .map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity,
                        MedicalRequestDetailDTO.class))
                .collect(Collectors.toList());
        medicalRequestDTO.setMedicalRequestDetailDTO(medicalRequestDetailDTOList);
        return medicalRequestDTO;
    }


    public List<MedicalRequestDTO> getAllMedicalRequest() {
        List<MedicalRequestEntity> medicalRequestEntityList = medicalRequestRepository.findAll();
        if (medicalRequestEntityList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No medical requests found");
        }
        List<MedicalRequestDTO> medicalRequestDTOList = new ArrayList<>();
        for (MedicalRequestEntity medicalRequestEntity : medicalRequestEntityList) {
            MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequestEntity, MedicalRequestDTO.class);

            StudentEntity studentEntity = medicalRequestEntity.getStudent();
            StudentDTO studentDTO = modelMapper.map(studentEntity, StudentDTO.class);

            medicalRequestDTO.setStudentDTO(studentDTO);

            try {
                UserEntity nurse = userRepository.findById(medicalRequestEntity.getNurse().getUserId()).orElseThrow(() -> new UsernameNotFoundException("Nurse not found"));
                UserDTO nurseDTO = modelMapper.map(nurse, UserDTO.class);
                medicalRequestDTO.setNurseDTO(nurseDTO);
            } catch (Exception e) {
                medicalRequestDTO.setNurseDTO(null);
            }
            ClassDTO classDTO = modelMapper.map(classRepository.findById(studentEntity.getClassEntity().getClassId()).get(), ClassDTO.class);
            classDTO.setStudents(null);
            studentDTO.setClassDTO(classDTO);

            medicalRequestDTO.setParentDTO(modelMapper.map(studentEntity.getParent(), UserDTO.class));

            List<MedicalRequestDetailEntity> medicalRequestDetailEntityList = medicalRequestEntity.getMedicalRequestDetailEntities();
            List<MedicalRequestDetailDTO> medicalRequestDetailDTOList = medicalRequestDetailEntityList.stream().map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity, MedicalRequestDetailDTO.class)).collect(Collectors.toList());

            medicalRequestDTO.setMedicalRequestDetailDTO(medicalRequestDetailDTOList);
            medicalRequestDTOList.add(medicalRequestDTO);
        }
        return medicalRequestDTOList;
    }

    // Hàm lấy khi status là COMFIRMED
    // public List<MedicalRequestDTO> getAllMedicalRequestByStatusConfirmed() {
    //     List<MedicalRequestDTO> medicalRequestDTOListReturn = new ArrayList<MedicalRequestDTO>();


    //     List<MedicalRequestEntity> medicalRequestEntityList = medicalRequestRepository
    //             .findByStatus(MedicalRequestEntity.MedicalRequestStatus.CONFIRMED);


    //     if (medicalRequestEntityList.isEmpty()) {
    //         throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No confirmed medical requests found");
    //     }

    //     for (MedicalRequestEntity medicalRequestEntity : medicalRequestEntityList) {
    //         MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequestEntity, MedicalRequestDTO.class);

    //         StudentEntity studentEntity = medicalRequestEntity.getStudent();
    //         StudentDTO studentDTO = modelMapper.map(studentEntity, StudentDTO.class);

    //         medicalRequestDTO.setStudentDTO(studentDTO);

    //         try {
    //             UserEntity nurse = userRepository.findById(medicalRequestEntity.getNurse().getUserId())
    //                     .orElseThrow(() -> new UsernameNotFoundException("Nurse not found"));
    //             UserDTO nurseDTO = modelMapper.map(nurse, UserDTO.class);
    //             medicalRequestDTO.setNurseDTO(nurseDTO);
    //         } catch (Exception e) {
    //             medicalRequestDTO.setNurseDTO(null);
    //         }
    //         ClassDTO classDTO =
    //                 modelMapper.map(classRepository.findById(studentEntity.getClassEntity().getClassId()).get(),
    //                         ClassDTO.class);
    //         classDTO.setStudents(null);
    //         studentDTO.setClassDTO(classDTO);

    //         medicalRequestDTO.setParentDTO(modelMapper.map(studentEntity.getParent(), UserDTO.class));

    //         List<MedicalRequestDetailEntity> medicalRequestDetailEntityList = medicalRequestEntity
    //                 .getMedicalRequestDetailEntities();


    //         List<MedicalRequestDetailDTO> medicalRequestDetailDTOList = medicalRequestDetailEntityList.stream()
    //                 .map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity,
    //                         MedicalRequestDetailDTO.class))
    //                 .collect(Collectors.toList());

    //         MedicalRequestDTO medicalRequestDTOReturn = new MedicalRequestDTO();

    //                 for (MedicalRequestDetailDTO medicalRequestDetailDTO : medicalRequestDetailDTOList) {

    //                     if(medicalRequestDetailDTO.getStatus().equals("TAKEN")) {
    //                         continue; // Chỉ lấy những chi tiết thuốc chưa được uống
    //                     }

    //                     if(medicalRequestDTOReturn.getMedicalRequestDetailDTO() == null || medicalRequestDTOReturn.getMedicalRequestDetailDTO().size() == 0) {
    //                         medicalRequestDTOReturn = medicalRequestDTO;
    //                         List<MedicalRequestDetailDTO> list = medicalRequestDTOReturn.getMedicalRequestDetailDTO();
    //                         if (list == null) {
    //                             list = new ArrayList<>();
    //                         }
    //                         list.add(medicalRequestDetailDTO);
    //                         medicalRequestDTOReturn.setMedicalRequestDetailDTO(list); 
    //                         medicalRequestDTOListReturn.add(medicalRequestDTOReturn);
    //                     }else{

    //                         for (MedicalRequestDetailDTO detail : medicalRequestDTOReturn.getMedicalRequestDetailDTO()) {
    //                             if(medicalRequestDetailDTO.getTimeSchedule().equals(detail.getTimeSchedule())){

    //                                 List<MedicalRequestDetailDTO> list = medicalRequestDTOReturn.getMedicalRequestDetailDTO();
    //                                 if (list == null) {
    //                                     list = new ArrayList<>();
    //                                 }
    //                                 medicalRequestDTOReturn.setMedicalRequestDetailDTO(list);

    //                             }else{
    //                                 // Nếu không trùng thì tạo mới MedicalRequestDTO
    //                                 medicalRequestDTOListReturn.add(medicalRequestDTOReturn);
    //                                 medicalRequestDTOReturn = medicalRequestDTO;
    //                                 List<MedicalRequestDetailDTO> list = medicalRequestDTOReturn.getMedicalRequestDetailDTO();
    //                                 list.add(medicalRequestDetailDTO);
    //                                 medicalRequestDTOReturn.setMedicalRequestDetailDTO(list);
    //                                 medicalRequestDTOListReturn.add(medicalRequestDTOReturn);   
    //                             }

    //                     }}
    //                 }
    //         };
    //     return medicalRequestDTOListReturn; // Chưa implement
    // }

    public List<MedicalRequestDTO> getAllMedicalRequestByStatusConfirmed() {
        List<MedicalRequestDTO> result = new ArrayList<>();

        List<MedicalRequestEntity> medicalRequestEntityList = medicalRequestRepository.findByStatus(MedicalRequestEntity.MedicalRequestStatus.CONFIRMED);

        if (medicalRequestEntityList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No confirmed medical requests found");
        }

        for (MedicalRequestEntity medicalRequestEntity : medicalRequestEntityList) {
            // Lấy các detail có status NOT_TAKEN
            List<MedicalRequestDetailEntity> notTakenDetails = medicalRequestEntity.getMedicalRequestDetailEntities().stream().filter(d -> d.getStatus() == MedicalRequestDetailEntity.Status.NOT_TAKEN).collect(Collectors.toList());

            // Nhóm theo timeSchedule
            Map<Object, List<MedicalRequestDetailEntity>> groupedByTimeSchedule = notTakenDetails.stream().collect(Collectors.groupingBy(MedicalRequestDetailEntity::getTimeSchedule));

            for (List<MedicalRequestDetailEntity> group : groupedByTimeSchedule.values()) {
                MedicalRequestDTO dto = modelMapper.map(medicalRequestEntity, MedicalRequestDTO.class);

                // Map student, nurse, parent, class như cũ
                StudentEntity studentEntity = medicalRequestEntity.getStudent();
                StudentDTO studentDTO = modelMapper.map(studentEntity, StudentDTO.class);
                ClassEntity classEntity = studentEntity.getClassEntity();
                if (classEntity != null) {
                    ClassDTO classDTO = modelMapper.map(classEntity, ClassDTO.class);
                    classDTO.setStudents(null);
                    studentDTO.setClassDTO(classDTO);
                }
                dto.setStudentDTO(studentDTO);

                try {
                    UserEntity nurse = userRepository.findById(medicalRequestEntity.getNurse().getUserId()).orElseThrow(() -> new UsernameNotFoundException("Nurse not found"));
                    UserDTO nurseDTO = modelMapper.map(nurse, UserDTO.class);
                    dto.setNurseDTO(nurseDTO);
                } catch (Exception e) {
                    dto.setNurseDTO(null);
                }

                dto.setParentDTO(modelMapper.map(studentEntity.getParent(), UserDTO.class));

                // Map các detail trong group
                List<MedicalRequestDetailDTO> detailDTOs = group.stream().map(detail -> modelMapper.map(detail, MedicalRequestDetailDTO.class)).collect(Collectors.toList());
                dto.setMedicalRequestDetailDTO(detailDTOs);

                result.add(dto);
            }
        }
        return result;
    }

    public List<MedicalRequestDTO> getAllMedicalRequestByStatus(String statusStr) {
        if (statusStr == null || statusStr.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status cannot be null or empty");
        }

        if (statusStr.equalsIgnoreCase("CONFIRMED")) {
            return getAllMedicalRequestByStatusConfirmed();
        }

        logger.info("Fetching medical requests with status: {}", statusStr);

        MedicalRequestEntity.MedicalRequestStatus status;
        try {
            status = MedicalRequestEntity.MedicalRequestStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status value");
        }

        List<MedicalRequestEntity> medicalRequestEntityList = medicalRequestRepository.findByStatus(status);
        if (medicalRequestEntityList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No medical requests found");
        }

        List<MedicalRequestDTO> medicalRequestDTOList = new ArrayList<>();
        for (MedicalRequestEntity medicalRequestEntity : medicalRequestEntityList) {
            StudentEntity studentEntity = medicalRequestEntity.getStudent();
            StudentDTO studentDTO = modelMapper.map(studentEntity, StudentDTO.class);

            ClassEntity classEntity = studentEntity.getClassEntity();
            if (classEntity != null) {
                ClassDTO classDTO = modelMapper.map(classEntity, ClassDTO.class);
                classDTO.setStudents(null); // nếu không muốn lấy danh sách học sinh trong class
                studentDTO.setClassDTO(classDTO);
            }

            List<MedicalRequestDetailEntity> medicalRequestDetailEntityList = medicalRequestEntity.getMedicalRequestDetailEntities();

            List<MedicalRequestDetailDTO> medicalRequestDetailDTOList = medicalRequestDetailEntityList.stream().map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity, MedicalRequestDetailDTO.class)).collect(Collectors.toList());

            MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequestEntity, MedicalRequestDTO.class);
            medicalRequestDTO.setStudentDTO(studentDTO);
            if (medicalRequestEntity.getNurse() != null) {
                medicalRequestDTO.setNurseDTO(modelMapper.map(medicalRequestEntity.getNurse(), UserDTO.class));
            }
            medicalRequestDTO.setMedicalRequestDetailDTO(medicalRequestDetailDTOList);
            medicalRequestDTOList.add(medicalRequestDTO);
        }
        return medicalRequestDTOList;
    }

    public List<MedicalRequestDetailDTO> getMedicalRequestDetail(int requestId) {
        Optional<MedicalRequestEntity> medicalRequestOpt = medicalRequestRepository.findById(requestId);
        if (medicalRequestOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn thuốc!");
        }

        MedicalRequestEntity medicalRequest = medicalRequestOpt.get();
        List<MedicalRequestDetailEntity> medicalRequestDetailEntityList = medicalRequest.getMedicalRequestDetailEntities();
        if (medicalRequestDetailEntityList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chi tiết thuốc nào cho đơn thuốc này!");
        }

        return medicalRequestDetailEntityList.stream().map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity, MedicalRequestDetailDTO.class)).collect(Collectors.toList());
    }

    public List<MedicalRequestDetailDTO> updateMedicalRequestDetailStatus(List<UpdateRequestDetailStatusRequest> requests) {
        List<MedicalRequestDetailDTO> result = new ArrayList<>();

        for (UpdateRequestDetailStatusRequest req : requests) {
            MedicalRequestDetailEntity detail = medicalRequestDetailRepository.findById(req.getRequestDetailId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chi tiết thuốc! ID: " + req.getRequestDetailId()));

            detail.setStatus(MedicalRequestDetailEntity.Status.TAKEN);
            detail.setNote(req.getNote()); // nếu có field note
            medicalRequestDetailRepository.save(detail);

            result.add(modelMapper.map(detail, MedicalRequestDetailDTO.class)); // mapper nếu có
        }

        // Kiểm tra nếu tất cả detail của 1 request đã TAKEN => cập nhật status request
        // Giả sử tất cả requestDetailId thuộc cùng 1 MedicalRequest
        if (!requests.isEmpty()) {
            int requestId = medicalRequestDetailRepository.findById(requests.get(0).getRequestDetailId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)).getMedicalRequest().getRequestId();

            List<MedicalRequestDetailEntity> allDetails = medicalRequestDetailRepository.findByMedicalRequest_RequestId(requestId);

            boolean allTaken = allDetails.stream().allMatch(d -> d.getStatus() == MedicalRequestDetailEntity.Status.TAKEN);

            if (allTaken) {
                MedicalRequestEntity request = allDetails.get(0).getMedicalRequest();
                request.setStatus(MedicalRequestEntity.MedicalRequestStatus.COMPLETED);
                medicalRequestRepository.save(request);
            }
        }

        return result;
    }

    public List<MedicalEventDTO> getMedicalEventsByStudent(int studentId) {
        Optional<StudentEntity> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");

        StudentEntity studentEntity = studentOpt.get();
        UserEntity parent = studentEntity.getParent();
        List<MedicalEventEntity> medicalEventEntitieList = medicalEventRepository.findByStudent(studentEntity);
        if (medicalEventEntitieList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found any medical event");
        List<MedicalEventDTO> medicalEventDTOList = medicalEventEntitieList.stream().map(event -> {
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
        }).collect(Collectors.toList());
        return medicalEventDTOList;
    }

    public MedicalRequestDTO updateMedicalRequestStatus(int requestId, UpdateMedicalRequestStatus request, int nurseId) {
        MedicalRequestEntity medicalRequest = medicalRequestRepository.findById(requestId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn gửi thuốc"));

        StudentEntity student = medicalRequest.getStudent();

        MedicalRequestEntity.MedicalRequestStatus currentStatus = medicalRequest.getStatus();
        MedicalRequestEntity.MedicalRequestStatus newStatus;
        try {
            newStatus = MedicalRequestEntity.MedicalRequestStatus.valueOf(request.getStatus().toString());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trạng thái không hợp lệ");
        }

        if (currentStatus == newStatus) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể cập nhật cùng một trạng thái");
        }

        if (currentStatus == MedicalRequestEntity.MedicalRequestStatus.PROCESSING) {
            if (newStatus == MedicalRequestEntity.MedicalRequestStatus.CONFIRMED || newStatus == MedicalRequestEntity.MedicalRequestStatus.CANCELLED) {
                // Nếu hủy phải có lý do
                if (newStatus == MedicalRequestEntity.MedicalRequestStatus.CANCELLED) {
                    if (request.getReason_rejected() == null || request.getReason_rejected().isBlank()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cần cung cấp lý do khi hủy yêu cầu");
                    }
                    medicalRequest.setReasonRejected(request.getReason_rejected());
                    // Gửi thông báo từ chối
                    notificationService.sendNotificationToParent(
                            medicalRequest.getParent().getUserId(),
                            "Yêu cầu gửi thuốc bị từ chối",
                            "Yêu cầu gửi thuốc cho học sinh " + student.getFullName() + " đã bị từ chối. Lý do: " + request.getReason_rejected(),
                            "MEDICAL_REQUEST",
                            medicalRequest.getRequestId(),
                            false
                    );
                }
                // Gửi thông báo xác nhận
                notificationService.sendNotificationToParent(
                        medicalRequest.getParent().getUserId(),
                        "Đã xác nhận yêu cầu gửi thuốc",
                        "Yêu cầu gửi thuốc cho học sinh " + student.getFullName() + " đã được xác nhận.",
                        "MEDICAL_REQUEST",
                        medicalRequest.getRequestId(),
                        false
                );
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ có thể xác nhận đơn thuốc hoặc từ chối đơn thuốc!");
            }
        } else if (currentStatus == MedicalRequestEntity.MedicalRequestStatus.CONFIRMED) {
            if (newStatus != MedicalRequestEntity.MedicalRequestStatus.COMPLETED) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cần hoàn thành đơn thuốc đã xác nhận không thể chuyển sang trạng thái khác!");
            }
            boolean allTaken = medicalRequest.getMedicalRequestDetailEntities().stream().allMatch(detail -> detail.getStatus() == MedicalRequestDetailEntity.Status.TAKEN);
            if (!allTaken) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tất cả thuốc trong đơn phải được cho uống hết thì mới có thể hoàn thành đơn thuốc");
            }
            // Gửi thông báo xác nhận
            notificationService.sendNotificationToParent(
                    medicalRequest.getParent().getUserId(),
                    "Đã cho học sinh uống thuốc thành công",
                    "Học sinh " + student.getFullName() + " đã được uống thuốc. Đơn thuốc: " + medicalRequest.getRequestName(),
                    "MEDICAL_REQUEST",
                    medicalRequest.getRequestId(),
                    false
            );
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể chuyển trạng thái từ " + currentStatus + " sang " + newStatus);
        }

        medicalRequest.setStatus(newStatus);
        medicalRequest.setNurse(userRepository.findById(nurseId).get());
        medicalRequestRepository.save(medicalRequest);

        MedicalRequestDTO dto = modelMapper.map(medicalRequest, MedicalRequestDTO.class);
        dto.setStudentDTO(modelMapper.map(student, StudentDTO.class));

        List<MedicalRequestDetailDTO> detailDTOs = medicalRequest.getMedicalRequestDetailEntities().stream().map(detail -> modelMapper.map(detail, MedicalRequestDetailDTO.class)).collect(Collectors.toList());
        dto.setMedicalRequestDetailDTO(detailDTOs);

        dto.setNurseDTO(modelMapper.map(userRepository.findById(nurseId).get(), UserDTO.class));
        dto.setParentDTO(modelMapper.map(medicalRequest.getParent(), UserDTO.class));
        return dto;
    }


    public HealthCheckFormDTO getHealthCheckFormById(int healthCheckFormId) {
        HealthCheckFormEntity formEntity = healthCheckFormRepository.findById(healthCheckFormId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy health check form id"));

        HealthCheckFormDTO formDTO = new HealthCheckFormDTO();
        formDTO.setId(formEntity.getId());
        formDTO.setExpDate(formEntity.getExpDate());
        formDTO.setNotes(formEntity.getNotes());
        formDTO.setCommit(formEntity.getCommit());
        formDTO.setStudentId(formEntity.getStudent().getId());
        formDTO.setParentId(formEntity.getParent().getUserId());
        formDTO.setNurseId(formEntity.getNurse().getUserId());

        StudentEntity student = formEntity.getStudent();
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setStudentId(student.getId());
        studentDTO.setFullName(student.getFullName());
        studentDTO.setDob(student.getDob());
        studentDTO.setGender(student.getGender().toString());
        studentDTO.setClassId(student.getClassEntity().getClassId());
        studentDTO.setParentId(student.getParent().getUserId());

        ClassEntity clazz = student.getClassEntity();
        ClassDTO classDTO = new ClassDTO();
        classDTO.setClassId(clazz.getClassId());
        classDTO.setClassName(clazz.getClassName());
        classDTO.setTeacherName(clazz.getTeacherName());
        classDTO.setQuantity(clazz.getQuantity());

        studentDTO.setClassDTO(classDTO);
        formDTO.setStudentDTO(studentDTO);

        UserEntity parent = formEntity.getParent();
        if (parent != null) {
            UserDTO parentDTO = new UserDTO();
            parentDTO.setId(parent.getUserId());
            parentDTO.setFullName(parent.getFullName());
            parentDTO.setEmail(parent.getEmail());
            parentDTO.setPassword(parent.getPassword());
            parentDTO.setPhone(parent.getPhone());
            parentDTO.setRelationship(parent.getRelationship());
            parentDTO.setAddress(parent.getAddress());
            parentDTO.setRole(parent.getRole());

            formDTO.setParentDTO(parentDTO);
            studentDTO.setParentDTO(parentDTO);
        }

        UserEntity nurse = formEntity.getNurse();
        if (nurse != null) {
            UserDTO nurseDTO = new UserDTO();
            nurseDTO.setId(nurse.getUserId());
            nurseDTO.setFullName(nurse.getFullName());
            nurseDTO.setEmail(nurse.getEmail());
            nurseDTO.setPassword(nurse.getPassword());
            nurseDTO.setPhone(nurse.getPhone());
            nurseDTO.setRelationship(nurse.getRelationship());
            nurseDTO.setAddress(nurse.getAddress());
            nurseDTO.setRole(nurse.getRole());
            nurseDTO.setStudentDTOs(null);

            formDTO.setNurseDTO(nurseDTO);
        }

        HealthCheckProgramEntity program = formEntity.getHealthCheckProgram();
        HealthCheckProgramDTO programDTO = new HealthCheckProgramDTO();
        programDTO.setId(program.getId());
        programDTO.setHealthCheckName(program.getHealthCheckName());
        programDTO.setDescription(program.getDescription());
        programDTO.setDateSendForm(program.getDateSendForm());
        programDTO.setStartDate(program.getStartDate());
        programDTO.setStatus(program.getStatus().toString());
        programDTO.setLocation(program.getLocation());
        programDTO.setAdminId(program.getAdmin().getUserId());
        programDTO.setNurseId(program.getNurse().getUserId());

        UserEntity admin = program.getAdmin();
        UserDTO adminDTO = new UserDTO();
        adminDTO.setId(admin.getUserId());
        adminDTO.setFullName(admin.getFullName());
        adminDTO.setEmail(admin.getEmail());
        adminDTO.setPassword(admin.getPassword());
        adminDTO.setPhone(admin.getPhone());
        adminDTO.setRelationship(admin.getRelationship());
        adminDTO.setAddress(admin.getAddress());
        adminDTO.setRole(admin.getRole());
        adminDTO.setStudentDTOs(null);

        programDTO.setAdminDTO(adminDTO);

        UserDTO nurseDTO = new UserDTO();
        nurseDTO.setId(nurse.getUserId());
        nurseDTO.setFullName(nurse.getFullName());
        nurseDTO.setEmail(nurse.getEmail());
        nurseDTO.setPassword(nurse.getPassword());
        nurseDTO.setPhone(nurse.getPhone());
        nurseDTO.setRelationship(nurse.getRelationship());
        nurseDTO.setAddress(nurse.getAddress());
        nurseDTO.setRole(nurse.getRole());
        nurseDTO.setStudentDTOs(null);

        programDTO.setNurseDTO(nurseDTO);

        formDTO.setHealthCheckProgramDTO(programDTO);

        return formDTO;
    }

    public List<HealthCheckFormDTO> getHealthCheckFormsByProgram(int programId, Boolean committed) {
        List<HealthCheckFormEntity> healthCheckForms;

        if (Boolean.TRUE.equals(committed)) {
            healthCheckForms = healthCheckFormRepository.findByCommitTrueAndHealthCheckProgram_Id(programId);
        } else {
            healthCheckForms = healthCheckFormRepository.findByHealthCheckProgram_Id(programId);
        }

        if (healthCheckForms.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phiếu khám sức khỏe");
        }

        return healthCheckForms.stream().map(entity -> {
            HealthCheckFormDTO dto = modelMapper.map(entity, HealthCheckFormDTO.class);
            dto.setStudentId(entity.getStudent().getId());
            dto.setParentId(entity.getParent().getUserId());
            dto.setNurseId(entity.getNurse() != null ? entity.getNurse().getUserId() : null);
            dto.setStudentDTO(modelMapper.map(entity.getStudent(), StudentDTO.class));
            dto.setParentDTO(modelMapper.map(entity.getParent(), UserDTO.class));
            if (entity.getNurse() != null) {
                dto.setNurseDTO(modelMapper.map(entity.getNurse(), UserDTO.class));
            }
            dto.setHealthCheckProgramDTO(modelMapper.map(entity.getHealthCheckProgram(), HealthCheckProgramDTO.class));
            return dto;
        }).collect(Collectors.toList());
    }


    public List<VaccineFormDTO> getVaccineFormsByProgram(int programId, Boolean committed) {
        List<VaccineFormEntity> vaccineForms;

        if (Boolean.TRUE.equals(committed)) {
            vaccineForms = vaccineFormRepository.findByCommitTrueAndVaccineProgram_VaccineId(programId);
        } else {
            vaccineForms = vaccineFormRepository.findByVaccineProgram_VaccineId(programId);
        }

        if (vaccineForms.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phiếu tiêm chủng");
        }

        return vaccineForms.stream().map(entity -> {
            VaccineFormDTO dto = modelMapper.map(entity, VaccineFormDTO.class);
            dto.setStudentID(entity.getStudent().getId());
            dto.setParentID(entity.getParent().getUserId());
            dto.setNurseID(entity.getNurse() != null ? entity.getNurse().getUserId() : null);
            dto.setVaccineProgramID(entity.getVaccineProgram().getVaccineId());

            dto.setStudentDTO(modelMapper.map(entity.getStudent(), StudentDTO.class));
            dto.setParentDTO(modelMapper.map(entity.getParent(), UserDTO.class));
            if (entity.getNurse() != null) {
                dto.setNurseDTO(modelMapper.map(entity.getNurse(), UserDTO.class));
            }

            dto.setVaccineProgramDTO(modelMapper.map(entity.getVaccineProgram(), VaccineProgramDTO.class));
            dto.setVaccineNameDTO(modelMapper.map(entity.getVaccineProgram().getVaccineName(), VaccineNameDTO.class));
            return dto;
        }).collect(Collectors.toList());
    }


    // public Map<String, int> countDraftFormByProgram(int programId) {
    // int vaccineForm =
    // vaccineFormRepository.countByVaccineProgram_IdAndStatusAndCommitFalse(programId,
    // VaccineFormStatus.DRAFT);
    // int healthCheckForm =
    // healthCheckFormRepository.countByHealthCheckProgram_IdAndStatusAndCommitFalse(programId,
    // HealthCheckFormStatus.DRAFT);
    // return Map.of(
    // "vaccineForm", vaccineForm,
    // "healthCheckForm", healthCheckForm);
    // }

    public VaccineFormDTO getVaccinFormById(int vaccineFormId) {
        Optional<VaccineFormEntity> vaccineFormOpt = vaccineFormRepository.findById(vaccineFormId);

        if (vaccineFormOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phiếu tiêm chủng");
        }

        VaccineFormEntity entity = vaccineFormOpt.get();

        VaccineFormDTO dto = new VaccineFormDTO();
        dto.setId(entity.getId());
        dto.setStudentID(entity.getStudent().getId());
        dto.setParentID(entity.getParent().getUserId());
        dto.setNurseID(entity.getNurse() != null ? entity.getNurse().getUserId() : 0);
        dto.setVaccineProgramID(entity.getVaccineProgram().getVaccineId());
        dto.setExpDate(entity.getExpDate());
        dto.setNote(entity.getNote());
        dto.setCommit(entity.getCommit());

        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setStudentId(entity.getStudent().getId());
        studentDTO.setFullName(entity.getStudent().getFullName());
        studentDTO.setDob(entity.getStudent().getDob());
        studentDTO.setGender(entity.getStudent().getGender().name());
        studentDTO.setClassId(entity.getStudent().getClassEntity().getClassId());
        studentDTO.setParentId(entity.getStudent().getParent().getUserId());

        UserDTO parentInStudent = new UserDTO();
        parentInStudent.setId(entity.getStudent().getParent().getUserId());
        parentInStudent.setFullName(entity.getStudent().getParent().getFullName());
        studentDTO.setParentDTO(parentInStudent);

        dto.setStudentDTO(studentDTO);

        UserDTO parentDTO = new UserDTO();
        parentDTO.setId(entity.getParent().getUserId());
        parentDTO.setFullName(entity.getParent().getFullName());
        dto.setParentDTO(parentDTO);

        if (entity.getNurse() != null) {
            UserDTO nurseDTO = new UserDTO();
            nurseDTO.setId(entity.getNurse().getUserId());
            nurseDTO.setFullName(entity.getNurse().getFullName());
            dto.setNurseDTO(nurseDTO);
        }

        VaccineProgramDTO vaccineProgramDTO = new VaccineProgramDTO();
        vaccineProgramDTO.setVaccineProgramId(entity.getVaccineProgram().getVaccineId());
        vaccineProgramDTO.setVaccineProgramName(entity.getVaccineProgram().getVaccineProgramName());
        vaccineProgramDTO.setDescription(entity.getVaccineProgram().getDescription());
        vaccineProgramDTO.setVaccineId(entity.getVaccineProgram().getVaccineName().getVaccineNameId());
        vaccineProgramDTO.setUnit(entity.getVaccineProgram().getUnit());
        vaccineProgramDTO.setStartDate(entity.getVaccineProgram().getStartDate());
        vaccineProgramDTO.setDateSendForm(entity.getVaccineProgram().getDateSendForm());
        vaccineProgramDTO.setLocation(entity.getVaccineProgram().getLocation());
        vaccineProgramDTO.setNurseId(entity.getVaccineProgram().getNurse().getUserId());
        vaccineProgramDTO.setAdminId(entity.getVaccineProgram().getAdmin().getUserId());

        UserDTO nurseInProgram = new UserDTO();
        nurseInProgram.setId(entity.getVaccineProgram().getNurse().getUserId());
        nurseInProgram.setFullName(entity.getVaccineProgram().getNurse().getFullName());
        vaccineProgramDTO.setNurseDTO(nurseInProgram);

        UserDTO adminInProgram = new UserDTO();
        adminInProgram.setId(entity.getVaccineProgram().getAdmin().getUserId());
        adminInProgram.setFullName(entity.getVaccineProgram().getAdmin().getFullName());
        vaccineProgramDTO.setAdminDTO(adminInProgram);

        dto.setVaccineProgramDTO(vaccineProgramDTO);

        VaccineNameDTO vaccineNameDTO = new VaccineNameDTO();
        vaccineNameDTO.setId(entity.getVaccineName().getVaccineNameId());
        vaccineNameDTO.setVaccineName(entity.getVaccineName().getVaccineName());
        vaccineNameDTO.setManufacture(entity.getVaccineName().getManufacture());
        vaccineNameDTO.setAgeFrom(entity.getVaccineName().getAgeFrom());
        vaccineNameDTO.setAgeTo(entity.getVaccineName().getAgeTo());
        vaccineNameDTO.setTotalUnit(entity.getVaccineName().getTotalUnit());
        vaccineNameDTO.setUrl(entity.getVaccineName().getUrl());
        vaccineNameDTO.setDescription(entity.getVaccineName().getDescription());

        UserDTO vaccineCreatorDTO = new UserDTO();
        vaccineCreatorDTO.setId(entity.getVaccineName().getUser().getUserId());
        vaccineCreatorDTO.setFullName(entity.getVaccineName().getUser().getFullName());
        vaccineNameDTO.setUserDTO(vaccineCreatorDTO);

        dto.setVaccineNameDTO(vaccineNameDTO);

        return dto;
    }


    public MedicalEventDTO createMedicalEvent(int nurseId, MedicalEventRequest request, MultipartFile image) {
        Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        if (nurseOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Y tá không tồn tại!");
        }

        UserEntity nurse = nurseOpt.get();

        Optional<StudentEntity> studentOpt = studentRepository.findStudentById(request.getStudentId());
        if (studentOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy học sinh!");
        }

        StudentEntity student = studentOpt.get();

        String imageUrl = null;
        try {
            imageUrl = uploadImageFile.uploadImage(image);
        } catch (Exception e) {
            // TODO: handle exception
        }

        logger.info("Image uploaded successfully: {}", imageUrl);

        Optional<MedicalEventEntity> medicalEventOpt = medicalEventRepository.findByTypeEventAndDateAndDescriptionAndLevelCheckAndLocationAndImageAndStudent(request.getTypeEvent(), request.getDate(), request.getDescription(), MedicalEventEntity.LevelCheck.valueOf(request.getLevelCheck()), request.getLocation(), imageUrl, student);
        if (medicalEventOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sự kiện y tế này đã tồn tại!");
        }

        MedicalEventEntity medicalEventEntity = new MedicalEventEntity();
        // medicalEventEntity.setEventName(request.getEventName());
        medicalEventEntity.setTypeEvent(request.getTypeEvent());
        medicalEventEntity.setDate(request.getDate());
        medicalEventEntity.setActionsTaken(request.getActionsTaken());
        medicalEventEntity.setDescription(request.getDescription());
        medicalEventEntity.setLevelCheck(MedicalEventEntity.LevelCheck.valueOf(request.getLevelCheck()));
        medicalEventEntity.setLocation(request.getLocation());
        medicalEventEntity.setImage(imageUrl); // Lưu đường dẫn hoặc tên file ảnh
        medicalEventEntity.setStudent(student);
        medicalEventEntity.setNurse(nurse);
        medicalEventRepository.save(medicalEventEntity);
        /*Gui thong bao */
        notificationService.sendNotificationToParent(
                student.getParent().getUserId(),
                "Thông báo sự cố",
                "Học sinh " + student.getFullName() + " bị " + medicalEventEntity.getTypeEvent() + " tại " + medicalEventEntity.getLocation(),
                "MEDICAL_EVENT",
                medicalEventEntity.getEventId(),
                false
        );
        /*-------------*/
        MedicalEventDTO dto = modelMapper.map(medicalEventEntity, MedicalEventDTO.class);
        dto.setStudentDTO(modelMapper.map(student, StudentDTO.class));
        dto.setParentDTO(modelMapper.map(student.getParent(), UserDTO.class));
        dto.setNurseDTO(modelMapper.map(nurse, UserDTO.class));
        dto.setClassDTO(modelMapper.map(student.getClassEntity(), ClassDTO.class));

        sendMedicalEventEmail(student, request);
        return dto;
    }


    private void sendMedicalEventEmail(StudentEntity student, MedicalEventRequest request) {
        String to = student.getParent().getEmail();
        String subject = "Thông báo sự cố y tế của học sinh " + student.getFullName();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = request.getDate().format(formatter);

        String levelLabel;
        String levelColor;
        switch (request.getLevelCheck()) {
            case "LOW" -> {
                levelLabel = "Thấp";
                levelColor = "#2ecc71"; // xanh lá
            }
            case "MEDIUM" -> {
                levelLabel = "Trung bình";
                levelColor = "#f39c12"; // vàng cam
            }
            case "HIGH" -> {
                levelLabel = "Cao";
                levelColor = "#e74c3c"; // đỏ
            }
            default -> {
                levelLabel = request.getLevelCheck();
                levelColor = "#7f8c8d"; // xám mặc định
            }
        }

        String htmlContent = """
                    <div style="font-family: Arial, sans-serif; font-size: 14px;">
                        <p>Phụ huynh thân mến,</p>
                        <p>Hệ thống vừa ghi nhận một sự cố y tế liên quan đến học sinh: <strong>%s</strong>.</p>
                        <ul>
                            <li><strong>Sự cố:</strong> %s</li>
                            <li><strong>Mô tả:</strong> %s</li>
                            <li><strong>Thời gian xảy ra:</strong> %s</li>
                            <li><strong>Địa điểm:</strong> %s</li>
                            <li><strong>Mức độ nguy hiểm:</strong> <span style="color:%s; font-weight:bold;">%s</span></li>
                        </ul>
                        <p>Vui lòng đăng nhập hệ thống để biết thêm chi tiết.</p>
                    </div>
                """.formatted(student.getFullName(), request.getTypeEvent(), request.getDescription(), formattedDate, request.getLocation(), levelColor, levelLabel);

        emailService.sendEmail(to, subject, htmlContent);
    }


    public MedicalEventDTO updateMedicalEvent(int nurseId, int medicalEventId, MedicalEventRequest request, MultipartFile image) {
        Optional<MedicalEventEntity> medicalEventOpt = medicalEventRepository.findById(medicalEventId);
        if (medicalEventOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sự kiện y tế không tồn tại!");
        }


        MedicalEventEntity medicalEvent = medicalEventOpt.get();


        if (medicalEvent.getNurse() == null || medicalEvent.getNurse().getUserId() != nurseId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền chỉnh sửa, sự kiện y tế này do y tá '" + medicalEventOpt.get().getNurse().getFullName() + "' đảm nhiệm!");
        }


        Optional<StudentEntity> studentOpt = studentRepository.findStudentById(request.getStudentId());
        if (studentOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy học sinh!");
        }
        StudentEntity student = studentOpt.get();


        // Cập nhật các trường
        // medicalEvent.setEventName(request.getEventName());
        medicalEvent.setTypeEvent(request.getTypeEvent());
        medicalEvent.setDate(request.getDate());
        medicalEvent.setActionsTaken(request.getActionsTaken());
        medicalEvent.setDescription(request.getDescription());
        medicalEvent.setLevelCheck(MedicalEventEntity.LevelCheck.valueOf(request.getLevelCheck()));
        medicalEvent.setLocation(request.getLocation());
        medicalEvent.setStudent(student);


        // Xử lý cập nhật ảnh nếu có file mới
        if (image != null && !image.isEmpty()) {
            String imageUrl = null;
            try {
                imageUrl = uploadImageFile.uploadImage(image);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi upload ảnh mới");
            }
            medicalEvent.setImage(imageUrl);
        }


        medicalEventRepository.save(medicalEvent);


        MedicalEventDTO dto = modelMapper.map(medicalEvent, MedicalEventDTO.class);
        dto.setStudentDTO(modelMapper.map(student, StudentDTO.class));
        dto.setParentDTO(modelMapper.map(student.getParent(), UserDTO.class));
        dto.setNurseDTO(modelMapper.map(medicalEvent.getNurse(), UserDTO.class));
        dto.setClassDTO(modelMapper.map(student.getClassEntity(), ClassDTO.class));
        return dto;
    }


    public MedicalEventDTO getMedicalEvent(int medicalEventId) {
        // Optional<MedicalEventEntity> medicalEventOpt =
        // medicalEventRepository.findByEventId(medicalEventId);
        // if (medicalEventOpt.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical event not
        // found");
        // MedicalEventEntity medicalEventEntity = medicalEventOpt.get();
        // MedicalEventDTO medicalEventDTO = modelMapper.map(medicalEventEntity,
        // MedicalEventDTO.class);
        // if (medicalEventEntity.getNurse() != null) {
        // UserDTO nurseDTO = modelMapper.map(medicalEventEntity.getNurse(),
        // UserDTO.class);
        // medicalEventDTO.setNurseDTO(nurseDTO);
        // }
        // if (medicalEventEntity.getStudent() != null) {
        // StudentDTO studentDTO = modelMapper.map(medicalEventEntity.getStudent(),
        // StudentDTO.class);
        // medicalEventDTO.setStudentDTO(studentDTO);

        // if (medicalEventEntity.getStudent().getParent() != null) {
        // UserDTO parentDTO =
        // modelMapper.map(medicalEventEntity.getStudent().getParent(), UserDTO.class);
        // medicalEventDTO.setParentDTO(parentDTO);
        // }
        // }
        // return medicalEventDTO;
        return null;
    }

    public List<MedicalEventDTO> getAllMedicalEvent() {
        List<MedicalEventEntity> events = medicalEventRepository.findAll();

        if (events.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sự kiện y tế.");
        }

        List<MedicalEventDTO> medicalEventDTOList = new ArrayList<>();

        for (MedicalEventEntity event : events) {
            MedicalEventDTO dto = new MedicalEventDTO();

            dto.setEventId(event.getEventId());
            dto.setTypeEvent(event.getTypeEvent());
//            dto.setEventName(event.getEventName());
            dto.setDate(event.getDate());
            dto.setDescription(event.getDescription());
            dto.setActionsTaken(event.getActionsTaken());
            dto.setImage(event.getImage());
            dto.setLocation(event.getLocation());

            dto.setLevelCheck(event.getLevelCheck() != null ? event.getLevelCheck().name() : null);

            if (event.getNurse() != null) {
                UserEntity nurse = event.getNurse();
                dto.setNurseDTO(modelMapper.map(nurse, UserDTO.class));
                dto.setNurseId(nurse.getUserId());
            }

            StudentEntity student = event.getStudent();
            if (student != null) {
                dto.setStudentDTO(modelMapper.map(student, StudentDTO.class));
                dto.setStudentId(student.getId());

                ClassEntity classEntity = student.getClassEntity();
                if (classEntity != null) {
                    ClassDTO classDTO = modelMapper.map(classEntity, ClassDTO.class);
                    classDTO.setStudents(null);
                    dto.setClassDTO(classDTO);
                }

                UserEntity parent = student.getParent();
                if (parent != null) {
                    UserDTO parentDTO = UserDTO.builder().id(parent.getUserId()).fullName(parent.getFullName()).email(parent.getEmail()).phone(parent.getPhone()).address(parent.getAddress()).role(parent.getRole()).build();
                    dto.setParentDTO(parentDTO);
                }
            }
            medicalEventDTOList.add(dto);
        }
        return medicalEventDTOList;
    }

    public void deleteMedicalEvent(int medicalEventId, int requesterId) {
        Optional<MedicalEventEntity> medicalEventOpt = medicalEventRepository.findById(medicalEventId);
        if (medicalEventOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sự kiện y tế");
        }

        MedicalEventEntity medicalEvent = medicalEventOpt.get();

        Optional<UserEntity> userOpt = userRepository.findUserByUserId(requesterId);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Người yêu cầu không tồn tại");
        }

        UserEntity requester = userOpt.get();
        boolean isAdmin = requester.getRole().equals(UserEntity.UserRole.ADMIN);

        // Nếu không phải admin và không phải y tá tạo ra event
        if (!isAdmin && (medicalEvent.getNurse() == null || medicalEvent.getNurse().getUserId() != requesterId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xóa! Sự kiện y tế này do y tá '" + (medicalEvent.getNurse() != null ? medicalEvent.getNurse().getFullName() : "khác") + "' đảm nhiệm!");
        }

        medicalEventRepository.delete(medicalEvent);
    }


    public HealthCheckResultDTO createHealthCheckResult(HealthCheckResultRequest request) {

        // Optional<HealthCheckFormEntity> healCheckFormOpt = healthCheckFormRepository
        // .findById(request.getHealthCheckFormId());
        // if (healCheckFormOpt.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check form
        // not found");
        // HealthCheckFormEntity healthCheckFormEntity = healCheckFormOpt.get();

        // if (healthCheckFormEntity.getCommit() == null ||
        // !healthCheckFormEntity.getCommit()) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        // "Parent has not committed the health check form yet");
        // }

        // HealthCheckFormDTO healthCheckFormDTO =
        // modelMapper.map(healthCheckFormEntity, HealthCheckFormDTO.class);

        // Optional<HealthCheckResultEntity> existingResultOpt =
        // healthCheckResultRepository
        // .findByHealthCheckFormEntity(healthCheckFormEntity);
        // if (existingResultOpt.isPresent()) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        // "Health check result already exists for this student: "
        // + healthCheckFormEntity.getStudent().getId());
        // }

        // HealthCheckResultEntity healthCheckResultEntity = new
        // HealthCheckResultEntity();
        // healthCheckResultEntity.setDiagnosis(request.getDiagnosis());
        // healthCheckResultEntity.setLevel(HealthCheckResultEntity.Level.valueOf(request.getLevel().toUpperCase()));
        // healthCheckResultEntity.setNote(request.getNote());
        // healthCheckResultEntity.setHealthCheckFormEntity(healthCheckFormEntity);
        // healthCheckResultRepository.save(healthCheckResultEntity);

        // HealthCheckResultDTO healthCheckResultDTO =
        // modelMapper.map(healthCheckResultEntity,
        // HealthCheckResultDTO.class);
        // healthCheckResultDTO.setHealthCheckFormDTO(healthCheckFormDTO);
        // healthCheckResultDTO.setStudentDTO(
        // modelMapper.map(healthCheckFormEntity.getStudent(), StudentDTO.class));

        // return healthCheckResultDTO;
        return null;
    }

    public List<HealthCheckResultDTO> createDefaultHealthCheckResultsForAllCommittedForms() {
        // List<HealthCheckFormEntity> committedForms =
        // healthCheckFormRepository.findByCommitTrue();
        // List<HealthCheckResultDTO> resultList = new ArrayList<>();

        // for (HealthCheckFormEntity form : committedForms) {
        // boolean exists =
        // healthCheckResultRepository.findByHealthCheckFormEntity(form).isPresent();
        // if (exists)
        // continue;

        // HealthCheckResultRequest defaultRequest = new HealthCheckResultRequest();
        // defaultRequest.setDiagnosis("GOOD");
        // defaultRequest.setLevel("GOOD");
        // defaultRequest.setNote("No problem");
        // defaultRequest.setHealthCheckFormId(form.getId());

        // try {
        // HealthCheckResultDTO resultDTO = createHealthCheckResult(defaultRequest);
        // resultList.add(resultDTO);
        // } catch (ResponseStatusException ex) {
        // System.out.println("Lỗi tạo result cho formId=" + form.getId() + ": " +
        // ex.getReason());
        // }
        // }
        // return resultList;
        return null;
    }

    public List<HealthCheckResultDTO> createDefaultHealthCheckResultsByProgramId(int programId) {
        // List<HealthCheckFormEntity> committedForms =
        // healthCheckFormRepository.findCommittedFormsByProgramId(programId);
        // List<HealthCheckResultDTO> resultList = new ArrayList<>();

        // for (HealthCheckFormEntity form : committedForms) {
        // boolean exists =
        // healthCheckResultRepository.findByHealthCheckFormEntity(form).isPresent();
        // if (exists)
        // continue;

        // HealthCheckResultRequest defaultRequest = new HealthCheckResultRequest();
        // defaultRequest.setDiagnosis("GOOD");
        // defaultRequest.setLevel("GOOD");
        // defaultRequest.setNote("No problem");
        // defaultRequest.setHealthCheckFormId(form.getId());

        // try {
        // HealthCheckResultDTO resultDTO = createHealthCheckResult(defaultRequest);
        // resultList.add(resultDTO);
        // } catch (ResponseStatusException ex) {
        // System.out.println("Lỗi tạo result cho formId=" + form.getId() + ": " +
        // ex.getReason());
        // }
        // }

        // return resultList;
        return null;
    }

    public HealthCheckResultDTO updateHealthCheckResult(int healthCheckResultId, HealthCheckResultRequest request) {
        HealthCheckResultEntity healthCheckResultEntity = healthCheckResultRepository.findById(healthCheckResultId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy kết quả khám sức khỏe"));

        HealthCheckFormEntity healthCheckFormEntity = healthCheckFormRepository.findById(request.getHealthCheckFormId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy form khám sức khỏe"));

        if (healthCheckFormEntity.getCommit() == null || !healthCheckFormEntity.getCommit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phụ huynh chưa commit phiếu khám sức khỏe");
        }

        healthCheckResultEntity.setNote(request.getNote());
        healthCheckResultEntity.setVision(request.getVision());
        healthCheckResultEntity.setHearing(request.getHearing());
        healthCheckResultEntity.setWeight(request.getWeight());
        healthCheckResultEntity.setHeight(request.getHeight());
        healthCheckResultEntity.setDentalStatus(request.getDentalStatus());
        healthCheckResultEntity.setBloodPressure(request.getBloodPressure());
        healthCheckResultEntity.setHeartRate(request.getHeartRate());
        healthCheckResultEntity.setGeneralCondition(request.getGeneralCondition());
        healthCheckResultEntity.setIsChecked(request.getIsChecked());
        healthCheckResultEntity.setHealthCheckForm(healthCheckFormEntity);
        healthCheckResultEntity.setStudent(healthCheckFormEntity.getStudent());

        healthCheckResultRepository.save(healthCheckResultEntity);

        try {
            MedicalRecordEntity medicalRecordEntity = medicalRecordsRepository.findMedicalRecordByStudent_Id(healthCheckFormEntity.getStudent().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy hồ sơ y tế của học sinh"));

            medicalRecordEntity.setVision(healthCheckResultEntity.getVision());
            medicalRecordEntity.setHearing(healthCheckResultEntity.getHearing());
            medicalRecordEntity.setWeight(healthCheckResultEntity.getWeight());
            medicalRecordEntity.setHeight(healthCheckResultEntity.getHeight());
            medicalRecordEntity.setNote(healthCheckResultEntity.getNote());
            medicalRecordEntity.setLastUpdate(LocalDateTime.now());
            medicalRecordEntity.setCreateBy(true);

            medicalRecordsRepository.save(medicalRecordEntity);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi cập nhật hồ sơ y tế", e);
        }

        HealthCheckResultDTO dto = modelMapper.map(healthCheckResultEntity, HealthCheckResultDTO.class);
        dto.setHealthCheckFormDTO(modelMapper.map(healthCheckFormEntity, HealthCheckFormDTO.class));
        dto.setStudentDTO(modelMapper.map(healthCheckFormEntity.getStudent(), StudentDTO.class));
        return dto;
    }

    public HealthCheckResultDTO getHealthCheckResult(int healthCheckResultId) {
        // HealthCheckResultEntity healthCheckResultEntity = healthCheckResultRepository
        // .findByHealthResultId(healthCheckResultId)
        // .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Health
        // check result not found"));
        // HealthCheckResultDTO dto = modelMapper.map(healthCheckResultEntity,
        // HealthCheckResultDTO.class);

        // HealthCheckFormEntity formEntity =
        // healthCheckResultEntity.getHealthCheckFormEntity();
        // HealthCheckFormDTO formDTO = modelMapper.map(formEntity,
        // HealthCheckFormDTO.class);
        // dto.setHealthCheckFormDTO(formDTO);

        // StudentEntity student = formEntity.getStudent();
        // StudentDTO studentDTO = modelMapper.map(student, StudentDTO.class);

        // if (student.getClassEntity() != null) {
        // studentDTO.setClassDTO(modelMapper.map(student.getClassEntity(),
        // ClassDTO.class));
        // }

        // if (student.getParent() != null) {
        // studentDTO.setUserDTO(modelMapper.map(student.getParent(), UserDTO.class));
        // }

        // dto.setStudentDTO(studentDTO);

        // return dto;
        return null;
    }

    public List<HealthCheckResultDTO> getHealthCheckResultByProgram(int programId) {
        List<HealthCheckResultEntity> resultEntityList = healthCheckResultRepository.findByHealthCheckForm_HealthCheckProgram_Id(programId);

        if (resultEntityList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No health check result found for this program");
        }

        List<HealthCheckResultDTO> dtoList = new ArrayList<>();

        for (HealthCheckResultEntity entity : resultEntityList) {
            HealthCheckResultDTO dto = modelMapper.map(entity, HealthCheckResultDTO.class);

            HealthCheckFormEntity form = entity.getHealthCheckForm();
            HealthCheckFormDTO formDTO = modelMapper.map(form, HealthCheckFormDTO.class);
            dto.setHealthCheckFormDTO(formDTO);

            StudentEntity student = form.getStudent();
            StudentDTO studentDTO = modelMapper.map(student, StudentDTO.class);
            if (student.getClassEntity() != null)
                studentDTO.setClassDTO(modelMapper.map(student.getClassEntity(), ClassDTO.class));
            if (student.getParent() != null)
                studentDTO.setParentDTO(modelMapper.map(student.getParent(), UserDTO.class));
            dto.setStudentDTO(studentDTO);

            if (form.getNurse() != null) dto.setNurseDTO(modelMapper.map(form.getNurse(), UserDTO.class));

            dtoList.add(dto);
        }

        return dtoList;
    }

    public void deleteHealthCheckResult(int healthCheckResultId) {
        // Optional<HealthCheckResultEntity> healthCheckResultOpt =
        // healthCheckResultRepository
        // .findByHealthResultId(healthCheckResultId);
        // if (healthCheckResultOpt.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check result
        // not found");
        // HealthCheckResultEntity healthCheckResultEntity = healthCheckResultOpt.get();
        // healthCheckResultRepository.delete(healthCheckResultEntity);

    }

//    public VaccineResultDTO createVaccineResult(VaccineResultRequest request) {
//
//        // Optional<VaccineFormEntity> vaccineFormOpt =
//        // vaccineFormRepository.findById(request.getVaccineFormId());
//        // if (vaccineFormOpt.isEmpty())
//        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine form not
//        // found");
//
//        // VaccineFormEntity vaccineFormEntity = vaccineFormOpt.get();
//
//        // if (Boolean.FALSE.equals(vaccineFormEntity.getCommit())) {
//        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent has not
//        // committed the vaccine form yet");
//        // }
//
//        // Optional<VaccineResultEntity> existingResultOpt = vaccineResultRepository
//        // .findByVaccineFormEntity(vaccineFormEntity);
//        // if (existingResultOpt.isPresent()) {
//        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//        // "Vaccine result already exists for this student: " +
//        // vaccineFormEntity.getStudent().getId());
//        // }
//
//        // int studentId = vaccineFormEntity.getStudent().getId();
//        // MedicalRecordEntity medicalRecordEntity = medicalRecordsRepository
//        // .findMedicalRecordByStudent_Id(studentId)
//        // .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
//        // "Not found medical record by student"));
//
//        // VaccineResultEntity vaccineResultEntity = new VaccineResultEntity();
//        // vaccineResultEntity.setStatusHealth(request.getStatusHealth());
//        // vaccineResultEntity.setResultNote(request.getResultNote());
//        // vaccineResultEntity.setReaction(request.getReaction());
//        // vaccineResultEntity.setCreatedAt(LocalDateTime.now());
//        // vaccineResultEntity.setVaccineFormEntity(vaccineFormEntity);
//        // vaccineResultRepository.save(vaccineResultEntity);
//
//        // VaccineProgramEntity program = vaccineFormEntity.getVaccineProgram();
//
//        // VaccineHistoryEntity history = new VaccineHistoryEntity();
//        // VaccineNameEntity vaccineNameEntity = program.getVaccineName();
//
//        // if (vaccineNameEntity == null) {
//        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//        // "Vaccine program missing vaccine name entity");
//        // }
//
//        // history.setVaccineNameEntity(vaccineNameEntity);
//        // history.setNote(DEFAULT_VACCINE_HS_NOTE);
//        // history.setMedicalRecord(medicalRecordEntity);
//
//        // vaccineHistoryRepository.save(history);
//
//        // VaccineResultDTO vaccineResultDTO = modelMapper.map(vaccineResultEntity,
//        // VaccineResultDTO.class);
//        // vaccineResultDTO.setVaccineFormDTO(modelMapper.map(vaccineFormEntity,
//        // VaccineFormDTO.class));
//        // vaccineResultDTO.setStudentDTO(modelMapper.map(vaccineFormEntity.getStudent(),
//        // StudentDTO.class));
//        // return vaccineResultDTO;
//        return null;
//    }

    public VaccineFormDTO createVaccineResult(int nurseId, VaccineResultRequest request) {

        return null;
    }

    public VaccineResultDTO updateVaccineResult(int vaccineResultId, VaccineResultRequest request) {
        // Optional<VaccineResultEntity> existingResultOpt =
        // vaccineResultRepository.findById(vaccineResultId);
        // if (existingResultOpt.isEmpty()) {
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine result not
        // found");
        // }

        // Optional<VaccineFormEntity> vaccineFormOpt =
        // vaccineFormRepository.findById(request.getVaccineFormId());
        // if (vaccineFormOpt.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine form not
        // found");

        // VaccineFormEntity vaccineFormEntity = vaccineFormOpt.get();

        // if (vaccineFormEntity.getCommit() == null || !vaccineFormEntity.getCommit())
        // {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent has not
        // committed the vaccine form yet");
        // }

        // VaccineResultEntity vaccineResultEntity = existingResultOpt.get();

        // vaccineResultEntity.setStatusHealth(request.getStatusHealth());
        // vaccineResultEntity.setResultNote(request.getResultNote());
        // vaccineResultEntity.setReaction(request.getReaction());
        // vaccineResultEntity.setCreatedAt(LocalDateTime.now());
        // vaccineResultEntity.setVaccineFormEntity(vaccineFormEntity);
        // vaccineResultRepository.save(vaccineResultEntity);

        // VaccineResultDTO vaccineResultDTO = modelMapper.map(vaccineResultEntity,
        // VaccineResultDTO.class);
        // vaccineResultDTO.setVaccineFormDTO(modelMapper.map(vaccineFormEntity,
        // VaccineFormDTO.class));
        // vaccineResultDTO.setStudentDTO(modelMapper.map(vaccineFormEntity.getStudent(),
        // StudentDTO.class));

        // return vaccineResultDTO;
        return null;
    }

    public VaccineResultDTO getVaccineResult(int vaccineResultId) {
        // VaccineResultEntity vaccineResultEntity =
        // vaccineResultRepository.findById(vaccineResultId)
        // .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine
        // result not found"));

        // VaccineResultDTO dto = modelMapper.map(vaccineResultEntity,
        // VaccineResultDTO.class);

        // VaccineFormEntity formEntity = vaccineResultEntity.getVaccineFormEntity();
        // VaccineFormDTO formDTO = modelMapper.map(formEntity, VaccineFormDTO.class);
        // dto.setVaccineFormDTO(formDTO);

        // StudentEntity student = formEntity.getStudent();
        // StudentDTO studentDTO = modelMapper.map(student, StudentDTO.class);

        // if (student.getClassEntity() != null) {
        // studentDTO.setClassDTO(modelMapper.map(student.getClassEntity(),
        // ClassDTO.class));
        // }

        // if (student.getParent() != null) {
        // studentDTO.setUserDTO(modelMapper.map(student.getParent(), UserDTO.class));
        // }

        // dto.setStudentDTO(studentDTO);

        // return dto;
        return null;
    }

    public List<VaccineResultDTO> getVaccineResultByProgram(int programId) {
        List<VaccineResultEntity> resultEntities = vaccineResultRepository.findByVaccineFormEntity_VaccineProgram_VaccineId(programId);

        if (resultEntities.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chưa có kết quả nào cho chương trình tiêm chủng này!");
        }

        List<VaccineResultDTO> dtoList = new ArrayList<>();
        for (VaccineResultEntity entity : resultEntities) {
            VaccineResultDTO dto = modelMapper.map(entity, VaccineResultDTO.class);

            StudentDTO studentDTO = modelMapper.map(entity.getVaccineFormEntity().getStudent(), StudentDTO.class);
            ClassDTO classDTO = modelMapper.map(entity.getVaccineFormEntity().getStudent().getClassEntity(), ClassDTO.class);
            classDTO.setStudents(null);
            studentDTO.setClassDTO(classDTO);
            studentDTO.setParentDTO(modelMapper.map(entity.getVaccineFormEntity().getStudent().getParent(), UserDTO.class));

            dto.setStudentDTO(studentDTO);
            dto.setNurseDTO(modelMapper.map(entity.getVaccineFormEntity().getNurse(), UserDTO.class));
            dtoList.add(dto);
        }

        return dtoList;
    }

    public void deleteVaccineResult(int vaccineResultId) {
        // Optional<VaccineResultEntity> vaccineResultOpt =
        // vaccineResultRepository.findById(vaccineResultId);
        // if (vaccineResultOpt.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine result not
        // found");
        // VaccineResultEntity vaccineResultEntity = vaccineResultOpt.get();
        // vaccineResultRepository.delete(vaccineResultEntity);

    }

    public List<StudentDTO> getAllStudent() {
        // List<StudentEntity> studentEntityList = studentRepository.findAll();
        // if (studentEntityList.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No students found");

        // List<StudentDTO> studentDTOList = studentEntityList.stream().map(student -> {
        // StudentDTO dto = modelMapper.map(student, StudentDTO.class);
        // if (student.getClassEntity() != null) {
        // ClassDTO classDTO = modelMapper.map(student.getClassEntity(),
        // ClassDTO.class);
        // dto.setClassDTO(classDTO);
        // }

        // if (student.getParent() != null) {
        // UserDTO userDTO = modelMapper.map(student.getParent(), UserDTO.class);
        // dto.setUserDTO(userDTO);
        // }
        // return dto;
        // }).collect(Collectors.toList());
        // return studentDTOList;
        return null;
    }

    public FeedbackDTO replyToFeedback(Integer feedbackId, ReplyFeedbackRequest request) {
        // FeedbackEntity feedback = feedbackRepository.findById(feedbackId)
        // .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DON'T
        // FIND TO RESPONSE."));

        // if (feedback.getStatus() == FeedbackStatus.REPLIED) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RESPONSE WAS
        // REPLIED");
        // }

        // feedback.setResponseNurse(request.getResponse());
        // feedback.setStatus(FeedbackStatus.REPLIED);

        // if (request.getNurseId() != null) {
        // UserEntity nurse = userRepository.findById(request.getNurseId())
        // .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NURSE
        // NOT FOUND"));
        // feedback.setNurse(nurse);
        // }

        // feedbackRepository.save(feedback);
        // FeedbackDTO dto = modelMapper.map(feedback, FeedbackDTO.class);
        // return dto;
        return null;
    }

    public List<FeedbackDTO> getFeedbacksForNurse(Integer nurseId) {
        // UserEntity nurse = userRepository.findById(nurseId.intValue())
        // .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NURSE
        // NOT FOUND."));
        // List<FeedbackEntity> feedbackList = feedbackRepository.findByNurse(nurse);

        // List<FeedbackDTO> feedbackDTOList = feedbackList.stream()
        // .map(feedback -> modelMapper.map(feedback,
        // FeedbackDTO.class)).collect(Collectors.toList());

        // return feedbackDTOList;
        return null;
    }

    public List<FeedbackDTO> getAllFeedbacks() {
        // List<FeedbackEntity> feedbackList = feedbackRepository.findAll();

        // List<FeedbackDTO> feedbackDTOList = feedbackList.stream()
        // .map(feedback -> {
        // FeedbackDTO dto = modelMapper.map(feedback, FeedbackDTO.class);
        // dto.setParentId(feedback.getParent() != null ?
        // feedback.getParent().getUserId() : null);
        // dto.setNurseId(feedback.getNurse() != null ? feedback.getNurse().getUserId()
        // : null);
        // dto.setVaccineResultId(
        // feedback.getVaccineResult() != null ?
        // feedback.getVaccineResult().getVaccineResultId()
        // : null);
        // dto.setHealthResultId(
        // feedback.getHealthResult() != null ?
        // feedback.getHealthResult().getHealthResultId() : null);
        // return dto;
        // })
        // .collect(Collectors.toList());

        // return feedbackDTOList;
        return null;
    }

    public List<StudentDTO> getStudentsNotVaccinated(int vaccineProgramId, int vaccineNameId) {
        // List<StudentEntity> students;

        // if (vaccineProgramId != null) {
        // students =
        // studentRepository.findStudentsNeverVaccinatedByProgramId(vaccineProgramId);
        // } else if (vaccineNameId != null) {
        // students =
        // studentRepository.findStudentsNeverVaccinatedByVaccineNameId(vaccineNameId);
        // } else {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing filter
        // condition");
        // }

        // return students.stream()
        // .map(s -> modelMapper.map(s, StudentDTO.class))
        // .collect(Collectors.toList());
        return null;
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
                        dto.setParentDTO(userDTO);
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
        // return title.toLowerCase()
        // .replaceAll("[^a-z0-9\\s]", "")
        // .replaceAll("\\s+", "-");
        return null;
    }

    public BlogResponse create(BlogRequest request) {
        // // Lấy thông tin người viết
        // UserEntity author = userRepository.findById(request.getUserId())
        // .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User
        // not found"));

        // // Tạo bài viết mới
        // BlogEntity post = new BlogEntity();
        // post.setTitle(request.getTitle());
        // post.setSlug(toSlug(request.getTitle()));
        // post.setCategory(request.getCategory());
        // post.setContent(request.getContent());
        // post.setAuthor(author);

        // // Lưu vào database
        // BlogEntity saved = blogRepository.save(post);

        // return toResponse(saved);
        return null;
    }

    public List<BlogResponse> getAll() {
        // List<BlogEntity> posts = blogRepository.findAll();
        // return posts.stream().map(this::toResponse).collect(Collectors.toList());
        return null;
    }

    public BlogResponse getBySlug(String slug) {
        // BlogEntity post = blogRepository.findBySlug(slug)
        // .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post
        // not found"));
        // return toResponse(post);
        return null;
    }

    public BlogResponse update(int id, BlogRequest request) {
        // BlogEntity post = blogRepository.findById(id)
        // .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post
        // not found"));

        // UserEntity author = userRepository.findById(request.getUserId())
        // .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User
        // not found"));

        // post.setTitle(request.getTitle());
        // post.setSlug(toSlug(request.getTitle()));
        // post.setCategory(request.getCategory());
        // post.setContent(request.getContent());
        // post.setAuthor(author);

        // BlogEntity updated = blogRepository.save(post);
        // return toResponse(updated);
        return null;
    }

    public void delete(int id) {
        // blogRepository.deleteById(id);
    }

    private BlogResponse toResponse(BlogEntity post) {
        // BlogResponse response = modelMapper.map(post, BlogResponse.class);
        // response.setUserId(post.getAuthor().getUserId());
        // return response;
        return null;
    }

    public List<UserDTO> searchUsers(String keyword, UserRole roleFilter) {
        // // Lấy toàn bộ danh sách user từ database
        // List<UserEntity> userList = userRepository.findAll();

        // // Lọc bỏ user có role là ADMIN và theo điều kiện keyword/roleFilter
        // List<UserDTO> result = userList.stream()
        // .filter(user -> user.getRole() != UserEntity.UserRole.ADMIN)
        // .filter(user -> {
        // boolean matchesKeyword = true;
        // if (keyword != null && !keyword.isBlank()) {
        // String lowerKeyword = keyword.toLowerCase();
        // matchesKeyword = (user.getFullName() != null
        // && user.getFullName().toLowerCase().contains(lowerKeyword))
        // || (user.getEmail() != null &&
        // user.getEmail().toLowerCase().contains(lowerKeyword))
        // || (user.getPhone() != null &&
        // user.getPhone().toLowerCase().contains(lowerKeyword));
        // }
        // boolean matchesRole = (roleFilter == null || user.getRole() == roleFilter);
        // return matchesKeyword && matchesRole;
        // })
        // .map(user -> modelMapper.map(user, UserDTO.class))
        // .collect(Collectors.toList());
        /*
         * // Lấy toàn bộ danh sách user từ database
         * List<UserEntity> userList = userRepository.findAll();
         * // Lọc bỏ user có role là ADMIN và theo điều kiện keyword/roleFilter
         * List<UserDTO> result = userList.stream()
         * .filter(user -> user.getRole() != UserEntity.UserRole.ADMIN)
         * .filter(user -> {
         * boolean matchesKeyword = true;
         * if (keyword != null && !keyword.isBlank()) {
         * String lowerKeyword = keyword.toLowerCase();
         * matchesKeyword = (user.getFullName() != null
         * && user.getFullName().toLowerCase().contains(lowerKeyword))
         * || (user.getEmail() != null &&
         * user.getEmail().toLowerCase().contains(lowerKeyword))
         * || (user.getPhone() != null &&
         * user.getPhone().toLowerCase().contains(lowerKeyword));
         * }
         * boolean matchesRole = (roleFilter == null || user.getRole() == roleFilter);
         * return matchesKeyword && matchesRole;
         * })
         * .map(user -> modelMapper.map(user, UserDTO.class))
         * .collect(Collectors.toList());
         * if (result.isEmpty()) {
         * throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No users found");
         * }
         * return result;
         */

        // if (result.isEmpty()) {
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No users found");
        // }

        // return result;
        return null;
    }

    public List<StudentDTO> getStudentsByClass(int classId) {
        // List<StudentEntity> students =
        // studentRepository.findByClassEntity_ClassId(classId);
        // return students.stream()
        // .map(student -> modelMapper.map(student, StudentDTO.class))
        // .collect(Collectors.toList());
        return null;
    }

    public List<MedicalRecordDTO> searchMedicalRecordsByStudentName(String keyword) {
        // List<StudentEntity> students =
        // studentRepository.findByFullNameContainingIgnoreCase(keyword);

        // List<MedicalRecordDTO> medicalRecords = new ArrayList<>();

        // for (StudentEntity student : students) {
        // Optional<MedicalRecordEntity> recordOpt = medicalRecordsRepository
        // .findMedicalRecordByStudent_Id(student.getId());

        // recordOpt.ifPresent(record -> {
        // MedicalRecordDTO dto = modelMapper.map(record, MedicalRecordDTO.class);
        // dto.setStudentId(student.getId());
        // medicalRecords.add(dto);
        // });
        // }

        // return medicalRecords;
        return null;
    }

    public List<MedicalRequestDTO> getByStatus(MedicalRequestEntity.MedicalRequestStatus status) {
        // List<MedicalRequestEntity> entities =
        // medicalRequestRepository.findByStatus(status);
        // return entities.stream()
        // .map(this::convertToDTO)
        // .collect(Collectors.toList());
        return null;
    }

    public List<MedicalRequestDTO> getByDate(LocalDate date) {
        // List<MedicalRequestEntity> entities = medicalRequestRepository.findAll()
        // .stream()
        // .filter(r -> r.getDate().isEqual(date))
        // .collect(Collectors.toList());
        // return entities.stream()
        // .map(this::convertToDTO)
        // .collect(Collectors.toList());
        return null;
    }

    public List<MedicalRequestDTO> getByClass(int classId) {
        // List<MedicalRequestEntity> entities = medicalRequestRepository.findAll()
        // .stream()
        // .filter(r -> r.getStudent().getClassEntity().getClassId() == classId)
        // .collect(Collectors.toList());
        // return entities.stream()
        // .map(this::convertToDTO)
        // .collect(Collectors.toList());
        return null;
    }

    public List<MedicalRequestDTO> searchByDateRange(LocalDate from, LocalDate to) {
        // List<MedicalRequestEntity> entities = medicalRequestRepository.findAll()
        // .stream()
        // .filter(r -> !r.getDate().isBefore(from) && !r.getDate().isAfter(to))
        // .collect(Collectors.toList());
        // return entities.stream()
        // .map(this::convertToDTO)
        // .collect(Collectors.toList());
        return null;
    }

    public void updateStatus(int requestId, MedicalRequestEntity.MedicalRequestStatus newStatus) {
        // MedicalRequestEntity entity = medicalRequestRepository.findById(requestId)
        // .orElseThrow(() -> new NoSuchElementException("Request not found"));
        // entity.setStatus(newStatus);
        // medicalRequestRepository.save(entity);
    }

    private MedicalRequestDTO convertToDTO(MedicalRequestEntity entity) {
        // MedicalRequestDTO dto = modelMapper.map(entity, MedicalRequestDTO.class);
        // dto.setStatus(entity.getStatus().name());
        // dto.setParentId(entity.getParent().getUserId());
        // return dto;
        return null;
    }

    public void createFormsForHealthCheckProgram(int programId) {
        // HealthCheckProgramEntity program =
        // healthCheckProgramRepository.findById(programId)
        // .orElseThrow(() -> new RuntimeException("Program not found"));

        // List<HealthCheckFormEntity> existingForms =
        // healthCheckFormRepository.findByHealthCheckProgram_Id(programId);
        // for (HealthCheckFormEntity form : existingForms) {
        // if (form.getStatus() == HealthCheckFormEntity.HealthCheckFormStatus.SENT) {
        // throw new RuntimeException("Health check forms already created for this
        // program");
        // } else {
        // form.setStatus(HealthCheckFormEntity.HealthCheckFormStatus.SENT);
        // healthCheckFormRepository.save(form);
        // }
        // }

        // // List<StudentEntity> students = studentRepository.findAllWithParent();

        // // List<HealthCheckFormEntity> forms = new ArrayList<>();
        /*
         * HealthCheckProgramEntity program =
         * healthCheckProgramRepository.findById(programId)
         * .orElseThrow(() -> new RuntimeException("Program not found"));
         * List<HealthCheckFormEntity> existingForms =
         * healthCheckFormRepository.findByHealthCheckProgram_Id(programId);
         * for (HealthCheckFormEntity form : existingForms) {
         * if (form.getStatus() == HealthCheckFormEntity.HealthCheckFormStatus.SENT) {
         * throw new
         * RuntimeException("Health check forms already created for this program");
         * } else {
         * form.setStatus(HealthCheckFormEntity.HealthCheckFormStatus.SENT);
         * healthCheckFormRepository.save(form);
         * }
         * }
         * // List<StudentEntity> students = studentRepository.findAllWithParent();
         * // List<HealthCheckFormEntity> forms = new ArrayList<>();
         * // for (StudentEntity student : students) {
         * // HealthCheckFormEntity form = new HealthCheckFormEntity();
         * // form.setHealthCheckProgram(program);
         * // form.setStudent(student);
         * // form.setParent(student.getParent());
         * // form.setFormDate(LocalDate.now());
         * // form.setStatus(HealthCheckFormEntity.HealthCheckFormStatus.SENT);
         * // form.setCommit(false);
         * // forms.add(form);
         * // }
         * // healthCheckFormRepository.saveAll(forms);
         */

    }

    public void createFormsForVaccineProgram(int vaccineProgramId) {
        /*
         * VaccineProgramEntity program =
         * vaccineProgramRepository.findById(vaccineProgramId)
         * .orElseThrow(() -> new RuntimeException("Vaccine program not found"));
         * List<StudentEntity> students = studentRepository.findAllWithParent();
         * List<VaccineFormEntity> forms = new ArrayList<>();
         * List<VaccineFormEntity> existingForms =
         * vaccineFormRepository.findByVaccineProgram_VaccineId(vaccineProgramId);
         * for (VaccineFormEntity form : existingForms) {
         * if (form.getStatus() == VaccineFormEntity.VaccineFormStatus.SENT) {
         * throw new
         * RuntimeException("Health check forms already created for this program");
         * } else {
         * form.setStatus(VaccineFormEntity.VaccineFormStatus.SENT);
         * vaccineFormRepository.save(form);
         * }
         * }
         */
        // VaccineProgramEntity program =
        // vaccineProgramRepository.findById(vaccineProgramId)
        // .orElseThrow(() -> new RuntimeException("Vaccine program not found"));

        // List<StudentEntity> students = studentRepository.findAllWithParent();

        // List<VaccineFormEntity> forms = new ArrayList<>();

        // List<VaccineFormEntity> existingForms =
        // vaccineFormRepository.findByVaccineProgram_VaccineId(vaccineProgramId);
        // for (VaccineFormEntity form : existingForms) {
        // if (form.getStatus() == VaccineFormEntity.VaccineFormStatus.SENT) {
        // throw new RuntimeException("Health check forms already created for this
        // program");
        // } else {
        // form.setStatus(VaccineFormEntity.VaccineFormStatus.SENT);
        // vaccineFormRepository.save(form);
        // }
        // }
    }

    public HealthCheckResultDTO createResultByProgramId(int programId, HealthCheckResultRequest request) {
        int nurseId = Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getName());
        UserEntity nurse = userRepository.findById(nurseId).orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy y tá."));

        HealthCheckFormEntity form = healthCheckFormRepository.findById(request.getHealthCheckFormId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy form với ID: " + request.getHealthCheckFormId()));

        if (form.getHealthCheckProgram() == null || form.getHealthCheckProgram().getId() != programId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Form không thuộc chương trình ID: " + programId);
        }

        if (form.getCommit() == null || !form.getCommit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Form chưa được commit (ID: " + request.getHealthCheckFormId() + ")");
        }

        Optional<HealthCheckResultEntity> existed = healthCheckResultRepository.findByHealthCheckForm(form);
        existed.ifPresent(oldResult -> healthCheckResultRepository.delete(oldResult));

        HealthCheckResultEntity result = new HealthCheckResultEntity();
        result.setVision(request.getVision());
        result.setHearing(request.getHearing());
        result.setWeight(request.getWeight());
        result.setHeight(request.getHeight());
        result.setDentalStatus(request.getDentalStatus());
        result.setBloodPressure(request.getBloodPressure());
        result.setHeartRate(request.getHeartRate());
        result.setGeneralCondition(request.getGeneralCondition());
        result.setNote(request.getNote());
        result.setIsChecked(request.getIsChecked());
        result.setHealthCheckForm(form);
        result.setStudent(form.getStudent());
        result.setNurse(nurse);

        HealthCheckResultEntity savedResult = healthCheckResultRepository.save(result);

        StudentEntity student = form.getStudent();
        Optional<MedicalRecordEntity> optionalRecord = medicalRecordsRepository.findByStudent(student);
        MedicalRecordEntity record = optionalRecord.orElse(new MedicalRecordEntity());
        record.setStudent(student);
        record.setVision(request.getVision());
        record.setHearing(request.getHearing());
        record.setWeight(request.getWeight());
        record.setHeight(request.getHeight());
        record.setNote(request.getNote());
        record.setLastUpdate(LocalDateTime.now());
        record.setCreateBy(true);
        medicalRecordsRepository.save(record);

        return modelMapper.map(savedResult, HealthCheckResultDTO.class);
    }

    public VaccineResultDTO createVaccineResultsByProgramId(int programId, VaccineResultRequest request) {
        int nurseId = Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getName());
        UserEntity nurse = userRepository.findById(nurseId).orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy y tá."));

        VaccineFormEntity form = vaccineFormRepository.findById(request.getVaccineFormId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy form với ID: " + request.getVaccineFormId()));

        if (form.getVaccineProgram() == null || form.getVaccineProgram().getVaccineId() != programId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Form không thuộc chương trình ID: " + programId);
        }

        if (form.getCommit() == null || !form.getCommit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Form chưa được commit (ID: " + request.getVaccineFormId() + ")");
        }

        Optional<VaccineResultEntity> existed = vaccineResultRepository.findByVaccineFormEntity(form);
        existed.ifPresent(oldResult -> vaccineResultRepository.delete(oldResult));
        StudentEntity student = form.getStudent();

        VaccineResultEntity result = new VaccineResultEntity();
        result.setResultNote(request.getResultNote());
        result.setReaction(request.getReaction());
        result.setActionsTaken(request.getActionsTaken());
        result.setCreatedAt(request.getCreateAt() != null ? request.getCreateAt() : LocalDateTime.now());
        result.setIsInjected(request.getIsInjected());

        result.setVaccineFormEntity(form);
        result.setStudentEntity(form.getStudent());
        result.setNurseEntity(nurse);

        VaccineResultEntity savedResult = vaccineResultRepository.save(result);

        VaccineNameEntity vaccineName = form.getVaccineName();

        Optional<VaccineHistoryEntity> historyOpt = vaccineHistoryRepository.findByStudentAndVaccineNameEntity(student, vaccineName);
        VaccineHistoryEntity history = historyOpt.orElse(new VaccineHistoryEntity());

        history.setStudent(student);
        history.setVaccineNameEntity(vaccineName);
        history.setNote("Đã tiêm ở trường");
        history.setCreateBy(true);
        history.setUnit(historyOpt.map(VaccineHistoryEntity::getUnit).orElse(0) + 1);

        vaccineHistoryRepository.save(history);

        ClassDTO classDTO = modelMapper.map(student.getClassEntity(), ClassDTO.class);
        classDTO.setStudents(null);
        StudentDTO studentDTO = modelMapper.map(student, StudentDTO.class);
        studentDTO.setClassDTO(classDTO);

        VaccineResultDTO resultDTO = modelMapper.map(savedResult, VaccineResultDTO.class);
        resultDTO.setStudentDTO(studentDTO);

        return resultDTO;
    }


    // Thien
    public List<ClassDTO> getAllClasses() {
        List<ClassEntity> classEntities = classRepository.findAll();
        if (classEntities.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không có lớp học nào được tìm thấy");
        }

        List<ClassDTO> classDTOs = classEntities.stream().map(classEntity -> modelMapper.map(classEntity, ClassDTO.class)).collect(Collectors.toList());

        for (ClassDTO classDTO : classDTOs) {
            classDTO.setStudents(null);
        }
        return classDTOs;
    }

    // Thien
    public List<UserDTO> getAllNurses() {
        List<UserEntity> nurseEntities = userRepository.findByRole(UserRole.NURSE);
        if (nurseEntities.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không có y tá nào được tìm thấy");
        }

        List<UserDTO> nurseDTOs = nurseEntities.stream().map(nurseEntity -> modelMapper.map(nurseEntity, UserDTO.class)).collect(Collectors.toList());
        return nurseDTOs;
    }

    public void createHealthCheckForm(int programId, LocalDate expDate) {
        HealthCheckProgramEntity programEntity = healthCheckProgramRepository.findById(programId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chương trình"));

        if (!expDate.isBefore(programEntity.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày hết hạn phải trước ngày thực hiện chương trình");
        }

        int nurseId = Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getName());
        UserEntity nurse = userRepository.findById(nurseId).orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy y tá với ID: " + nurseId));

        if (!expDate.isBefore(programEntity.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày hết hạn phải trước ngày thực hiện chương trình");
        }

        List<ParticipateClassEntity> participateClasses = participateClassRepository.findAllByProgramId(programId);

        for (ParticipateClassEntity pc : participateClasses) {
            List<StudentEntity> students = studentRepository.findByClassEntity(pc.getClazz());

            for (StudentEntity student : students) {
                UserEntity parent = student.getParent();
                if (parent == null) continue;

                boolean hasUncommittedForm = healthCheckFormRepository.findHealthCheckFormEntityByHealthCheckProgramAndStudent(programEntity, student).stream().anyMatch(form -> form.getCommit() == null);

                if (hasUncommittedForm) continue;
                HealthCheckFormEntity form = new HealthCheckFormEntity();
                form.setStudent(student);
                form.setParent(parent);
                form.setNotes(null);
                form.setCommit(null);
                form.setExpDate(expDate);
                form.setHealthCheckProgram(programEntity);
                form.setNurse(nurse);
                healthCheckFormRepository.save(form);

                /*Gui thong bao */
                notificationService.sendNotificationToParent(
                        parent.getUserId(),
                        "Thông báo chương trình khám sức khỏe",
                        "Bạn có phiếu thông báo khám sức khỏe cho học sinh " + form.getStudent().getFullName() + " cần xác nhận.",
                        "HEALTH_CHECK",
                        form.getId(),
                        false
                );
                /*-------------*/
            }
        }
        programEntity.setStatus(HealthCheckProgramEntity.HealthCheckProgramStatus.FORM_SENT);
        healthCheckProgramRepository.save(programEntity);
    }

    public HealthCheckProgramDTO updateHealthCheckProgramStatus(int id, String status) {
        HealthCheckProgramEntity healthCheckProgramEntity = healthCheckProgramRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chương trình kiểm tra sức khỏe"));

        HealthCheckProgramEntity.HealthCheckProgramStatus newStatus;
        try {
            newStatus = HealthCheckProgramEntity.HealthCheckProgramStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trạng thái không hợp lệ: " + status);
        }

        if (healthCheckProgramEntity.getStatus() == HealthCheckProgramEntity.HealthCheckProgramStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chương trình đã hoàn thành và không thể cập nhật");
        }

        if (healthCheckProgramEntity.getStatus() == newStatus) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chương trình đã ở trạng thái: " + newStatus);
        }

        healthCheckProgramEntity.setStatus(newStatus);
        healthCheckProgramRepository.save(healthCheckProgramEntity);

        UserEntity admin = healthCheckProgramEntity.getAdmin();
        UserDTO adminDTO = new UserDTO();
        adminDTO.setId(admin.getUserId());
        adminDTO.setFullName(admin.getFullName());
        adminDTO.setEmail(admin.getEmail());
        adminDTO.setPhone(admin.getPhone());
        adminDTO.setAddress(admin.getAddress());
        adminDTO.setRole(admin.getRole());

        UserEntity nurse = healthCheckProgramEntity.getNurse();
        UserDTO nurseDTO = new UserDTO();
        nurseDTO.setId(nurse.getUserId());
        nurseDTO.setFullName(nurse.getFullName());
        nurseDTO.setEmail(nurse.getEmail());
        nurseDTO.setPhone(nurse.getPhone());
        nurseDTO.setAddress(nurse.getAddress());
        nurseDTO.setRole(nurse.getRole());

        List<ParticipateClassEntity> participateEntities = participateClassRepository.findAllByProgramId(healthCheckProgramEntity.getId());

        List<ParticipateClassDTO> participateClassDTOs = new ArrayList<>();
        for (ParticipateClassEntity participate : participateEntities) {
            ParticipateClassDTO participateDTO = new ParticipateClassDTO();
            participateDTO.setParticipate_id(participate.getParticipateId());
            participateDTO.setProgram_id(healthCheckProgramEntity.getId());
            participateDTO.setClass_id(participate.getClazz().getClassId());
            participateDTO.setType(participate.getType().toString());

            ClassEntity clazz = participate.getClazz();
            ClassDTO classDTO = new ClassDTO();
            classDTO.setClassId(clazz.getClassId());
            classDTO.setClassName(clazz.getClassName());
            classDTO.setTeacherName(clazz.getTeacherName());
            classDTO.setQuantity(clazz.getQuantity());

            participateDTO.setClassDTO(classDTO);
            participateClassDTOs.add(participateDTO);
        }

        HealthCheckProgramDTO programDTO = new HealthCheckProgramDTO();
        programDTO.setId(healthCheckProgramEntity.getId());
        programDTO.setHealthCheckName(healthCheckProgramEntity.getHealthCheckName());
        programDTO.setDescription(healthCheckProgramEntity.getDescription());
        programDTO.setDateSendForm(healthCheckProgramEntity.getDateSendForm());
        programDTO.setStartDate(healthCheckProgramEntity.getStartDate());
        programDTO.setStatus(healthCheckProgramEntity.getStatus().toString());
        programDTO.setLocation(healthCheckProgramEntity.getLocation());

        programDTO.setAdminId(admin.getUserId());
        programDTO.setNurseId(nurse.getUserId());
        programDTO.setAdminDTO(adminDTO);
        programDTO.setNurseDTO(nurseDTO);
        programDTO.setParticipateClasses(participateClassDTOs);

        return programDTO;
    }

    public List<HealthCheckFormDTO> getCommittedFormsByProgramId(int programId) {
        List<HealthCheckFormEntity> formEntities = healthCheckFormRepository.findAllByHealthCheckProgram_IdAndCommitTrue(programId);

        List<HealthCheckFormDTO> result = new ArrayList<>();

        for (HealthCheckFormEntity formEntity : formEntities) {
            HealthCheckFormDTO formDTO = new HealthCheckFormDTO();
            formDTO.setId(formEntity.getId());
            formDTO.setExpDate(formEntity.getExpDate());
            formDTO.setNotes(formEntity.getNotes());
            formDTO.setCommit(formEntity.getCommit());
            formDTO.setStudentId(formEntity.getStudent().getId());
            formDTO.setParentId(formEntity.getParent().getUserId());
            formDTO.setNurseId(formEntity.getNurse().getUserId());

            // Student + Class
            StudentEntity student = formEntity.getStudent();
            StudentDTO studentDTO = new StudentDTO();
            studentDTO.setStudentId(student.getId());
            studentDTO.setFullName(student.getFullName());
            studentDTO.setDob(student.getDob());
            studentDTO.setGender(student.getGender().toString());
            studentDTO.setClassId(student.getClassEntity().getClassId());
            studentDTO.setParentId(student.getParent().getUserId());

            ClassEntity clazz = student.getClassEntity();
            ClassDTO classDTO = new ClassDTO();
            classDTO.setClassId(clazz.getClassId());
            classDTO.setClassName(clazz.getClassName());
            classDTO.setTeacherName(clazz.getTeacherName());
            classDTO.setQuantity(clazz.getQuantity());
            studentDTO.setClassDTO(classDTO);

            formDTO.setStudentDTO(studentDTO);

            UserEntity parent = formEntity.getParent();
            if (parent != null) {
                UserDTO parentDTO = new UserDTO();
                parentDTO.setId(parent.getUserId());
                parentDTO.setFullName(parent.getFullName());
                parentDTO.setEmail(parent.getEmail());
                parentDTO.setPassword(parent.getPassword());
                parentDTO.setPhone(parent.getPhone());
                parentDTO.setRelationship(parent.getRelationship());
                parentDTO.setAddress(parent.getAddress());
                parentDTO.setRole(parent.getRole());

                formDTO.setParentDTO(parentDTO);
                studentDTO.setParentDTO(parentDTO);
            }

            UserEntity nurse = formEntity.getNurse();
            if (nurse != null) {
                UserDTO nurseDTO = new UserDTO();
                nurseDTO.setId(nurse.getUserId());
                nurseDTO.setFullName(nurse.getFullName());
                nurseDTO.setEmail(nurse.getEmail());
                nurseDTO.setPassword(nurse.getPassword());
                nurseDTO.setPhone(nurse.getPhone());
                nurseDTO.setRelationship(nurse.getRelationship());
                nurseDTO.setAddress(nurse.getAddress());
                nurseDTO.setRole(nurse.getRole());
                nurseDTO.setStudentDTOs(null);
                formDTO.setNurseDTO(nurseDTO);
            }

            HealthCheckProgramEntity program = formEntity.getHealthCheckProgram();
            HealthCheckProgramDTO programDTO = new HealthCheckProgramDTO();
            programDTO.setId(program.getId());
            programDTO.setHealthCheckName(program.getHealthCheckName());
            programDTO.setDescription(program.getDescription());
            programDTO.setDateSendForm(program.getDateSendForm());
            programDTO.setStartDate(program.getStartDate());
            programDTO.setStatus(program.getStatus().toString());
            programDTO.setLocation(program.getLocation());
            programDTO.setAdminId(program.getAdmin().getUserId());
            programDTO.setNurseId(program.getNurse().getUserId());

            UserEntity admin = program.getAdmin();
            UserDTO adminDTO = new UserDTO();
            adminDTO.setId(admin.getUserId());
            adminDTO.setFullName(admin.getFullName());
            adminDTO.setEmail(admin.getEmail());
            adminDTO.setPassword(admin.getPassword());
            adminDTO.setPhone(admin.getPhone());
            adminDTO.setRelationship(admin.getRelationship());
            adminDTO.setAddress(admin.getAddress());
            adminDTO.setRole(admin.getRole());
            adminDTO.setStudentDTOs(null);
            programDTO.setAdminDTO(adminDTO);

            UserDTO nurseDTO = new UserDTO();
            nurseDTO.setId(nurse.getUserId());
            nurseDTO.setFullName(nurse.getFullName());
            nurseDTO.setEmail(nurse.getEmail());
            nurseDTO.setPassword(nurse.getPassword());
            nurseDTO.setPhone(nurse.getPhone());
            nurseDTO.setRelationship(nurse.getRelationship());
            nurseDTO.setAddress(nurse.getAddress());
            nurseDTO.setRole(nurse.getRole());
            nurseDTO.setStudentDTOs(null);
            programDTO.setNurseDTO(nurseDTO);

            formDTO.setHealthCheckProgramDTO(programDTO);

            result.add(formDTO);
        }
        return result;
    }

    public List<HealthCheckFormDTO> getCommittedHealthCheckFormsByProgram(int programId) {
        List<HealthCheckFormEntity> healthCheckForms = healthCheckFormRepository.findByCommitTrueAndHealthCheckProgram_Id(programId);

        if (healthCheckForms.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phiếu khám sức khỏe đã xác nhận");
        }

        HealthCheckProgramEntity program = healthCheckForms.get(0).getHealthCheckProgram();
        if (program.getStatus() != HealthCheckProgramEntity.HealthCheckProgramStatus.GENERATED_RESULT) {
            program.setStatus(HealthCheckProgramEntity.HealthCheckProgramStatus.GENERATED_RESULT);
            healthCheckProgramRepository.save(program);
        }

        return healthCheckForms.stream().map(entity -> {
            HealthCheckFormDTO dto = modelMapper.map(entity, HealthCheckFormDTO.class);
            dto.setStudentId(entity.getStudent().getId());
            dto.setParentId(entity.getParent().getUserId());

            StudentDTO studentDTO = modelMapper.map(entity.getStudent(), StudentDTO.class);

            ClassEntity classEntity = entity.getStudent().getClassEntity();
            if (classEntity != null) {
                ClassDTO classDTO = new ClassDTO();
                classDTO.setClassId(classEntity.getClassId());
                classDTO.setClassName(classEntity.getClassName());
                classDTO.setTeacherName(classEntity.getTeacherName());
                classDTO.setQuantity(classEntity.getQuantity());
                classDTO.setStudents(null);
                classDTO.setParticipateClasses(null);

                studentDTO.setClassDTO(classDTO);
            }

            dto.setStudentDTO(studentDTO);

            HealthCheckResultEntity resultEntity = healthCheckResultRepository.findByHealthCheckForm(entity).orElse(null);
            if (resultEntity != null) {
                dto.setHealthCheckResultDTO(modelMapper.map(resultEntity, HealthCheckResultDTO.class));
            }

            return dto;
        }).collect(Collectors.toList());
    }

    public List<VaccineFormDTO> getCommittedVaccineFormsByProgram(int programId) {
        List<VaccineFormEntity> vaccineForms = vaccineFormRepository.findByCommitTrueAndVaccineProgram_VaccineId(programId);

        if (vaccineForms.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phiếu tiêm chủng đã xác nhận");
        }

        VaccineProgramEntity program = vaccineForms.get(0).getVaccineProgram();
        if (program.getStatus() != VaccineProgramEntity.VaccineProgramStatus.GENERATED_RESULT) {
            program.setStatus(VaccineProgramEntity.VaccineProgramStatus.GENERATED_RESULT);
            vaccineProgramRepository.save(program);
        }

        return vaccineForms.stream().map(entity -> {
            VaccineFormDTO dto = modelMapper.map(entity, VaccineFormDTO.class);
            dto.setStudentID(entity.getStudent().getId());
            dto.setParentID(entity.getParent().getUserId());

            StudentDTO studentDTO = modelMapper.map(entity.getStudent(), StudentDTO.class);

            ClassEntity classEntity = entity.getStudent().getClassEntity();
            if (classEntity != null) {
                ClassDTO classDTO = new ClassDTO();
                classDTO.setClassId(classEntity.getClassId());
                classDTO.setClassName(classEntity.getClassName());
                classDTO.setTeacherName(classEntity.getTeacherName());
                classDTO.setQuantity(classEntity.getQuantity());
                classDTO.setStudents(null);
                classDTO.setParticipateClasses(null);

                studentDTO.setClassDTO(classDTO);
            }

            dto.setStudentDTO(studentDTO);

            // dto.setNurseID(entity.getNurse() != null ? entity.getNurse().getUserId() : null);
//            dto.setStudentDTO(modelMapper.map(entity.getStudent(), StudentDTO.class));

            // Tìm vaccineResultDTO từ VaccineResultEntity
            VaccineResultEntity vaccineResultEntity = vaccineResultRepository.findByVaccineFormEntity(entity).orElse(null);
            dto.setVaccineResultDTO(vaccineResultEntity != null ? modelMapper.map(vaccineResultEntity, VaccineResultDTO.class) : null);
            if (vaccineResultEntity != null) {
                dto.setVaccineResultDTO(modelMapper.map(vaccineResultEntity, VaccineResultDTO.class));
            }

            return dto;
        }).collect(Collectors.toList());
    }


    // Nút xem kết quả
    public List<VaccineFormDTO> viewVaccineResultByProgram(int programId) {
        List<VaccineFormEntity> vaccineForms = vaccineFormRepository.findByVaccineProgram_VaccineIdAndCommitIsTrue(programId);
        if (vaccineForms.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phiếu tiêm chủng đã xác nhận");
        }

        return vaccineForms.stream().map(entity -> {
            VaccineFormDTO dto = modelMapper.map(entity, VaccineFormDTO.class);
            dto.setStudentID(entity.getStudent().getId());
            dto.setParentID(entity.getParent().getUserId());
            // dto.setNurseID(entity.getNurse() != null ? entity.getNurse().getUserId() : null);
            dto.setStudentDTO(modelMapper.map(entity.getStudent(), StudentDTO.class));

            // Tìm vaccineResultDTO từ VaccineResultEntity

            StudentDTO studentDTO = modelMapper.map(entity.getStudent(), StudentDTO.class);

            ClassEntity classEntity = entity.getStudent().getClassEntity();
            if (classEntity != null) {
                ClassDTO classDTO = new ClassDTO();
                classDTO.setClassId(classEntity.getClassId());
                classDTO.setClassName(classEntity.getClassName());
                classDTO.setTeacherName(classEntity.getTeacherName());
                classDTO.setQuantity(classEntity.getQuantity());
                classDTO.setStudents(null);
                classDTO.setParticipateClasses(null);

                studentDTO.setClassDTO(classDTO);
            }

            dto.setStudentDTO(studentDTO);

            VaccineResultEntity vaccineResultEntity = vaccineResultRepository.findByVaccineFormEntity(entity).orElse(null);
            dto.setVaccineResultDTO(vaccineResultEntity != null ? modelMapper.map(vaccineResultEntity, VaccineResultDTO.class) : null);
            if (vaccineResultEntity != null) {
                dto.setVaccineResultDTO(modelMapper.map(vaccineResultEntity, VaccineResultDTO.class));
            }

            return dto;
        }).collect(Collectors.toList());
    }


    //Nút xem kết quả
    public List<HealthCheckFormDTO> viewHealthCheckResultByProgram(int programId) {
        List<HealthCheckFormEntity> healthCheckForms = healthCheckFormRepository.findAllByHealthCheckProgram_IdAndCommitIsTrue(programId);

        if (healthCheckForms.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phiếu khám sức khỏe");
        }

        return healthCheckForms.stream().map(entity -> {
            HealthCheckFormDTO dto = modelMapper.map(entity, HealthCheckFormDTO.class);
            dto.setStudentId(entity.getStudent().getId());
            dto.setParentId(entity.getParent().getUserId());

            StudentDTO studentDTO = modelMapper.map(entity.getStudent(), StudentDTO.class);

            ClassEntity classEntity = entity.getStudent().getClassEntity();
            if (classEntity != null) {
                ClassDTO classDTO = new ClassDTO();
                classDTO.setClassId(classEntity.getClassId());
                classDTO.setClassName(classEntity.getClassName());
                classDTO.setTeacherName(classEntity.getTeacherName());
                classDTO.setQuantity(classEntity.getQuantity());
                classDTO.setStudents(null);
                classDTO.setParticipateClasses(null);
                studentDTO.setClassDTO(classDTO);
            }

            dto.setStudentDTO(studentDTO);

            HealthCheckResultEntity resultEntity = healthCheckResultRepository.findByHealthCheckForm(entity).orElse(null);
            dto.setHealthCheckResultDTO(resultEntity != null ? modelMapper.map(resultEntity, HealthCheckResultDTO.class) : null);
            if (resultEntity != null) {
                dto.setHealthCheckResultDTO(modelMapper.map(resultEntity, HealthCheckResultDTO.class));
            }
            return dto;
        }).collect(Collectors.toList());
    }


    public void createVaccineForm(int programId, LocalDate expDate) {
        VaccineProgramEntity programEntity = vaccineProgramRepository.findById(programId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chương trình tiêm chủng"));

        if (!expDate.isBefore(programEntity.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày hết hạn phải trước ngày thực hiện chương trình");
        }

        int nurseId = Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getName());
        UserEntity nurse = userRepository.findById(nurseId).orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy y tá với ID: " + nurseId));

        List<ParticipateClassEntity> participateClasses = participateClassRepository.findAllByProgramId(programId);

        VaccineNameEntity vaccineName = programEntity.getVaccineName();

        List<VaccineUnitEntity> units = vaccineUnitRepository.findByVaccineName(vaccineName);
        int totalUnit = units.size();

        for (ParticipateClassEntity pc : participateClasses) {
            List<StudentEntity> students = studentRepository.findByClassEntity(pc.getClazz());

            for (StudentEntity student : students) {
                UserEntity parent = student.getParent();
                if (parent == null) continue;

                int injectedUnit = vaccineHistoryRepository.sumUnitsByStudentAndVaccineName(student, vaccineName);

                if (injectedUnit >= totalUnit) continue;

                boolean hasUncommittedForm = vaccineFormRepository.findVaccineFormEntityByVaccineProgramAndStudent(programEntity, student).stream().anyMatch(form -> form.getCommit() == null);

                if (hasUncommittedForm) continue;

                VaccineFormEntity form = new VaccineFormEntity();
                form.setStudent(student);
                form.setParent(parent);
                form.setCommit(null);
                form.setExpDate(expDate);
                form.setNurse(nurse);
                form.setVaccineProgram(programEntity);
                form.setVaccineName(vaccineName);
                form.setNote(null);

                vaccineFormRepository.save(form);

                /*Gui thong bao */
                notificationService.sendNotificationToParent(
                        parent.getUserId(),
                        "Thông báo chương trình tiêm chủng",
                        "Bạn có phiếu thông báo tiêm chủng cho học sinh " + student.getFullName() + " cần xác nhận.",
                        "VACCINE",
                        form.getId(),
                        false
                );
                /*-------------*/
            }
        }

        programEntity.setStatus(VaccineProgramEntity.VaccineProgramStatus.FORM_SENT);
        vaccineProgramRepository.save(programEntity);
    }


    public List<HealthCheckResultDTO> getAllHealthCheckResults() {
        List<HealthCheckResultEntity> resultEntities = healthCheckResultRepository.findAll();

        if (resultEntities.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không có kết quả khám sức khỏe nào!");
        }

        return resultEntities.stream().map(entity -> {
            HealthCheckResultDTO dto = modelMapper.map(entity, HealthCheckResultDTO.class);

            HealthCheckFormEntity form = entity.getHealthCheckForm();
            if (form != null) {
                HealthCheckFormDTO formDTO = modelMapper.map(form, HealthCheckFormDTO.class);

                if (form.getHealthCheckProgram() != null) {
                    formDTO.setHealthCheckProgramDTO(modelMapper.map(form.getHealthCheckProgram(), HealthCheckProgramDTO.class));
                }
                if (form.getNurse() != null) {
                    formDTO.setNurseDTO(modelMapper.map(form.getNurse(), UserDTO.class));
                }

                dto.setHealthCheckFormDTO(formDTO);
            }

            dto.setStudentDTO(modelMapper.map(entity.getStudent(), StudentDTO.class));
            return dto;
        }).collect(Collectors.toList());
    }

    public List<VaccineResultDTO> getAllVaccineResults() {
        List<VaccineResultEntity> resultEntities = vaccineResultRepository.findAll();

        if (resultEntities.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không có kết quả tiêm chủng nào!");
        }

        return resultEntities.stream().map(entity -> {
            VaccineResultDTO dto = modelMapper.map(entity, VaccineResultDTO.class);

            VaccineFormEntity form = entity.getVaccineFormEntity();
            if (form != null) {
                VaccineFormDTO formDTO = modelMapper.map(form, VaccineFormDTO.class);

                if (form.getVaccineProgram() != null) {
                    formDTO.setVaccineProgramDTO(modelMapper.map(form.getVaccineProgram(), VaccineProgramDTO.class));
                }

                dto.setVaccineFormDTO(formDTO);
            }

            if (entity.getStudentEntity() != null) {
                dto.setStudentDTO(modelMapper.map(entity.getStudentEntity(), StudentDTO.class));
            }

            if (entity.getNurseEntity() != null) {
                dto.setNurseDTO(modelMapper.map(entity.getNurseEntity(), UserDTO.class));
            }
            return dto;
        }).collect(Collectors.toList());
    }


}
