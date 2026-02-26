package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otp_codes")
public class OtpCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_otp")
    private Integer maOtp;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "otp_code", nullable = false, length = 6)
    private String otpCode;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "da_su_dung", nullable = false)
    private Boolean daSuDung = false;

    @Column(name = "han_su_dung", nullable = false)
    private LocalDateTime hanSuDung;

    // Constructors
    public OtpCode() {
        this.ngayTao = LocalDateTime.now();
        this.hanSuDung = LocalDateTime.now().plusMinutes(5); // OTP có hiệu lực 5 phút
    }

    public OtpCode(String email, String otpCode) {
        this();
        this.email = email;
        this.otpCode = otpCode;
    }

    // Getters and Setters
    public Integer getMaOtp() {
        return maOtp;
    }

    public void setMaOtp(Integer maOtp) {
        this.maOtp = maOtp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public Boolean getDaSuDung() {
        return daSuDung;
    }

    public void setDaSuDung(Boolean daSuDung) {
        this.daSuDung = daSuDung;
    }

    public LocalDateTime getHanSuDung() {
        return hanSuDung;
    }

    public void setHanSuDung(LocalDateTime hanSuDung) {
        this.hanSuDung = hanSuDung;
    }

    // Kiểm tra OTP có còn hiệu lực không
    public boolean isStillValid() {
        return !this.daSuDung && LocalDateTime.now().isBefore(this.hanSuDung);
    }
}