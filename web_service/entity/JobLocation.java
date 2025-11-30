package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "job_locations", uniqueConstraints = @UniqueConstraint(columnNames = {"ma_cong_viec", "ma_dia_diem"}))
public class JobLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_jblc")
    private Integer maJblc;

    @ManyToOne
    @JoinColumn(name = "ma_cong_viec", nullable = false)
    private JobDetail jobDetail;

    @ManyToOne
    @JoinColumn(name = "ma_dia_diem", nullable = false)
    private Location location;

    // Constructors
    public JobLocation() {}

    public JobLocation(JobDetail jobDetail, Location location) {
        this.jobDetail = jobDetail;
        this.location = location;
    }

    // Getters and Setters
    public Integer getMaJblc() {
        return maJblc;
    }

    public void setMaJblc(Integer maJblc) {
        this.maJblc = maJblc;
    }

    public JobDetail getJobDetail() {
        return jobDetail;
    }

    public void setJobDetail(JobDetail jobDetail) {
        this.jobDetail = jobDetail;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}