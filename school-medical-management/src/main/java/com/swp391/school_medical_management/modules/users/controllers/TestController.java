package com.swp391.school_medical_management.modules.users.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/test")
    public List<Map<String, Object>> testConnection() {
        Map<String, Object> obj1 = new HashMap<>();
        obj1.put("message", "123");
        obj1.put("status", "success");

        Map<String, Object> obj2 = new HashMap<>();
        obj2.put("message", "456");
        obj2.put("status", "fail");

        return Arrays.asList(obj1, obj2);
    }


}