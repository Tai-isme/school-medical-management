package com.swp391.school_medical_management.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.cloudinary.utils.StringUtils;
import com.swp391.school_medical_management.service.UploadImageFile;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import ch.qos.logback.core.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadImageFileImpl implements UploadImageFile {

    private final Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile file) {
        assert file.getOriginalFilename() != null;
        String publicValue = generatePublicValue(file.getOriginalFilename());
        String extension = getFileName(file.getOriginalFilename())[1];
        File fileUpload = null;
        
        try {
            fileUpload = convert(file);
            cloudinary.uploader().upload(fileUpload, ObjectUtils.asMap("public_id", publicValue));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        log.info("publicValue is: {}", publicValue);
        log.info("extension is: {}", extension);
        log.info("fileUpload is: {}", fileUpload);
        String filePath = cloudinary.url().generate(publicValue+ "." + extension);

        cleanDisk(fileUpload);
        log.info("filePath is: {}", filePath);

        return filePath;
    }

    private void cleanDisk(File file) {
    try {
        log.info("file.toPath() is: {}", file.toPath());
        Path filePath = file.toPath();
        Files.delete(filePath);
    } catch (IOException e) {
        log.error("Error");
    }
}

    private File convert(MultipartFile file) throws IOException {
        assert file.getOriginalFilename() != null;
        String[] fileNameParts = getFileName(file.getOriginalFilename());
        String fileName = generatePublicValue(file.getOriginalFilename()) + "." + fileNameParts[1];
        File convFile = new File(fileName);
        try (InputStream is = file.getInputStream()) {
            Files.copy(is, convFile.toPath());
        }
        return convFile;
    }

    public String generatePublicValue(String originalName){
        String fileName = getFileName(originalName)[0];
        return UUID.randomUUID().toString() + "_" + fileName;
    }

    public String[] getFileName(String originalName) {
        return originalName.split("\\.");
    }
}
