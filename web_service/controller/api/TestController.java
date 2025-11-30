package com.example.demo.controller.api;

import com.example.demo.utils.ApiResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        return ApiResponseUtil.success("Pong! API is working correctly.");
    }
    
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ApiResponseUtil.success("API is healthy and running",
            java.time.LocalDateTime.now().toString());
    }
}