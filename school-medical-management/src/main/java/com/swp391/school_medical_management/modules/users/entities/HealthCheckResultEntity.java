package com.swp391.school_medical_management.modules.users.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
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
@Table(name = "health_check_result")
public class HealthCheckResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "health_result_id")
    private Integer healthResultId;

    @Column(name = "diagnosis", length = 255)
    private String diagnosis;

    @Column(name = "level", length = 50)
    @Enumerated(EnumType.STRING)
    private Level level;

    public enum Level {
        GOOD, FAIR, AVERAGE, POOR
    }

    @Column(name = "vision", length = 50)
    private String vision;

    @Column(name = "hearing", length = 50)
    private String hearing;

    @Column(name = "weight", length = 50)
    private Double weight;

    @Column(name = "height", length = 50)
    private Double height;

    @Column(name = "note", length = 255)
    private String note;

    @ManyToOne
    @JoinColumn(name = "health_check_form_id", referencedColumnName = "health_check_form_id", foreignKey = @ForeignKey(name = "FK_health_check_result_form"))
    private HealthCheckFormEntity healthCheckFormEntity;
}
