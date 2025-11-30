package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "[user]")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_nguoi_dung")
    private Integer maNguoiDung;

    @ManyToOne
    @JoinColumn(name = "ma_vai_tro", nullable = false)
    private Role role;

    @Column(name = "tai_khoan", nullable = false, unique = true)
    private String taiKhoan;

    @Column(name = "mat_khau", nullable = false)
    private String matKhau;

    @Column(name = "ten_hien_thi")
    private String tenHienThi;

    @Column(name = "lien_he", nullable = false)
    private String lienHe;

    @Column(name = "trang_thai_hoat_dong")
    private Boolean trangThaiHoatDong = true;

    @Column(name = "da_xac_minh")
    private Boolean daXacMinh = false;

    @Column(name = "lan_cuoi_dang_nhap")
    private LocalDateTime lanCuoiDangNhap;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    // Constructors
    public User() {}

    public User(String taiKhoan, String matKhau, String tenHienThi, String lienHe) {
        this.taiKhoan = taiKhoan;
        this.matKhau = matKhau;
        this.tenHienThi = tenHienThi;
        this.lienHe = lienHe;
    }

    // Getters and Setters
    public Integer getMaNguoiDung() {
        return maNguoiDung;
    }

    public void setMaNguoiDung(Integer maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(String taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }

    public void setTenHienThi(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getLienHe() {
        return lienHe;
    }

    public void setLienHe(String lienHe) {
        this.lienHe = lienHe;
    }

    public Boolean getTrangThaiHoatDong() {
        return trangThaiHoatDong;
    }

    public void setTrangThaiHoatDong(Boolean trangThaiHoatDong) {
        this.trangThaiHoatDong = trangThaiHoatDong;
    }

    public Boolean getDaXacMinh() {
        return daXacMinh;
    }

    public void setDaXacMinh(Boolean daXacMinh) {
        this.daXacMinh = daXacMinh;
    }

    public LocalDateTime getLanCuoiDangNhap() {
        return lanCuoiDangNhap;
    }

    public void setLanCuoiDangNhap(LocalDateTime lanCuoiDangNhap) {
        this.lanCuoiDangNhap = lanCuoiDangNhap;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }
}