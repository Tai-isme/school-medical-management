package com.swp391.school_medical_management.modules.users.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private LocalDate date;

    @Column(name = "description", length = 255)
    private String description;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "student_id", 
                foreignKey = @ForeignKey(name = "FK_medical_event_student"))
    private StudentEntity student;

    @ManyToOne
    @JoinColumn(name = "nurse_id", referencedColumnName = "user_id",
                foreignKey = @ForeignKey(name = "FK_medical_event_nurse"))
    private UserEntity nurse;
}
