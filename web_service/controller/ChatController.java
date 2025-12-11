package com.example.demo.controller;

import com.example.demo.entity.Message;
import com.example.demo.entity.User;
import com.example.demo.model.ChatMessage;
import com.example.demo.service.MessageService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import org.apache.commons.text.StringEscapeUtils;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        // Lưu tin nhắn vào cơ sở dữ liệu
        User sender = userService.findById(Integer.parseInt(chatMessage.getSender()));
        User receiver = userService.findById(Integer.parseInt(chatMessage.getReceiver()));

        if (sender != null && receiver != null) {
            Message savedMessage = null;
            try {
                // Sử dụng nội dung gốc để đảm bảo lưu thành công
                String content = chatMessage.getContent();

                Message message = new Message(sender, receiver, content);
                savedMessage = messageService.saveMessage(message);

                if (savedMessage == null) {
                    System.out.println("Lỗi: Không thể lưu tin nhắn vào cơ sở dữ liệu");
                    return; // Dừng xử lý nếu không lưu được
                }
            } catch (Exception e) {
                System.out.println("Lỗi xử lý tin nhắn: " + e.getMessage());
                e.printStackTrace();
                // Nếu có lỗi, vẫn cố gắng xử lý tiếp để gửi tin nhắn
                savedMessage = new Message(sender, receiver, chatMessage.getContent());
            }

            // Tạo đối tượng chat message để gửi
            ChatMessage responseMessage = new ChatMessage(
                    "CHAT",
                    savedMessage.getNoiDung(),
                    chatMessage.getSender(),
                    chatMessage.getReceiver(),
                    sender.getTaiKhoan(),
                    receiver.getTaiKhoan()
            );

            // Gửi tin nhắn đến người nhận
            System.out.println("Gửi tin nhắn đến người nhận: " + chatMessage.getReceiver() + " (username: " + receiver.getTaiKhoan() + ")");
            messagingTemplate.convertAndSendToUser(
                    receiver.getTaiKhoan(), // Gửi theo username thay vì ID
                    "/queue/messages",
                    responseMessage
            );

            // Gửi tin nhắn đến người gửi để update giao diện
            System.out.println("Gửi tin nhắn đến người gửi: " + chatMessage.getSender() + " (username: " + sender.getTaiKhoan() + ")");
            messagingTemplate.convertAndSendToUser(
                    sender.getTaiKhoan(), // Gửi theo username thay vì ID
                    "/queue/messages",
                    responseMessage
            );
        }
    }

    @MessageMapping("/chat.addUser")
    public void addUser(SimpMessageHeaderAccessor headerAccessor, @Payload ChatMessage chatMessage) {
        // Thêm người dùng vào phiên
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
    }
}