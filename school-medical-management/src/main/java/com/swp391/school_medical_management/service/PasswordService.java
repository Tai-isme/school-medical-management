package com.swp391.school_medical_management.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class PasswordService {
    public String generateStrongRandomPassword() {
        int length = 12;
        String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int rndCharAt = random.nextInt(charSet.length());
            sb.append(charSet.charAt(rndCharAt));
        }
        return sb.toString();
    }
}
