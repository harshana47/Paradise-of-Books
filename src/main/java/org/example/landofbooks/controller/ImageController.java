package org.example.landofbooks.controller;

import io.jsonwebtoken.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/images")
@CrossOrigin(origins = "*") // Allowing all origins (for testing)
public class ImageController {

    private static final String IMAGE_DIRECTORY = "uploads/images/";

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            // Assuming IMAGE_DIRECTORY is the relative directory for storing images on the server
            Path imagePath = Paths.get(IMAGE_DIRECTORY).resolve(filename); // Resolve the image file relative to IMAGE_DIRECTORY
            Resource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                // Dynamically set the MIME type (e.g., for JPEG or PNG)
                String contentType = Files.probeContentType(imagePath);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Image not found
            }
        } catch (MalformedURLException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Internal server error
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }
}
