package com.swp391.school_medical_management.modules.users.dtos.request;

import com.swp391.school_medical_management.modules.users.entities.HealthCheckProgramEntity;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class HealthCheckProgramRequest {

    @NotBlank(message = "Tên đợt khám không được để trống")
    @Size(max = 100, message = "Tên đợt khám tối đa 100 ký tự")
    private String healthCheckName;

    private String description;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @FutureOrPresent(message = "Ngày bắt đầu phải là hôm nay hoặc trong tương lai")
    private LocalDate startDate;

    @FutureOrPresent(message = "Ngày gửi form phải là hôm nay hoặc trong tương lai")
    private LocalDate dateSendForm;

    @Size(max = 255, message = "Địa điểm tối đa 255 ký tự")
    private String location;

    @NotNull(message = "Trạng thái không được để trống")
    private HealthCheckProgramEntity.HealthCheckProgramStatus status;

    @NotNull(message = "ID admin không được để trống")
    private int adminId;

    @NotNull(message = "ID y tá không được để trống")
    private int nurseId;

    private List<Integer> classIds;

}
