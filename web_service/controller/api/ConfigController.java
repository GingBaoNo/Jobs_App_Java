package com.example.demo.controller.api;

import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private final Environment env;

    public ConfigController(Environment env) {
        this.env = env;
    }

    @GetMapping("/maps-config")
    public ResponseEntity<Map<String, String>> getMapsConfig() {
        Map<String, String> response = new HashMap<>();

        // Luôn sử dụng OpenStreetMap (không cần API key)
        response.put("provider", "openstreetmap");
        response.put("apiKey", "");
        response.put("message", "OpenStreetMap được sử dụng (không cần API key)");

        return ResponseEntity.ok(response);
    }
}