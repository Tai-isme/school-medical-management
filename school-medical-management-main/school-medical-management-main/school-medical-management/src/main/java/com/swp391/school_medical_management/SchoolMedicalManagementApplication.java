package com.swp391.school_medical_management;


import com.swp391.school_medical_management.modules.users.entities.User;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SchoolMedicalManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchoolMedicalManagementApplication.class, args);
	}

}
