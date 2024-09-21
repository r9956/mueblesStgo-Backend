package com.example.mueblesStgoBackend.controllers;

import com.example.mueblesStgoBackend.services.ClockDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/clock")
@CrossOrigin("*")
public class ClockDataController {
    @Autowired
    private ClockDataService clockDataService;

    @PostMapping("/upload")
    public ResponseEntity<String> readClockData(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty.");
        }
        try {
            return clockDataService.fileReader(file);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file.");
        }
    }

    @PostMapping("/analyze")
    public void analyzeClockData() {
        clockDataService.analyzeClockData();
    }
}
