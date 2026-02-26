package com.example.demo.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("API is running successfully!");
    }
    
    @GetMapping("/info")
    public ResponseEntity<Object> getInfo() {
        return ResponseEntity.ok(new Object() {
            public String name = "Job Portal API";
            public String version = "1.0.0";
            public String description = "API for mobile application to interact with job portal database";
            public String timestamp = java.time.LocalDateTime.now().toString();
        });
    }
}