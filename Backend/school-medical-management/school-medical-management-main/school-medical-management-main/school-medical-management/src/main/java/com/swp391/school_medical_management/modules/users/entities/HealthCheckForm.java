package com.swp391.school_medical_management.modules.users.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "health_check_form")
public class HealthCheckForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "health_check_form_id")
    private Long id;

    @Column(name = "form_date")
    private LocalDate formDate;

    @Column(name = "notes")
    private String notes;

    @Column(name = "commit")
    private Boolean commit;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private User parent;

    @ManyToOne
    @JoinColumn(name = "nurse_id")
    private User nurse;

    @ManyToOne
    @JoinColumn(name = "health_check_id")
    private HealthCheckProgram healthCheckProgram;
}
