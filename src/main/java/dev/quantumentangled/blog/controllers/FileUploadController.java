package dev.quantumentangled.blog.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@PreAuthorize("hasRole('AUTHOR')")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);


    @Value("${blog.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload-image")
    @ResponseBody
    public ResponseEntity<String> handleUpload(@RequestParam("image") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Empty file");
        }

        // Allowed MIME types for images
        List<String> allowedTypes = List.of("image/png", "image/jpeg", "image/gif", "image/webp");

        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType.toLowerCase())) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body("Only image files are allowed");
        }

        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path destination = Paths.get(uploadDir).resolve(filename);
        logger.info("File copied into " + destination.toString());
        Files.copy(file.getInputStream(), destination);
        
        // Return public URL (relative to your static mapping)
        return ResponseEntity.ok("/uploads/" + filename);
    }
}
