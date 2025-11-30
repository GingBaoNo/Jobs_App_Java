package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "profile_skills", uniqueConstraints = @UniqueConstraint(columnNames = {"ma_ho_so", "ma_ky_nang"}))
public class ProfileSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_ps")
    private Integer maPs;

    @ManyToOne
    @JoinColumn(name = "ma_ho_so", nullable = false)
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "ma_ky_nang", nullable = false)
    private Skill skill;

    @Column(name = "cap_do_thanh_thao")
    private String capDoThanhThao = "Trung cấp"; // "Sơ cấp", "Trung cấp", "Nâng cao"

    // Constructors
    public ProfileSkill() {}

    public ProfileSkill(Profile profile, Skill skill) {
        this.profile = profile;
        this.skill = skill;
    }

    // Getters and Setters
    public Integer getMaPs() {
        return maPs;
    }

    public void setMaPs(Integer maPs) {
        this.maPs = maPs;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public String getCapDoThanhThao() {
        return capDoThanhThao;
    }

    public void setCapDoThanhThao(String capDoThanhThao) {
        this.capDoThanhThao = capDoThanhThao;
    }
}