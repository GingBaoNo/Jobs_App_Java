package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "work_types")
public class WorkType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_hinh_thuc")
    private Integer maHinhThuc;

    @Column(name = "ten_hinh_thuc", nullable = false, unique = true)
    private String tenHinhThuc;

    // Constructors
    public WorkType() {}

    public WorkType(String tenHinhThuc) {
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