package com.example.fjobs.models;

import com.google.gson.annotations.SerializedName;

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
}