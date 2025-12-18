package com.example.fjobs.models;

import com.google.gson.annotations.SerializedName;

public class WorkField {
    @SerializedName("maLinhVuc")
    private Integer maLinhVuc;

    @SerializedName("tenLinhVuc")
    private String tenLinhVuc;

    // Constructors
    public WorkField() {}

    public WorkField(String tenLinhVuc) {
        this.tenLinhVuc = tenLinhVuc;
    }

    public WorkField(Integer maLinhVuc, String tenLinhVuc) {
        this.maLinhVuc = maLinhVuc;
        this.tenLinhVuc = tenLinhVuc;
    }

    // Getters and Setters
    public Integer getMaLinhVuc() {
        return maLinhVuc;
    }

    public void setMaLinhVuc(Integer maLinhVuc) {
        this.maLinhVuc = maLinhVuc;
    }

    public String getTenLinhVuc() {
        return tenLinhVuc;
    }

    public void setTenLinhVuc(String tenLinhVuc) {
        this.tenLinhVuc = tenLinhVuc;
    }
}