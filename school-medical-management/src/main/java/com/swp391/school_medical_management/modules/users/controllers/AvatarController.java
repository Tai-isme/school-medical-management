package com.swp391.school_medical_management.modules.users.controllers;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.swp391.school_medical_management.service.UploadImageFile;

@RestController
@RequestMapping("/api/avatar")
public class AvatarController {

    @Autowired
    private UploadImageFile uploadImageFile;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = uploadImageFile.uploadImage(file);
            return ResponseEntity.ok(Map.of("url", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Lỗi upload: " + e.getMessage()));
        }
    }
}
