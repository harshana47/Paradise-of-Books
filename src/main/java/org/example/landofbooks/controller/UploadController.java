package org.example.landofbooks.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("api/v1/upload")
public class UploadController {

    private final String uploadDir = "/path/to/upload/directory/";

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file selected");
        }

        try {
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf(".")) : "";
            String uniqueFileName = UUID.randomUUID().toString() + extension;

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            File destinationFile = new File(uploadDir + uniqueFileName);
            file.transferTo(destinationFile);

            return ResponseEntity.ok("File uploaded successfully: " + uniqueFileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }
}
