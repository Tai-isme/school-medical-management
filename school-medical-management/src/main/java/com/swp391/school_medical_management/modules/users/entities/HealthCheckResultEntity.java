package com.swp391.school_medical_management.modules.users.entities;


import jakarta.persistence.*;
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
    private String level; // Giá trị: GOOD, FAIR, AVERAGE, POOR

    @Column(name = "note", length = 255)
    private String note;

    @ManyToOne
    @JoinColumn(
        name = "health_check_form_id", 
        referencedColumnName = "health_check_form_id",
        foreignKey = @ForeignKey(name = "FK_health_check_result_form")
    )
    private HealthCheckFormEntity healthCheckFormEntity;
}
