package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_tin_nhan")
    private Integer maTinNhan;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_gui", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_nhan", nullable = false)
    private User receiver;

    @Lob
    @Column(name = "noi_dung", nullable = false)
    private String noiDung;

    @Column(name = "da_doc")
    private Boolean daDoc = false;

    @Column(name = "thoi_gian_gui")
    private LocalDateTime thoiGianGui;

    // Constructors
    public Message() {
        this.thoiGianGui = LocalDateTime.now();
    }

    public Message(User sender, User receiver, String noiDung) {
        this.sender = sender;
        this.receiver = receiver;
        this.noiDung = noiDung;
        this.thoiGianGui = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getMaTinNhan() {
        return maTinNhan;
    }

    public void setMaTinNhan(Integer maTinNhan) {
        this.maTinNhan = maTinNhan;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public Boolean getDaDoc() {
        return daDoc;
    }

    public void setDaDoc(Boolean daDoc) {
        this.daDoc = daDoc;
    }

    public LocalDateTime getThoiGianGui() {
        return thoiGianGui;
    }

    public void setThoiGianGui(LocalDateTime thoiGianGui) {
        this.thoiGianGui = thoiGianGui;
    }
}