package com.swp391.school_medical_management.modules.users.entities;

import java.time.LocalDateTime;

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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "medical_event")
public class MedicalEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Integer eventId;

    @Column(name = "type_event", length = 100)
    private String typeEvent;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "actions_taken", length = 255)
    private String actionsTaken;

    @Column(name = "level_check", length = 20)
    @Enumerated(EnumType.STRING)
    private LevelCheck levelCheck;

    public enum LevelCheck {
        low, medium, high
    }

    @Column(name = "location", length = 100)
    private String location;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "student_id", foreignKey = @ForeignKey(name = "FK_medical_event_student"))
    private StudentEntity student;

    @ManyToOne
    @JoinColumn(name = "nurse_id", referencedColumnName = "user_id", foreignKey = @ForeignKey(name = "FK_medical_event_nurse"))
    private UserEntity nurse;
}
