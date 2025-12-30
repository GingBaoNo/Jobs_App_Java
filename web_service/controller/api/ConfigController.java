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

        // Lấy API key từ biến môi trường hoặc application.properties
        String googleApiKey = env.getProperty("google.maps.api.key");
        String hereApiKey = env.getProperty("here.maps.api.key");

        // Ưu tiên sử dụng OpenStreetMap (không cần API key)
        response.put("provider", "openstreetmap");
        response.put("apiKey", "");
        response.put("message", "OpenStreetMap được sử dụng (không cần API key)");

        // Nếu người dùng vẫn muốn dùng Google hoặc HERE, có thể cấu hình
        if (hereApiKey != null && !hereApiKey.isEmpty() && !hereApiKey.equals("YOUR_HERE_API_KEY_HERE")) {
            response.put("provider", "here");
            response.put("apiKey", hereApiKey);
            response.put("message", null);
        } else if (googleApiKey != null && !googleApiKey.isEmpty() && !googleApiKey.equals("YOUR_GOOGLE_MAPS_API_KEY")) {
            response.put("provider", "google");
            response.put("apiKey", googleApiKey);
            response.put("message", null);
        }

        return ResponseEntity.ok(response);
    }
}