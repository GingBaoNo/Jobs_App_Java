package com.example.demo.service;

import com.example.demo.entity.Message;
import com.example.demo.entity.User;
import com.example.demo.model.StandardChatMessage;
import com.example.demo.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public List<Message> getMessagesBySenderAndReceiver(User sender, User receiver) {
        return messageRepository.findBySenderAndReceiver(sender, receiver);
    }

    public List<StandardChatMessage> getStandardMessagesBySenderAndReceiver(User sender, User receiver) {
        List<Message> messages = messageRepository.findBySenderAndReceiver(sender, receiver);
        return messages.stream()
                .map(this::convertToStandardChatMessage)
                .collect(Collectors.toList());
    }

    public List<Message> getUnreadMessagesByReceiver(User receiver) {
        return messageRepository.findByReceiverAndDaDoc(receiver, false);
    }

    public List<StandardChatMessage> getStandardUnreadMessagesByReceiver(User receiver) {
        List<Message> messages = messageRepository.findByReceiverAndDaDoc(receiver, false);
        return messages.stream()
                .map(this::convertToStandardChatMessage)
                .collect(Collectors.toList());
    }

    public List<Message> getMessagesByReceiver(User receiver) {
        return messageRepository.findByReceiver(receiver);
    }

    public List<StandardChatMessage> getStandardMessagesByReceiver(User receiver) {
        List<Message> messages = messageRepository.findByReceiver(receiver);
        return messages.stream()
                .map(this::convertToStandardChatMessage)
                .collect(Collectors.toList());
    }

    public Message saveMessage(Message message) {
        System.out.println("Đang lưu tin nhắn từ " + (message.getSender() != null ? message.getSender().getTaiKhoan() : "null") +
                          " đến " + (message.getReceiver() != null ? message.getReceiver().getTaiKhoan() : "null"));
        System.out.println("Message ID trước khi lưu: " + message.getMaTinNhan());
        System.out.println("Nội dung tin nhắn: " + message.getNoiDung());
        System.out.println("Thời gian gửi: " + message.getThoiGianGui());

        // Tạo một đối tượng mới hoàn toàn để tránh dirty checking
        Message newMessage = new Message();
        newMessage.setSender(message.getSender());
        newMessage.setReceiver(message.getReceiver());
        newMessage.setNoiDung(message.getNoiDung());
        newMessage.setDaDoc(message.getDaDoc());
        newMessage.setThoiGianGui(message.getThoiGianGui());
        // Không gán ID để đảm bảo tạo mới

        System.out.println("Đang tạo đối tượng Message mới để lưu...");
        Message savedMessage = messageRepository.save(newMessage);
        System.out.println("Tin nhắn đã được lưu với ID: " + (savedMessage != null ? savedMessage.getMaTinNhan() : "null"));

        if (savedMessage != null) {
            // Gửi thông báo reload sau khi lưu tin nhắn thành công
            try {
                if (notificationService != null && savedMessage.getSender() != null && savedMessage.getReceiver() != null) {
                    notificationService.notifyUserReload(savedMessage.getSender().getTaiKhoan(), "new_message");
                    notificationService.notifyUserReload(savedMessage.getReceiver().getTaiKhoan(), "new_message");
                }
            } catch (Exception e) {
                System.out.println("Lỗi khi gửi thông báo reload: " + e.getMessage());
            }
        } else {
            System.out.println("LỖI: savedMessage là null sau khi lưu!");
        }

        return savedMessage;
    }

    public StandardChatMessage saveStandardMessage(StandardChatMessage standardMsg) {
        // Chuyển đổi từ StandardChatMessage sang Message entity
        // Tìm người gửi và người nhận từ database để đảm bảo toàn vẹn quan hệ
        User sender = userService.findById(standardMsg.getSenderId());
        User receiver = userService.findById(standardMsg.getReceiverId());

        if (sender == null || receiver == null) {
            throw new RuntimeException("Không tìm thấy người gửi hoặc người nhận");
        }

        Message message = new Message(sender, receiver, standardMsg.getContent());
        message.setThoiGianGui(standardMsg.getSendTime());
        message.setDaDoc(standardMsg.getIsRead());

        Message savedMessage = messageRepository.save(message);

        // Chuyển đổi lại sang StandardChatMessage để trả về
        return convertToStandardChatMessage(savedMessage);
    }

    public void markAsRead(Integer id) {
        // Sử dụng phương thức cập nhật trực tiếp trong repository thay vì load và save
        Message message = messageRepository.findById(id).orElse(null);
        if (message != null) {
            message.setDaDoc(true);
            messageRepository.save(message);
        }
    }

    public void markAsReadDirect(Integer id) {
        // Phương thức cập nhật trực tiếp không qua load đối tượng
        messageRepository.updateMessageReadStatus(id, true);
    }

    public void deleteMessage(Integer id) {
        messageRepository.deleteById(id);
    }

    public Message sendMessage(User sender, User receiver, String noiDung) {
        Message message = new Message(sender, receiver, noiDung);
        return saveMessage(message);
    }

    public StandardChatMessage sendStandardMessage(User sender, User receiver, String noiDung) {
        Message message = new Message(sender, receiver, noiDung);
        Message savedMessage = saveMessage(message);
        return convertToStandardChatMessage(savedMessage);
    }

    // Hàm chuyển đổi từ Message sang StandardChatMessage
    private StandardChatMessage convertToStandardChatMessage(Message message) {
        StandardChatMessage standardMsg = new StandardChatMessage();
        standardMsg.setMessageId(message.getMaTinNhan());
        standardMsg.setSenderId(message.getSender().getMaNguoiDung());
        standardMsg.setSenderUsername(message.getSender().getTaiKhoan());
        standardMsg.setSenderDisplayName(message.getSender().getTenHienThi() != null ?
            message.getSender().getTenHienThi() : message.getSender().getTaiKhoan());
        standardMsg.setReceiverId(message.getReceiver().getMaNguoiDung());
        standardMsg.setReceiverUsername(message.getReceiver().getTaiKhoan());
        standardMsg.setReceiverDisplayName(message.getReceiver().getTenHienThi() != null ?
            message.getReceiver().getTenHienThi() : message.getReceiver().getTaiKhoan());
        standardMsg.setContent(message.getNoiDung());
        standardMsg.setSendTime(message.getThoiGianGui());
        standardMsg.setIsRead(message.getDaDoc());
        standardMsg.setType("CHAT");
        return standardMsg;
    }
}