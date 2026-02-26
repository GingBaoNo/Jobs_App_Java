package com.example.demo.handler;

import com.example.demo.entity.User;
import com.example.demo.model.StandardChatMessage;
import com.example.demo.service.MessageService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;
import java.util.Map;

@Component
public class WebSocketHandler {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    private Map<String, String> userSessions = new HashMap<>();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        System.out.println("User connected");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {
            System.out.println("User Disconnected: " + username);
            userSessions.remove(username);
            // Gửi tin nhắn đến các client khác về việc người dùng đã rời
            StandardChatMessage standardMsg = new StandardChatMessage();
            standardMsg.setType("LEAVE");
            standardMsg.setSenderUsername(username);
            standardMsg.setSenderDisplayName(username);

            messagingTemplate.convertAndSend("/topic/public", standardMsg);
        }
    }

    @EventListener
    public void handleWebSocketConnectEvent(SessionConnectedEvent event) {
        // Xử lý sự kiện kết nối
        System.out.println("WebSocket connected: " + event.getMessage().getHeaders());
    }
}