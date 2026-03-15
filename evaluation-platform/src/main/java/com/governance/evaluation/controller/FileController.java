package com.governance.evaluation.controller;

import com.governance.evaluation.entity.Evaluation;
import com.governance.evaluation.entity.User;
import com.governance.evaluation.entity.UserRole;
import com.governance.evaluation.repository.EvaluationRepository;
import com.governance.evaluation.repository.UserRepository;
import com.governance.evaluation.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;
    private final EvaluationRepository evaluationRepository;
    private final UserRepository userRepository;

    /**
     * Upload evidence file
     * POST /api/files/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("evaluationId") Long evaluationId,
            @RequestParam("criterionId") Long criterionId
    ) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Evaluation evaluation = evaluationRepository.findById(evaluationId)
                    .orElseThrow(() -> new RuntimeException("Evaluation not found"));

            // Check authorization
            if (!evaluation.getOrganization().getUserId().equals(currentUser.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Not authorized to upload files for this evaluation"));
            }

            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "File is empty"));
            }

            // Check file size (max 10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "File size exceeds 10MB limit"));
            }

            // Store file
            String filename = fileStorageService.storeFile(file, evaluationId, criterionId);

            System.out.println("✅ File uploaded: " + filename);

            Map<String, Object> response = new HashMap<>();
            response.put("filename", filename);
            response.put("originalFilename", file.getOriginalFilename());
            response.put("size", file.getSize());
            response.put("contentType", file.getContentType());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error uploading file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload file"));
        }
    }

    /**
     * Download evidence file
     * GET /api/files/download/{evaluationId}/{filename}
     */
    @GetMapping("/download/{evaluationId}/{filename}")
    public ResponseEntity<?> downloadFile(
            @PathVariable Long evaluationId,
            @PathVariable String filename
    ) {
        try {
            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Evaluation evaluation = evaluationRepository.findById(evaluationId)
                    .orElseThrow(() -> new RuntimeException("Evaluation not found"));

            // ✅ SECURITY: Only owner, evaluators, and admins can download
            String userRole = currentUser.getRole().toString();
            boolean isOwner = evaluation.getOrganization().getUserId().equals(currentUser.getUserId());
            boolean isEvaluator = "EVALUATOR".equals(userRole);
            boolean isAdmin = "ADMIN".equals(userRole);

            if (!isOwner && !isEvaluator && !isAdmin) {
                System.err.println("❌ Unauthorized file download attempt by: " + currentUserEmail);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Not authorized to access this file"));
            }

            // Load file
            Path filePath = fileStorageService.loadFile(evaluationId, filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            System.out.println("✅ File downloaded by " + currentUserEmail + ": " + filename);

            // Determine content type
            String contentType = "application/octet-stream";
            if (filename.endsWith(".pdf")) contentType = "application/pdf";
            else if (filename.endsWith(".png")) contentType = "image/png";
            else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) contentType = "image/jpeg";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (Exception e) {
            System.err.println("❌ Error downloading file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to download file"));
        }
    }
}