package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "skills")
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_ky_nang")
    private Integer maKyNang;

    @Column(name = "ten_ky_nang", nullable = false, unique = true)
    private String tenKyNang;

    // Constructors
    public Skill() {}

    public Skill(String tenKyNang) {
        this.tenKyNang = tenKyNang;
    }

    // Getters and Setters
    public Integer getMaKyNang() {
        return maKyNang;
    }

    public void setMaKyNang(Integer maKyNang) {
        this.maKyNang = maKyNang;
    }

    public String getTenKyNang() {
        return tenKyNang;
    }

    public void setTenKyNang(String tenKyNang) {
        this.tenKyNang = tenKyNang;
    }
}