package com.example.fjobs.models;

import com.google.gson.annotations.SerializedName;

public class JobPosition {
    @SerializedName("maViTri")
    private Integer maViTri;

    @SerializedName("tenViTri")
    private String tenViTri;

    @SerializedName("workDiscipline")
    private WorkDiscipline workDiscipline;

    // Constructors
    public JobPosition() {}

    public JobPosition(String tenViTri, WorkDiscipline workDiscipline) {
        this.tenViTri = tenViTri;
        this.workDiscipline = workDiscipline;
    }

    public JobPosition(Integer maViTri, String tenViTri, WorkDiscipline workDiscipline) {
        this.maViTri = maViTri;
        this.tenViTri = tenViTri;
        this.workDiscipline = workDiscipline;
    }

    // Getters and Setters
    public Integer getMaViTri() {
        return maViTri;
    }

    public void setMaViTri(Integer maViTri) {
        this.maViTri = maViTri;
    }

    public String getTenViTri() {
        return tenViTri;
    }

    public void setTenViTri(String tenViTri) {
        this.tenViTri = tenViTri;
    }

    public WorkDiscipline getWorkDiscipline() {
        return workDiscipline;
    }

    public void setWorkDiscipline(WorkDiscipline workDiscipline) {
        this.workDiscipline = workDiscipline;
    }
}