package com.swp391.school_medical_management.modules.users.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "vaccine_result")
public class VaccineResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccine_result_id")
    private Integer vaccineResultId;

    @Column(name = "status_health")
    private String statusHealth;

    @Column(name = "result_note")
    private String resultNote;

    @Column(name = "reaction")
    private String reaction;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "vaccine_form_id", referencedColumnName = "vaccine_form_id")
    private VaccineFormEntity vaccineFormEntity;
}
