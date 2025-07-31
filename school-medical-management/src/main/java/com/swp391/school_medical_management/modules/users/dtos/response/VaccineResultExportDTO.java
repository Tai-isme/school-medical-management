package com.swp391.school_medical_management.modules.users.dtos.response;

import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VaccineResultExportDTO {
    private String programName; // Tên chương trình
    private LocalDate startDate; // Ngày thực hiện
    private String location; // Địa điểm
    private String nurseName; // Y tá phụ trách
    private String adminName; // Người tạo chương trình
    private String description;// Mô tả chương trinh
    private Long totalNumberStudentVaccinated; // Tổng học sinh tham gia
    private Long totalNumberStudentNotVaccinated; // Tổng học sinh không tham gia

    private String vaccineName; // Tên vaccine
    private Integer unit; // Mũi thứ
    private String manufature; // Nguồn gốc vaccine
    private Integer ageFrom; // Tuổi từ
    private Integer ageTo; // Tuổi đến
    private String vaccineDescription; // Mô tả vaccine

    private Integer studentId; // Mã học sinh
    private String studentName; // Tên học sinh
    private LocalDate dob; // Ngày tháng năm sinh
    private StudentEntity.Gender gender; // Giới tính
    private String className; // Tên lớp
    private String parentName; // Tên phụ huynh
    private String phone; // Số điện thoại phụ huynh
    private String address; // Địa chỉ
    private String relationship; // Mối quan hệ

    private String reaction; // Phản ứng sau tiêm
    private String actionsTaken; // Hành động xử lý
    private String note; // Ghi chú
    private Boolean isInjected; // Đã tiêm

}
