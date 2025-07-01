package com.swp391.school_medical_management.modules.users.entities;

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
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "vaccine_history")
public class VaccineHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccine_history_id")
    private Integer id;

    @Enumerated(EnumType.STRING) 
    @Column(name = "vaccine_name")
    private VaccineName vaccineName;

    public enum VaccineName {
        HEPATITIS_A,
        HEPATITIS_B,
        INFLUENZA,
        CHICKENPOX,
        JAPANESE_ENCEPHALITIS,
        POLIO,
        DIPHTHERIA,
        PERTUSSIS,
        TETANUS_HEPATITIS_B,
        HIB,
        HPV;   
    }

    @Column(name = "note")
    private String note;

    @ManyToOne()
    @JoinColumn(name = "record_id", referencedColumnName = "record_id")
    private MedicalRecordEntity medicalRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_id", referencedColumnName = "vaccine_id")
    private VaccineProgramEntity vaccineProgram;

}
