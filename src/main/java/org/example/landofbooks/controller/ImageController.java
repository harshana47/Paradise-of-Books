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
@CrossOrigin(origins = "*")
public class ImageController {

    private static final String IMAGE_DIRECTORY = "uploads/images/";

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path imagePath = Paths.get(IMAGE_DIRECTORY).resolve(filename);
            Resource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                String contentType = Files.probeContentType(imagePath);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (MalformedURLException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }
}
