package com.swp391.school_medical_management.modules.users.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "health_check_form")
public class HealthCheckFormEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "health_check_form_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "health_check_id")
    private HealthCheckProgramEntity healthCheckProgram;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private StudentEntity student;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private UserEntity parent;

    @ManyToOne
    @JoinColumn(name = "nurse_id")
    private UserEntity nurse;

    @Column(name = "exp_date")
    private LocalDate expDate;

    @Column(name = "notes")
    private String notes;

    @Column(name = "commit")
    private Boolean commit;
}
