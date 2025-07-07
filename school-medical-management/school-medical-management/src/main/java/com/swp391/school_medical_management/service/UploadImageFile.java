package com.swp391.school_medical_management.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;


public interface UploadImageFile {
    String uploadImage(MultipartFile file) throws IOException;
}
