package com.example.demo.service;

import com.example.demo.entity.Message;
import com.example.demo.entity.User;
import com.example.demo.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }
    
    public List<Message> getMessagesBySenderAndReceiver(User sender, User receiver) {
        return messageRepository.findBySenderAndReceiver(sender, receiver);
    }
    
    public List<Message> getUnreadMessagesByReceiver(User receiver) {
        return messageRepository.findByReceiverAndDaDoc(receiver, false);
    }
    
    public List<Message> getMessagesByReceiver(User receiver) {
        return messageRepository.findByReceiver(receiver);
    }
    
    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }
    
    public void markAsRead(Integer id) {
        Message message = messageRepository.findById(id).orElse(null);
        if (message != null) {
            message.setDaDoc(true);
            messageRepository.save(message);
        }
    }
    
    public void deleteMessage(Integer id) {
        messageRepository.deleteById(id);
    }
    
    public Message sendMessage(User sender, User receiver, String noiDung) {
        Message message = new Message(sender, receiver, noiDung);
        return saveMessage(message);
    }
}