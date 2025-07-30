package com.swp391.school_medical_management.modules.users.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NurseAccountRequest {

    @NotBlank(message = "Vui lòng nhập email!")
    @Email(message = "Vui lòng nhập Email hợp lệ!")
    private String email;

    //Thien
    @NotBlank(message = "Vui lòng nhập số điện thoại!")
    @Pattern(regexp = "^(0[3|5|7|8|9])+([0-9]{8})$", message = "Số điện thoại không hợp lệ!")
    private String phone;

    private String address;

    @NotBlank(message = "Vui lòng nhập tên người dùng!")
    @Size(min = 2, max = 50, message = "Tên người dùng phải có độ dài từ 2 đến 50 ký tự!")
    private String name;


}
