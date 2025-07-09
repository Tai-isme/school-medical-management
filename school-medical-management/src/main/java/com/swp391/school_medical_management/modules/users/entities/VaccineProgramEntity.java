package com.swp391.school_medical_management.modules.users.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vaccine_program")
public class VaccineProgramEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccine_id")
    private Long vaccineId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_name_id", referencedColumnName = "vaccine_name_id")
    private VaccineNameEntity vaccineName;

    @Column(name = "description")
    private String description;

    @Column(name = "vaccine_date")
    private LocalDate vaccineDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private VaccineProgramStatus status;

    public enum VaccineProgramStatus {
        ON_GOING, COMPLETED, NOT_STARTED
    }

    @Column(name = "note")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", referencedColumnName = "user_id")
    private UserEntity admin;
}
