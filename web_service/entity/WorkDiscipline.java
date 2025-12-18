package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "work_disciplines")
public class WorkDiscipline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_nganh")
    private Integer maNganh;

    @Column(name = "ten_nganh", nullable = false)
    private String tenNganh;

    @ManyToOne
    @JoinColumn(name = "ma_linh_vuc", nullable = false)
    private WorkField workField;

    // Constructors
    public WorkDiscipline() {}

    public WorkDiscipline(String tenNganh, WorkField workField) {
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