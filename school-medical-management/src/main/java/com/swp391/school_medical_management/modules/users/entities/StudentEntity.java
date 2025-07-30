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
@Table(name = "student")
public class StudentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private int id;
    @Column(name = "student_name")
    private String fullName;
    private LocalDate dob;
    @Column(name = "avatar_url")
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    public enum Gender {
        MALE, FEMALE
    }

    @ManyToOne
    @JoinColumn(name = "class_id", referencedColumnName = "class_id")
    private ClassEntity classEntity;
    
    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "user_id")
    private UserEntity parent;
}
