package com.swp391.school_medical_management.modules.users.entities;

import java.time.LocalDate;

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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "student")
public class StudentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private long id;
    @Column(name = "student_name")
    private String fullName;
    private LocalDate dob;
    @Column(name = "avatar_url")
    private String avatarUrl;
    private Gender gender;

    private enum Gender{
        MALE, FEMALE
    }

    @ManyToOne
    @JoinColumn(name = "class_id", referencedColumnName = "class_id")
    private ClassEntity classEntity;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private UserEntity parent;
}
