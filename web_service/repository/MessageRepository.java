package com.example.demo.repository;

import com.example.demo.entity.Message;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findBySenderAndReceiver(User sender, User receiver);
    List<Message> findByReceiverAndDaDoc(User receiver, Boolean daDoc);
    List<Message> findByReceiver(User receiver);
}