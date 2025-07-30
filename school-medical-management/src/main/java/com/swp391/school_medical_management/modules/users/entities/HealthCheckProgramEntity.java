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
@Table(name = "health_check_program")
public class HealthCheckProgramEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "health_check_id")
    private Long id;

    @Column(name = "health_check_name")
    private String healthCheckName;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "date_send_form")
    private LocalDate dateSendForm;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private HealthCheckProgramStatus status;

    public enum HealthCheckProgramStatus {
        NOT_STARTED,
        FORM_SENT,
        ON_GOING,
        GENERATED_RESULT,
        COMPLETED
    }

    @Column(name = "location")
    private String location;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private UserEntity admin;

    @ManyToOne
    @JoinColumn(name = "nurse_id")
    private UserEntity nurse;
}
