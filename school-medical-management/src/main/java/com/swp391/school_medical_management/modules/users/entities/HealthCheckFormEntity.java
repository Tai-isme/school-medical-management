package com.swp391.school_medical_management.modules.users.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "health_check_form")
public class HealthCheckFormEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "health_check_form_id")
    private int id;

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
