package com.swp391.school_medical_management.modules.users.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "class")
public class ClassEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private int classId;

    @Column(name = "teacher_name", length = 100, nullable = true) 
    private String teacherName;

    @Column(name = "class_name", length = 20, nullable = true) 
    private String className;

    @Column(name = "quantity", nullable = true) 
    private int quantity;
}