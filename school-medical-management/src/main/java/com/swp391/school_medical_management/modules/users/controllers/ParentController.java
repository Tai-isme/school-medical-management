package com.swp391.school_medical_management.modules.users.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/parent")
public class ParentController {

    @GetMapping("hello")
    public String hello() {
        return "Hello World";
    }
}
