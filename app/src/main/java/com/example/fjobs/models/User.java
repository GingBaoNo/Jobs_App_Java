package com.example.fjobs.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("maNguoiDung")
    private Integer maNguoiDung;

    @SerializedName("taiKhoan")
    private String taiKhoan;

    @SerializedName("tenHienThi")
    private String tenHienThi;

    @SerializedName("email")
    private String email;

    @SerializedName("soDienThoai")
    private String soDienThoai;

    @SerializedName("maVaiTro")
    private Integer maVaiTro;

    @SerializedName("urlAnhDaiDien")
    private String urlAnhDaiDien;

    // Constructors
    public User() {}

    public User(String taiKhoan, String tenHienThi, String email, String soDienThoai, Integer maVaiTro) {
        this.taiKhoan = taiKhoan;
        this.tenHienThi = tenHienThi;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.maVaiTro = maVaiTro;
    }

    // Getters and Setters
    public Integer getMaNguoiDung() { return maNguoiDung; }
    public void setMaNguoiDung(Integer maNguoiDung) { this.maNguoiDung = maNguoiDung; }

    public String getTaiKhoan() { return taiKhoan; }
    public void setTaiKhoan(String taiKhoan) { this.taiKhoan = taiKhoan; }

    public String getTenHienThi() { return tenHienThi; }
    public void setTenHienThi(String tenHienThi) { this.tenHienThi = tenHienThi; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public Integer getMaVaiTro() { return maVaiTro; }
    public void setMaVaiTro(Integer maVaiTro) { this.maVaiTro = maVaiTro; }

    public String getUrlAnhDaiDien() { return urlAnhDaiDien; }
    public void setUrlAnhDaiDien(String urlAnhDaiDien) { this.urlAnhDaiDien = urlAnhDaiDien; }
}