package com.swp391.school_medical_management.modules.users.services.impl;

import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckFormDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineFormDTO;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckForm;
import com.swp391.school_medical_management.modules.users.entities.VaccineForm;
import com.swp391.school_medical_management.modules.users.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NurseService {
    @Autowired
    private HealthCheckFormRepository healthCheckFormRepo;
    @Autowired private VaccineFormRepository vaccineFormRepo;
    @Autowired private HealthCheckProgramRepository healthRepo;
    @Autowired private VaccineProgramRepository vaccineRepo;
    @Autowired private StudentRepository studentRepo;
    @Autowired private UserRepository userRepo;

    public HealthCheckForm createHealthForm(HealthCheckFormDTO dto) {
        HealthCheckForm form = new HealthCheckForm();
        form.setHealthCheckProgram(healthRepo.findById(dto.getHealthCheckId()).orElseThrow());
        form.setStudent(studentRepo.findById(dto.getStudentId()).orElseThrow());
        form.setParent(userRepo.findById(dto.getParentId()).orElseThrow());
        form.setNurse(userRepo.findById(dto.getNurseId()).orElseThrow());
        form.setFormDate(dto.getFormDate());
        form.setNotes(dto.getNotes());
        form.setCommit(dto.getCommit());
        return healthCheckFormRepo.save(form);
    }

    public List<HealthCheckForm> getAllHealthForms() {
        return healthCheckFormRepo.findAll();
    }

    public VaccineForm createVaccineForm(VaccineFormDTO dto) {
        VaccineForm form = new VaccineForm();
        form.setVaccineProgram(vaccineRepo.findById(dto.getVaccineId()).orElseThrow());
        form.setStudent(studentRepo.findById(dto.getStudentId()).orElseThrow());
        form.setParent(userRepo.findById(dto.getParentId()).orElseThrow());
        form.setNurse(userRepo.findById(dto.getNurseId()).orElseThrow());
        form.setFormDate(dto.getFormDate());
        form.setNote(dto.getNote());
        form.setCommit(dto.getCommit());
        return vaccineFormRepo.save(form);
    }

    public List<VaccineForm> getAllVaccineForms() {
        return vaccineFormRepo.findAll();
    }

    // Lấy danh sách các form đã commit
    public List<HealthCheckForm> getCommittedHealthForms() {
        return healthCheckFormRepo.findByCommitTrue();
    }

    public List<VaccineForm> getCommittedVaccineForms() {
        return vaccineFormRepo.findByCommitTrue();
    }

    // Cập nhật form
    public HealthCheckForm updateHealthForm(Long id, HealthCheckFormDTO dto) {
        HealthCheckForm form = healthCheckFormRepo.findById(id).orElseThrow();
        form.setFormDate(dto.getFormDate());
        form.setNotes(dto.getNotes());
        form.setCommit(dto.getCommit());
        form.setNurse(userRepo.findById(dto.getNurseId()).orElseThrow());
        return healthCheckFormRepo.save(form);
    }

    public VaccineForm updateVaccineForm(Long id, VaccineFormDTO dto) {
        VaccineForm form = vaccineFormRepo.findById(id).orElseThrow();
        form.setFormDate(dto.getFormDate());
        form.setNote(dto.getNote());
        form.setCommit(dto.getCommit());
        form.setNurse(userRepo.findById(dto.getNurseId()).orElseThrow());
        return vaccineFormRepo.save(form);
    }

    // Xoá form
    public void deleteHealthForm(Long id) {
        healthCheckFormRepo.deleteById(id);
    }

    public void deleteVaccineForm(Long id) {
        vaccineFormRepo.deleteById(id);
    }

    // Tìm kiếm theo tên học sinh hoặc phụ huynh
    public List<HealthCheckForm> searchHealthForms(String keyword) {
        return healthCheckFormRepo.findByStudent_StudentNameContainingIgnoreCaseOrParent_FullNameContainingIgnoreCase(keyword, keyword);
    }

    public List<VaccineForm> searchVaccineForms(String keyword) {
        return vaccineFormRepo.findByStudent_StudentNameContainingIgnoreCaseOrParent_FullNameContainingIgnoreCase(keyword, keyword);
    }



}
