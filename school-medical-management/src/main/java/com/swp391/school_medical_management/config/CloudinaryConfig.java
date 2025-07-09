package com.swp391.school_medical_management.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary configKey(){
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "duzh5dnul");
        config.put("api_key", "511313553762856");
        config.put("api_secret", "GAsZ5F746_YoJKQke9Qu-aj3GIY");
        return new Cloudinary(config);
    }

}
