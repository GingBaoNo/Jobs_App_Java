package com.example.demo.model;

import java.time.LocalDateTime;

public class MessageDTO {
    private Integer maTinNhan;
    private Integer maNguoiGui;
    private String tenNguoiGui;
    private String taiKhoanNguoiGui;
    private Integer maNguoiNhan;
    private String noiDung;
    private Boolean daDoc;
    private LocalDateTime thoiGianGui;

    // Constructors
    public MessageDTO() {}

    public MessageDTO(Integer maNguoiGui, String tenNguoiGui, String taiKhoanNguoiGui, Integer maNguoiNhan, String noiDung, Boolean daDoc, LocalDateTime thoiGianGui) {
        this.maNguoiGui = maNguoiGui;
        this.tenNguoiGui = tenNguoiGui;
        this.taiKhoanNguoiGui = taiKhoanNguoiGui;
        this.maNguoiNhan = maNguoiNhan;
        this.noiDung = noiDung;
        this.daDoc = daDoc;
        this.thoiGianGui = thoiGianGui;
    }

    // Getters and Setters
    public Integer getMaTinNhan() {
        return maTinNhan;
    }

    public void setMaTinNhan(Integer maTinNhan) {
        this.maTinNhan = maTinNhan;
    }

    public Integer getMaNguoiGui() {
        return maNguoiGui;
    }

    public void setMaNguoiGui(Integer maNguoiGui) {
        this.maNguoiGui = maNguoiGui;
    }

    public String getTenNguoiGui() {
        return tenNguoiGui;
    }

    public void setTenNguoiGui(String tenNguoiGui) {
        this.tenNguoiGui = tenNguoiGui;
    }

    public String getTaiKhoanNguoiGui() {
        return taiKhoanNguoiGui;
    }

    public void setTaiKhoanNguoiGui(String taiKhoanNguoiGui) {
        this.taiKhoanNguoiGui = taiKhoanNguoiGui;
    }

    public Integer getMaNguoiNhan() {
        return maNguoiNhan;
    }

    public void setMaNguoiNhan(Integer maNguoiNhan) {
        this.maNguoiNhan = maNguoiNhan;
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