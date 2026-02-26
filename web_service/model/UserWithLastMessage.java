package com.example.demo.model;

import com.example.demo.entity.User;
import java.time.LocalDateTime;

public class UserWithLastMessage {
    private User user;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Boolean isUnread;

    public UserWithLastMessage(User user, String lastMessage, LocalDateTime lastMessageTime, Boolean isUnread) {
        this.user = user;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.isUnread = isUnread;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public Boolean getUnread() {
        return isUnread;
    }

    public void setUnread(Boolean unread) {
        isUnread = unread;
    }
}