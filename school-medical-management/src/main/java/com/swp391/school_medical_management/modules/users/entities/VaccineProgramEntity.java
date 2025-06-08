package com.swp391.school_medical_management.modules.users.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    private Integer vaccineId;

    @Column(name = "vaccine_name")
    private String vaccineName;

    @Column(name = "description")
    private String description;

    @Column(name = "vaccine_date")
    private LocalDate vaccineDate;

    @Column(name = "status")
    private String status; // VALID: NOT_STARTED, ON_GOING, COMPLETED

    @Column(name = "note")
    private String note;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", referencedColumnName = "user_id")
    private UserEntity admin;
}
