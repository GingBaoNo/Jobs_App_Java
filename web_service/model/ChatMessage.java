package com.example.demo.model;

public class ChatMessage {
    private String type;
    private String content;
    private String sender;
    private String receiver;
    private String senderUsername;
    private String receiverUsername;
    private Long timestamp;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }

    public ChatMessage() {}

    public ChatMessage(String type, String content, String sender, String receiver, String senderUsername, String receiverUsername) {
        this.type = type;
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}