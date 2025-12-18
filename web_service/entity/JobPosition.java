package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "job_positions")
public class JobPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_vi_tri")
    private Integer maViTri;

    @Column(name = "ten_vi_tri", nullable = false)
    private String tenViTri;

    @ManyToOne
    @JoinColumn(name = "ma_nganh", nullable = false)
    private WorkDiscipline workDiscipline;

    // Constructors
    public JobPosition() {}

    public JobPosition(String tenViTri, WorkDiscipline workDiscipline) {
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