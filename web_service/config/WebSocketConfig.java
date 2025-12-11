package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint cho kết nối WebSocket
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Cho phép tất cả các origin (trong môi trường phát triển)
                .withSockJS(); // SockJS fallback nếu WebSocket không được hỗ trợ
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefix cho các topic gửi từ server về client
        registry.enableSimpleBroker("/topic", "/queue", "/user");
        // Prefix cho các endpoint nhận tin nhắn từ client
        registry.setApplicationDestinationPrefixes("/app");
        // Prefix cho các topic gửi riêng cho user
        registry.setUserDestinationPrefix("/user");
    }
}