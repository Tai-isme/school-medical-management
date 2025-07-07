package com.swp391.school_medical_management.modules.users.entities;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "form_date")
    private LocalDate formDate;

    @Column(name = "notes")
    private String notes;

    @Column(name = "commit")
    private Boolean commit;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private HealthCheckFormStatus status;

    public enum HealthCheckFormStatus {
        DRAFT, SENT
    }
}
