package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint cho kết nối WebSocket với STOMP
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        
        // Endpoint cho WebSocket thuần (không SockJS)
        registry.addEndpoint("/websocket")
                .setAllowedOriginPatterns("*");
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