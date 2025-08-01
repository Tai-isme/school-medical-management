package com.swp391.school_medical_management.modules.users.dtos.response;

import com.swp391.school_medical_management.modules.users.entities.HealthCheckProgramEntity;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthCheckResultExportDTO {
    private String programName; // Tên chương trình
    private HealthCheckProgramEntity.HealthCheckProgramStatus status; // Trạng thai chương trnìh
    private LocalDate startDate; // Ngày thực hiện
    private String location; // Địa điểm
    private String nurseName; // Y tá phụ trách
    private String adminName; // Người tạo chương trình
    private String description;// Mô tả chương trinh
    private Long totalNumberStudentChecked; // Tổng học sinh tham gia
    private Long totalNumberStudentNotChecked; // Tổng học sinh không tham gia

    private Integer studentId; // Mã học sinh
    private String studentName; // Tên học sinh
    private LocalDate dob; // Ngày tháng năm sinh
    private StudentEntity.Gender gender; // Giới tính
    private String className; // Tên lớp
    private String parentName; // Tên phụ huynh
    private String phone; // Số điện thoại phụ huynh
    private String address; // Địa chỉ
    private String relationship; // Mối quan hệ

    private String generalCondition; // Tong quan
    private String vision; // Thi giác
    private String hearing; // Thinh giac
    private Double weight; // Can nang
    private Integer height; // Chieu cao
    private String dentalStatus; // Tinh trang rang mieng
    private String bloodPressure; // Huyet ap
    private Integer heartRate; // Nhip tim
    private String note; // Ghi chu
    private Boolean isChecked; // Da kham
}
