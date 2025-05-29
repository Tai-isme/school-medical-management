package com.swp391.school_medical_management.modules.users.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "fullName")
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String role;
    private boolean is_activated;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Student> students;
}
