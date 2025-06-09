package com.swp391.school_medical_management.modules.users.entities;

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
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "vaccine_history")
public class VaccineHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccine_history_id")
    private Integer id;

    @Column(name = "vaccine_name")
    private String vaccineName;

    @Column(name = "note")
    private String note;

    @ManyToOne()
    @JoinColumn(name = "record_id", referencedColumnName = "record_id")
    private MedicalRecordEntity medicalRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_id", referencedColumnName = "vaccine_id")
    private VaccineProgramEntity vaccineProgram;

}
