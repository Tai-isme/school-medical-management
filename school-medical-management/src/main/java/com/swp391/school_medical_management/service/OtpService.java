package com.swp391.school_medical_management.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OtpService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    public String generateOtp() {
        Random random = new Random();
        int number = random.nextInt(9999);
        return String.format("%06d", number);
    }
}
