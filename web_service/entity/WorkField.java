package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "work_fields")
public class WorkField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_linh_vuc")
    private Integer maLinhVuc;

    @Column(name = "ten_linh_vuc", nullable = false, unique = true)
    private String tenLinhVuc;

    // Constructors
    public WorkField() {}

    public WorkField(String tenLinhVuc) {
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