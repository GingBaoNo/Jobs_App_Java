package com.example.demo.controller;

import com.example.demo.entity.Message;
import com.example.demo.entity.User;
import com.example.demo.model.ChatMessage;
import com.example.demo.model.StandardChatMessage;
import com.example.demo.service.MessageService;
import com.example.demo.service.NotificationService;
import com.example.demo.service.UserService;
import com.example.demo.utils.ChatMessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload StandardChatMessage standardMsg) {
        if (standardMsg != null) {
            System.out.println("Tiến hành xử lý tin nhắn từ người dùng ID: " + standardMsg.getSenderId() +
                              " đến người dùng ID: " + standardMsg.getReceiverId());

            User sender = userService.findById(standardMsg.getSenderId());
            User receiver = userService.findById(standardMsg.getReceiverId());

            if (sender != null && receiver != null) {
                System.out.println("Tìm thấy người gửi: " + sender.getTaiKhoan() + ", người nhận: " + receiver.getTaiKhoan());

                Message savedMessage = null;
                try {
                    // Lưu tin nhắn vào cơ sở dữ liệu
                    Message message = new Message(sender, receiver, standardMsg.getContent());
                    // Đảm bảo không có ID để tạo đối tượng mới
                    message.setMaTinNhan(null);
                    System.out.println("Đang tạo đối tượng Message mới để lưu vào DB");

                    savedMessage = messageService.saveMessage(message);

                    if (savedMessage == null) {
                        System.out.println("Lỗi: Không thể lưu tin nhắn vào cơ sở dữ liệu - savedMessage là null");
                        return; // Dừng xử lý nếu không lưu được
                    }

                    System.out.println("Tin nhắn đã được lưu vào DB với ID: " + savedMessage.getMaTinNhan());
                } catch (Exception e) {
                    System.out.println("Lỗi xử lý tin nhắn: " + e.getMessage());
                    e.printStackTrace();
                    return; // Dừng xử lý nếu có lỗi
                }

                // Chuyển đổi sang StandardChatMessage sử dụng tiện ích để đảm bảo đầy đủ thông tin
                StandardChatMessage responseMsg = ChatMessageUtils.toStandardChatMessage(savedMessage);
                System.out.println("Chuẩn bị gửi tin nhắn qua WebSocket");

                // Gửi tin nhắn chuẩn đến người nhận
                messagingTemplate.convertAndSendToUser(
                        receiver.getTaiKhoan(),
                        "/queue/messages",
                        responseMsg
                );
                System.out.println("Đã gửi tin nhắn cho người nhận: " + receiver.getTaiKhoan());

                // Gửi tin nhắn chuẩn đến người gửi (để cập nhật giao diện)
                messagingTemplate.convertAndSendToUser(
                        sender.getTaiKhoan(),
                        "/queue/messages",
                        responseMsg
                );
                System.out.println("Đã gửi tin nhắn cho người gửi: " + sender.getTaiKhoan());

                // Gửi thông báo reload cho cả người gửi và người nhận
                notificationService.notifyUserReload(sender.getTaiKhoan(), "new_message");
                notificationService.notifyUserReload(receiver.getTaiKhoan(), "new_message");
            } else {
                System.out.println("Không tìm thấy người gửi hoặc người nhận: sender=" +
                    (sender != null ? sender.getTaiKhoan() : "null") +
                    ", receiver=" + (receiver != null ? receiver.getTaiKhoan() : "null"));
            }
        } else {
            System.out.println("Payload không hợp lệ - standardMsg là null");
        }
    }

    @MessageMapping("/chat.addUser")
    public void addUser(SimpMessageHeaderAccessor headerAccessor, @Payload ChatMessage chatMessage) {
        // Thêm người dùng vào phiên
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
    }
}