package com.example.fjobs.models;

import com.google.gson.annotations.SerializedName;

public class WorkDiscipline {
    @SerializedName("maNganh")
    private Integer maNganh;

    @SerializedName("tenNganh")
    private String tenNganh;

    @SerializedName("workField")
    private WorkField workField;

    // Constructors
    public WorkDiscipline() {}

    public WorkDiscipline(String tenNganh, WorkField workField) {
        this.tenNganh = tenNganh;
        this.workField = workField;
    }

    public WorkDiscipline(Integer maNganh, String tenNganh, WorkField workField) {
        this.maNganh = maNganh;
        this.tenNganh = tenNganh;
        this.workField = workField;
    }

    // Getters and Setters
    public Integer getMaNganh() {
        return maNganh;
    }

    public void setMaNganh(Integer maNganh) {
        this.maNganh = maNganh;
    }

    public String getTenNganh() {
        return tenNganh;
    }

    public void setTenNganh(String tenNganh) {
        this.tenNganh = tenNganh;
    }

    public WorkField getWorkField() {
        return workField;
    }

    public void setWorkField(WorkField workField) {
        this.workField = workField;
    }
}