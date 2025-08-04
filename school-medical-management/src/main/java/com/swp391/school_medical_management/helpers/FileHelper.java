package com.swp391.school_medical_management.helpers;


import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class FileHelper {

    public static MultipartFile downloadImageAsMultipartFile(String imageUrl, String fileName) throws IOException {
        URL url = new URL(imageUrl);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (InputStream is = url.openStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
        }

        byte[] bytes = baos.toByteArray();
        return new MockMultipartFile(fileName, fileName + ".png", "image/png", bytes);
    }
}