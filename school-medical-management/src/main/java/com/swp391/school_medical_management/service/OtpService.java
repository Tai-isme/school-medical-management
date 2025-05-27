package com.swp391.school_medical_management.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OtpService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    public String generateOtp() {
        Random random = new Random();
        int number = random.nextInt(9999);
        return String.format("%06d", number);
    }

    public boolean isOtpValid(String phone, String otp) {
        try {
            String redisKey = "otp:" + phone;
            String storedOtp = stringRedisTemplate.opsForValue().get(redisKey);
            logger.info("storedOtp: " + storedOtp);
            if (storedOtp != null && storedOtp.equals(otp)) {
                stringRedisTemplate.delete(redisKey);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

}
