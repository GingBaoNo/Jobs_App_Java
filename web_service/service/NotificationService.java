package com.example.demo.service;

import com.example.demo.model.StandardChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Gửi thông báo reload khi có tin nhắn mới
    public void notifyReloadOnMessage(StandardChatMessage message) {
        String notification = "MESSAGE_UPDATE:" + message.getSenderId() + ":" + message.getReceiverId();
        messagingTemplate.convertAndSend("/topic/reload", notification);
    }

    // Gửi thông báo reload cho tất cả người dùng
    public void notifyGlobalReload(String reason) {
        messagingTemplate.convertAndSend("/topic/reload", "GLOBAL_UPDATE:" + reason);
    }

    // Gửi thông báo reload cho người nhận cụ thể
    public void notifyUserReload(String username, String reason) {
        messagingTemplate.convertAndSendToUser(username, "/queue/reload", "USER_UPDATE:" + reason);
    }
}