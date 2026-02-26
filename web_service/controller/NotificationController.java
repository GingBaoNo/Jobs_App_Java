package com.example.demo.controller;

import com.example.demo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notification")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationService notificationService;

    // Endpoint để gửi thông báo reload cho tất cả người dùng
    @PostMapping("/reload")
    public String triggerReload(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        String target = payload.getOrDefault("target", "all"); // "all", "users", "specific"
        
        // Gửi thông báo reload đến frontend
        messagingTemplate.convertAndSend("/topic/reload", message);
        
        return "Reload notification sent";
    }
}