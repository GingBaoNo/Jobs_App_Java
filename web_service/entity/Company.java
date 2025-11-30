package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "company")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_cong_ty")
    private Integer maCongTy;

    @OneToOne
    @JoinColumn(name = "ma_nha_tuyen_dung", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "ten_cong_ty", nullable = false)
    private String tenCongTy;

    @Column(name = "ten_nguoi_dai_dien")
    private String tenNguoiDaiDien;

    @Column(name = "ma_so_thue")
    private String maSoThue;

    @Column(name = "dia_chi")
    private String diaChi;

    @Column(name = "lien_he_cty")
    private String lienHeCty;

    @Column(name = "hinh_anh_cty")
    private String hinhAnhCty;

    @Column(name = "da_xac_thuc")
    private Boolean daXacThuc = false;

    @Column(name = "trang_thai")
    private String trangThai = "PENDING"; // PENDING, APPROVED, REJECTED

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    // Constructors
    public Company() {}

    public Company(User user, String tenCongTy) {
        this.user = user;
        this.tenCongTy = tenCongTy;
    }

    // Getters and Setters
    public Integer getMaCongTy() {
        return maCongTy;
    }

    public void setMaCongTy(Integer maCongTy) {
        this.maCongTy = maCongTy;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTenCongTy() {
        return tenCongTy;
    }

    public void setTenCongTy(String tenCongTy) {
        this.tenCongTy = tenCongTy;
    }

    public String getTenNguoiDaiDien() {
        return tenNguoiDaiDien;
    }

    public void setTenNguoiDaiDien(String tenNguoiDaiDien) {
        this.tenNguoiDaiDien = tenNguoiDaiDien;
    }

    public String getMaSoThue() {
        return maSoThue;
    }

    public void setMaSoThue(String maSoThue) {
        this.maSoThue = maSoThue;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getLienHeCty() {
        return lienHeCty;
    }

    public void setLienHeCty(String lienHeCty) {
        this.lienHeCty = lienHeCty;
    }

    public String getHinhAnhCty() {
        return hinhAnhCty;
    }

    public void setHinhAnhCty(String hinhAnhCty) {
        this.hinhAnhCty = hinhAnhCty;
    }

    public Boolean getDaXacThuc() {
        return daXacThuc;
    }

    public void setDaXacThuc(Boolean daXacThuc) {
        this.daXacThuc = daXacThuc;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }
}