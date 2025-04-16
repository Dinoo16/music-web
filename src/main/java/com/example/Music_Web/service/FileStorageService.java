package com.example.Music_Web.service;

import com.mpatric.mp3agic.Mp3File;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

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

    public int getDurationFromFile(MultipartFile file) throws Exception {
        // Save to a temporary file
        File tempFile = File.createTempFile("temp_song", ".mp3");
        file.transferTo(tempFile);
        try {
            Mp3File mp3File = new Mp3File(tempFile);
            return (int) mp3File.getLengthInSeconds();
        } finally {
            tempFile.delete();
        }
    }
    // Method to format duration in minutes:seconds
    public String formatDuration(int seconds) {
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

}
