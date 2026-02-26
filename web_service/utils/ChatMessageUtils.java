package com.example.demo.utils;

import com.example.demo.entity.Message;
import com.example.demo.entity.User;
import com.example.demo.model.ChatMessage;
import com.example.demo.model.StandardChatMessage;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChatMessageUtils {

    // Chuyển đổi từ Message entity sang StandardChatMessage
    public static StandardChatMessage toStandardChatMessage(Message message) {
        StandardChatMessage standardMsg = new StandardChatMessage();

        // Đảm bảo không sử dụng ID của message gốc để tránh vấn đề với persistence context
        if (message.getMaTinNhan() != null) {
            standardMsg.setMessageId(message.getMaTinNhan());
        }

        // Kiểm tra xem sender và receiver có null không để tránh lỗi
        if (message.getSender() != null) {
            standardMsg.setSenderId(message.getSender().getMaNguoiDung());
            standardMsg.setSenderUsername(message.getSender().getTaiKhoan());
            standardMsg.setSenderDisplayName(message.getSender().getTenHienThi() != null ?
                message.getSender().getTenHienThi() : message.getSender().getTaiKhoan());
        }

        if (message.getReceiver() != null) {
            standardMsg.setReceiverId(message.getReceiver().getMaNguoiDung());
            standardMsg.setReceiverUsername(message.getReceiver().getTaiKhoan());
            standardMsg.setReceiverDisplayName(message.getReceiver().getTenHienThi() != null ?
                message.getReceiver().getTenHienThi() : message.getReceiver().getTaiKhoan());
        }

        standardMsg.setContent(message.getNoiDung());
        standardMsg.setSendTime(message.getThoiGianGui());
        standardMsg.setIsRead(message.getDaDoc());
        standardMsg.setType("CHAT");
        return standardMsg;
    }

    // Chuyển đổi từ danh sách Message entities sang danh sách StandardChatMessage
    public static List<StandardChatMessage> toStandardChatMessages(List<Message> messages) {
        return messages.stream()
                .filter(Objects::nonNull)  // Lọc các message null
                .map(ChatMessageUtils::toStandardChatMessage)
                .collect(Collectors.toList());
    }

    // Chuyển đổi từ ChatMessage (WebSocket) sang StandardChatMessage
    public static StandardChatMessage toStandardChatMessageFromChatMessage(ChatMessage chatMessage) {
        StandardChatMessage standardMsg = new StandardChatMessage();
        standardMsg.setSenderId(Integer.parseInt(chatMessage.getSender()));
        standardMsg.setSenderUsername(chatMessage.getSenderUsername());
        standardMsg.setReceiverId(Integer.parseInt(chatMessage.getReceiver()));
        standardMsg.setReceiverUsername(chatMessage.getReceiverUsername());
        standardMsg.setContent(chatMessage.getContent());
        standardMsg.setType(chatMessage.getType());
        return standardMsg;
    }

    // Chuyển đổi từ StandardChatMessage sang Message entity
    public static Message toMessageEntity(StandardChatMessage standardMsg, User sender, User receiver) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setNoiDung(standardMsg.getContent());
        message.setThoiGianGui(standardMsg.getSendTime());
        message.setDaDoc(standardMsg.getIsRead());
        return message;
    }

    // Chuyển đổi từ StandardChatMessage sang ChatMessage (WebSocket)
    public static ChatMessage toWebSocketChatMessage(StandardChatMessage standardMsg) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(standardMsg.getType());
        chatMessage.setContent(standardMsg.getContent());
        chatMessage.setSender(standardMsg.getSenderId().toString());
        chatMessage.setReceiver(standardMsg.getReceiverId().toString());
        chatMessage.setSenderUsername(standardMsg.getSenderUsername());
        chatMessage.setReceiverUsername(standardMsg.getReceiverUsername());
        return chatMessage;
    }
}