package com.example.fjobs.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("maNguoiDung")
    private Integer maNguoiDung;

    @SerializedName("taiKhoan")
    private String taiKhoan;

    @SerializedName("tenHienThi")
    private String tenHienThi;

    @SerializedName("lienHe")
    private String lienHe;

    @SerializedName("maVaiTro")
    private Integer maVaiTro;

    // Constructors
    public User() {}

    public User(String taiKhoan, String tenHienThi, String lienHe, Integer maVaiTro) {
        this.taiKhoan = taiKhoan;
        this.tenHienThi = tenHienThi;
        this.lienHe = lienHe;
        this.maVaiTro = maVaiTro;
    }

    // Getters and Setters
    public Integer getMaNguoiDung() { return maNguoiDung; }
    public void setMaNguoiDung(Integer maNguoiDung) { this.maNguoiDung = maNguoiDung; }

    public String getTaiKhoan() { return taiKhoan; }
    public void setTaiKhoan(String taiKhoan) { this.taiKhoan = taiKhoan; }

    public String getTenHienThi() { return tenHienThi; }
    public void setTenHienThi(String tenHienThi) { this.tenHienThi = tenHienThi; }

    public String getLienHe() { return lienHe; }
    public void setLienHe(String lienHe) { this.lienHe = lienHe; }

    public Integer getMaVaiTro() { return maVaiTro; }
    public void setMaVaiTro(Integer maVaiTro) { this.maVaiTro = maVaiTro; }
}