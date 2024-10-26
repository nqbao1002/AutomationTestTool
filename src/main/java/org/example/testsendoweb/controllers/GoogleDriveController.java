package org.example.testsendoweb.controllers;

import lombok.AllArgsConstructor;
import org.example.testsendoweb.services.DriveService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/drive")
@AllArgsConstructor
public class GoogleDriveController {

    private final DriveService driveService;

    // Endpoint to upload CSV file to Google Drive
    @PostMapping("/upload")
    public ResponseEntity<String> uploadCSVToDrive(@RequestBody String content) {
        try {
            driveService.uploadGoogleSheetToDrive(content);  // Call the method to upload file
            return ResponseEntity.ok("File uploaded successfully.");
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file.");
        }
    }


    // You need to implement the getCredentials method as per your project setup for Google OAuth.
}