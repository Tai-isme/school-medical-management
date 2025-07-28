package com.swp391.school_medical_management.modules.users.dtos.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class HealthCheckProgramRequest {

    @NotBlank(message = "Tên đợt khám không được để trống")
    @Size(max = 100, message = "Tên đợt khám tối đa 100 ký tự")
    private String healthCheckName;

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    private String description;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @FutureOrPresent(message = "Ngày bắt đầu phải là hôm nay hoặc trong tương lai")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @Future(message = "Ngày kết thúc phải là trong tương lai")
    private LocalDate endDate;

    @Size(max = 255, message = "Ghi chú tối đa 255 ký tự")
    private String note;

}
