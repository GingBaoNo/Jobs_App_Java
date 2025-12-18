package com.example.fjobs.models;

import com.google.gson.annotations.SerializedName;

public class ExperienceLevel {
    @SerializedName("maCapDo")
    private Integer maCapDo;

    @SerializedName("tenCapDo")
    private String tenCapDo;

    // Constructors
    public ExperienceLevel() {}

    public ExperienceLevel(String tenCapDo) {
        this.tenCapDo = tenCapDo;
    }

    public ExperienceLevel(Integer maCapDo, String tenCapDo) {
        this.maCapDo = maCapDo;
        this.tenCapDo = tenCapDo;
    }

    // Getters and Setters
    public Integer getMaCapDo() {
        return maCapDo;
    }

    public void setMaCapDo(Integer maCapDo) {
        this.maCapDo = maCapDo;
    }

    public String getTenCapDo() {
        return tenCapDo;
    }

    public void setTenCapDo(String tenCapDo) {
        this.tenCapDo = tenCapDo;
    }
}