package com.example.demo.model;

import java.time.LocalDateTime;

public class StandardChatMessage {
    private Integer messageId;
    private Integer senderId;
    private String senderUsername;
    private String senderDisplayName;
    private Integer receiverId;
    private String receiverUsername;
    private String receiverDisplayName;
    private String content;
    private LocalDateTime sendTime;
    private Boolean isRead;
    private String type; // CHAT, JOIN, LEAVE

    public StandardChatMessage() {
        this.sendTime = LocalDateTime.now();
    }

    public StandardChatMessage(Integer senderId, String senderUsername, String senderDisplayName,
                              Integer receiverId, String receiverUsername, String receiverDisplayName,
                              String content, String type) {
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.senderDisplayName = senderDisplayName;
        this.receiverId = receiverId;
        this.receiverUsername = receiverUsername;
        this.receiverDisplayName = receiverDisplayName;
        this.content = content;
        this.type = type;
        this.sendTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getSenderDisplayName() {
        return senderDisplayName;
    }

    public void setSenderDisplayName(String senderDisplayName) {
        this.senderDisplayName = senderDisplayName;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public String getReceiverDisplayName() {
        return receiverDisplayName;
    }

    public void setReceiverDisplayName(String receiverDisplayName) {
        this.receiverDisplayName = receiverDisplayName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}