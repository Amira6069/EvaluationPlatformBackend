package com.governance.evaluation.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService() {
        // Store files in project directory under 'uploads'
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
            System.out.println("✅ File storage directory created: " + this.fileStorageLocation);
        } catch (Exception e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    public String storeFile(MultipartFile file, Long evaluationId, Long criterionId) {
        try {
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = evaluationId + "_" + criterionId + "_" + UUID.randomUUID() + extension;

            // Create evaluation-specific directory
            Path evalDirectory = this.fileStorageLocation.resolve(evaluationId.toString());
            Files.createDirectories(evalDirectory);

            // Copy file to target location
            Path targetLocation = evalDirectory.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("✅ File stored: " + filename);

            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public Path loadFile(Long evaluationId, String filename) {
        return this.fileStorageLocation.resolve(evaluationId.toString()).resolve(filename);
    }

    public void deleteFile(Long evaluationId, String filename) {
        try {
            Path filePath = loadFile(evaluationId, filename);
            Files.deleteIfExists(filePath);
            System.out.println("✅ File deleted: " + filename);
        } catch (IOException e) {
            System.err.println("❌ Failed to delete file: " + filename);
        }
    }
}