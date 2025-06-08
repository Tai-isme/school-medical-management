package com.swp391.school_medical_management.modules.users.services.impl;


import com.swp391.school_medical_management.modules.users.dtos.response.*;
import com.swp391.school_medical_management.modules.users.entities.*;
import com.swp391.school_medical_management.modules.users.repositories.MedicalEventRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalRecordRepository;
import com.swp391.school_medical_management.modules.users.repositories.StudentRepository;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParentService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private MedicalEventRepository medicalEventRepository;

    public UserDTO getUserInfo(String userId) {
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new RuntimeException("User not exists!"));
        return modelMapper.map(user, UserDTO.class);
    }

    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find student"));

        SchoolClass schoolClass = student.getSchoolClass();
        SchoolClassDTO schoolClassDTO = schoolClass != null ? SchoolClassDTO.builder()
                .id(schoolClass.getId())
                .className(schoolClass.getClassName())
                .teacherName(schoolClass.getTeacherName())
                .quantity(schoolClass.getQuantity())
                .build() : null;

        return StudentDTO.builder()
                .id(student.getId())
                .studentName(student.getStudentName())
                .dob(student.getDob())
                .gender(student.getGender())
                .relationship(student.getRelationship())
                .classID(schoolClass != null ? schoolClass.getId() : null)
                .parentID(student.getParent().getId())
                .schoolClass(schoolClassDTO)
                .build();
    }

    public MedicalRecordDTO getMedicalRecord(Long studentId, String parentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("CANNOT FIND STUDENT"));

        if (!student.getParent().getId().equals(Long.valueOf(parentId))) {
            throw new AccessDeniedException("DON'T HAVE PERMISSION");
        }

        MedicalRecord record = medicalRecordRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("DON'T HAVE INFORMATION"));

        List<VaccineHistoryDTO> vaccineDTOs = record.getVaccineHistories().stream()
                .map(v -> VaccineHistoryDTO.builder()
                        .id(v.getId())
                        .vaccineName(v.getVaccineName())
                        .note(v.getNote())
                        .recordID(record.getId())
                        .build())
                .collect(Collectors.toList());

        return MedicalRecordDTO.builder()
                .id(record.getId())
                .studentID(student.getId())
                .allergies(record.getAllergies())
                .chronicDisease(record.getChronicDisease())
                .treatmentHistory(record.getTreatmentHistory())
                .vision(record.getVision())
                .hearing(record.getHearing())
                .weight(record.getWeight())
                .hight(record.getHigh())
                .lastUpdate(record.getLastUpdate())
                .note(record.getNote())
                .vaccineHistories(vaccineDTOs)
                .build();
    }

    public void createMedicalRecord(MedicalRecordDTO dto, String parentId) {
        Student student = studentRepository.findById(dto.getStudentID())
                .orElseThrow(() -> new RuntimeException("CANNOT FIND STUDENT"));

        if (!student.getParent().getId().equals(Long.valueOf(parentId))) {
            throw new AccessDeniedException("DON'T HAVE PERMISSION ADD INFORMATION");
        }

        MedicalRecord record = MedicalRecord.builder()
                .student(student)
                .allergies(dto.getAllergies())
                .chronicDisease(dto.getChronicDisease())
                .treatmentHistory(dto.getTreatmentHistory())
                .vision(dto.getVision())
                .hearing(dto.getHearing())
                .weight(dto.getWeight())
                .high(dto.getHight())
                .lastUpdate(LocalDate.now())
                .note(dto.getNote())
                .build();

        if (dto.getVaccineHistories() != null) {
            List<VaccineHistory> vaccines = dto.getVaccineHistories().stream().map(v -> {
                VaccineHistory vh = new VaccineHistory();
                vh.setVaccineName(v.getVaccineName());
                vh.setNote(v.getNote());
                vh.setMedicalRecord(record);
                return vh;
            }).collect(Collectors.toList());
            record.setVaccineHistories(vaccines);
        }

        medicalRecordRepository.save(record);
    }

    public void updateMedicalRecord(Long id, MedicalRecordDTO dto, String parentId) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DON'T FIND INFORMATION"));

        if (!record.getStudent().getParent().getId().equals(Long.valueOf(parentId))) {
            throw new AccessDeniedException("DON'T HAVE PERMISSION EDIT INFORMATION");
        }

        record.setAllergies(dto.getAllergies());
        record.setChronicDisease(dto.getChronicDisease());
        record.setTreatmentHistory(dto.getTreatmentHistory());
        record.setVision(dto.getVision());
        record.setHearing(dto.getHearing());
        record.setWeight(dto.getWeight());
        record.setHigh(dto.getHight());
        record.setLastUpdate(LocalDate.now());
        record.setNote(dto.getNote());

        if (dto.getVaccineHistories() != null) {
            record.getVaccineHistories().clear();
            List<VaccineHistory> vaccines = dto.getVaccineHistories().stream().map(v -> {
                VaccineHistory vh = new VaccineHistory();
                vh.setVaccineName(v.getVaccineName());
                vh.setNote(v.getNote());
                vh.setMedicalRecord(record);
                return vh;
            }).collect(Collectors.toList());
            record.getVaccineHistories().addAll(vaccines);
        }

        medicalRecordRepository.save(record);
    }


    public List<MedicalEvent> getEventsByStudent(Student student) {
        return medicalEventRepository.findByStudent(student);
    }
}

