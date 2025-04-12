package com.example.Music_Web.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

@Service
public class FileStorageService {
    private final Path rootLocation = Paths.get("uploads");

    public String storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file");
        }

        // Create directory if not exists
        if (!Files.exists(rootLocation)) {
            Files.createDirectories(rootLocation);
        }

        // Sanitize filename
        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        // Save file
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, this.rootLocation.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);
        }

        return "/" + rootLocation.toString() + "/" + filename;
    }
}
