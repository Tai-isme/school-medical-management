package com.swp391.school_medical_management.modules.users.controllers;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.swp391.school_medical_management.service.UploadImageFile;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/upload")
@RestController
@RequiredArgsConstructor
public class UploadFileController {

    private final UploadImageFile uploadImageFile;

    @PostMapping("/image")
    public String uploadImage(@RequestParam("file")MultipartFile file) throws IOException {
        return uploadImageFile.uploadImage(file);
    }

}
