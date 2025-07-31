package com.swp391.school_medical_management.modules.users.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vaccine_program")
public class VaccineProgramEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccine_id")
    private int vaccineId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_name_id", referencedColumnName = "vaccine_name_id")
    private VaccineNameEntity vaccineName;

    private int unit;

    @Column(name = "vaccine_program_name", length = 100)
    private String vaccineProgramName;


    @Column(name = "description", length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private VaccineProgramStatus status;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "date_send_form")
    private LocalDate dateSendForm;

    @Column(name = "location", length = 255)
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nurse_id", referencedColumnName = "user_id")
    private UserEntity nurse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", referencedColumnName = "user_id")
    private UserEntity admin;

    public enum VaccineProgramStatus {
        NOT_STARTED,
        FORM_SENT,
        ON_GOING,
        GENERATED_RESULT,
        COMPLETED
    }
}
