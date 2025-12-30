package com.example.fjobs.models;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class Company {
    @SerializedName("maCongTy")
    private int maCongTy;

    @SerializedName("tenCongTy")
    private String tenCongTy;

    @SerializedName("tenNguoiDaiDien")
    private String tenNguoiDaiDien;

    @SerializedName("maSoThue")
    private String maSoThue;

    @SerializedName("diaChi")
    private String diaChi;

    @SerializedName("lienHeCty")
    private String lienHeCty;

    @SerializedName("hinhAnhCty")
    private String hinhAnhCty;

    @SerializedName("daXacThuc")
    private boolean daXacThuc;

    @SerializedName("moTaCongTy")
    private String moTaCongTy; // Mô tả công ty

    @SerializedName("trangThai")
    private String trangThai; // Trạng thái công ty

    @SerializedName("kinhDo")
    private BigDecimal kinhDo; // Kinh độ

    @SerializedName("viDo")
    private BigDecimal viDo; // Vĩ độ

    // Constructors
    public Company() {}

    public Company(String tenCongTy, String tenNguoiDaiDien, String maSoThue,
                   String diaChi, String lienHeCty, String hinhAnhCty) {
        this.tenCongTy = tenCongTy;
        this.tenNguoiDaiDien = tenNguoiDaiDien;
        this.maSoThue = maSoThue;
        this.diaChi = diaChi;
        this.lienHeCty = lienHeCty;
        this.hinhAnhCty = hinhAnhCty;
    }

    // Getters and Setters
    public int getMaCongTy() { return maCongTy; }
    public void setMaCongTy(int maCongTy) { this.maCongTy = maCongTy; }

    public String getTenCongTy() { return tenCongTy; }
    public void setTenCongTy(String tenCongTy) { this.tenCongTy = tenCongTy; }

    public String getTenNguoiDaiDien() { return tenNguoiDaiDien; }
    public void setTenNguoiDaiDien(String tenNguoiDaiDien) { this.tenNguoiDaiDien = tenNguoiDaiDien; }

    public String getMaSoThue() { return maSoThue; }
    public void setMaSoThue(String maSoThue) { this.maSoThue = maSoThue; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getLienHeCty() { return lienHeCty; }
    public void setLienHeCty(String lienHeCty) { this.lienHeCty = lienHeCty; }

    public String getHinhAnhCty() { return hinhAnhCty; }
    public void setHinhAnhCty(String hinhAnhCty) { this.hinhAnhCty = hinhAnhCty; }

    public boolean isDaXacThuc() { return daXacThuc; }
    public void setDaXacThuc(boolean daXacThuc) { this.daXacThuc = daXacThuc; }

    public String getMoTaCongTy() { return moTaCongTy; }
    public void setMoTaCongTy(String moTaCongTy) { this.moTaCongTy = moTaCongTy; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public BigDecimal getKinhDo() { return kinhDo; }
    public void setKinhDo(BigDecimal kinhDo) { this.kinhDo = kinhDo; }

    public BigDecimal getViDo() { return viDo; }
    public void setViDo(BigDecimal viDo) { this.viDo = viDo; }
}