package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "experience_levels")
public class ExperienceLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_cap_do")
    private Integer maCapDo;

    @Column(name = "ten_cap_do", nullable = false)
    private String tenCapDo;

    // Constructors
    public ExperienceLevel() {}

    public ExperienceLevel(String tenCapDo) {
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