package com.swp391.school_medical_management.modules.users.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "health_check_result")
public class HealthCheckResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "health_result_id")
    private int healthResultId;

    @Column(name = "vision", length = 50)
    private String vision;

    @Column(name = "hearing", length = 50)
    private String hearing;

    @Column(name = "weight", length = 50)
    private Double weight;

    @Column(name = "height", length = 50)
    private int height;

    @Column(name = "dental_status", length = 100)
    private String dentalStatus;

    @Column(name = "blood_pressure", length = 50)
    private String bloodPressure;

    @Column(name = "heart_rate", length = 50)
    private int heartRate;

    @Column(name = "general_condition", length = 50)
    private String generalCondition;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "is_checked")
    private Boolean isChecked;

    @OneToOne
    @JoinColumn(name = "health_check_form_id", referencedColumnName = "health_check_form_id", foreignKey = @ForeignKey(name = "FK_health_check_result_form"))
    private HealthCheckFormEntity healthCheckForm;

    @OneToOne
    @JoinColumn(name = "student_id", referencedColumnName = "student_id", foreignKey = @ForeignKey(name = "FK_health_check_result_student"))
    private StudentEntity student;

    @ManyToOne
    @JoinColumn(name = "nurse_id", referencedColumnName = "user_id", foreignKey = @ForeignKey(name = "FK_health_check_result_nurse"))
    private UserEntity nurse;
}
