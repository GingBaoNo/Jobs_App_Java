package com.example.fjobs.models;

import com.google.gson.annotations.SerializedName;

public class WorkType {
    @SerializedName("maHinhThuc")
    private Integer maHinhThuc;

    @SerializedName("tenHinhThuc")
    private String tenHinhThuc;

    // Constructors
    public WorkType() {}

    public WorkType(String tenHinhThuc) {
        this.tenHinhThuc = tenHinhThuc;
    }

    public WorkType(Integer maHinhThuc, String tenHinhThuc) {
        this.maHinhThuc = maHinhThuc;
        this.tenHinhThuc = tenHinhThuc;
    }

    // Getters and Setters
    public Integer getMaHinhThuc() {
        return maHinhThuc;
    }

    public void setMaHinhThuc(Integer maHinhThuc) {
        this.maHinhThuc = maHinhThuc;
    }

    public String getTenHinhThuc() {
        return tenHinhThuc;
    }

    public void setTenHinhThuc(String tenHinhThuc) {
        this.tenHinhThuc = tenHinhThuc;
    }
}