package com.swp391.school_medical_management.modules.users.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "class")
public class SchoolClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private Long id;

    @Column(name = "class_name")
    private String className;

    @Column(name = "teacher_name")
    private String teacherName;

    @Column(name = "quantity")
    private Integer quantity;

    @OneToMany(mappedBy = "schoolClass")
    @JsonIgnore
    private List<Student> students;
}
