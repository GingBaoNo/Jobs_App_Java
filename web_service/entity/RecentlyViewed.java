package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recently_viewed", uniqueConstraints = @UniqueConstraint(columnNames = {"ma_nguoi_dung", "ma_cong_viec"}))
public class RecentlyViewed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_xem_gan_day")
    private Integer maXemGanDay;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "ma_cong_viec", nullable = false)
    private JobDetail jobDetail;

    @Column(name = "thoi_gian_xem")
    private LocalDateTime thoiGianXem;

    // Constructors
    public RecentlyViewed() {
        this.thoiGianXem = LocalDateTime.now();
    }

    public RecentlyViewed(User user, JobDetail jobDetail) {
        this.user = user;
        this.jobDetail = jobDetail;
        this.thoiGianXem = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getMaXemGanDay() {
        return maXemGanDay;
    }

    public void setMaXemGanDay(Integer maXemGanDay) {
        this.maXemGanDay = maXemGanDay;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public JobDetail getJobDetail() {
        return jobDetail;
    }

    public void setJobDetail(JobDetail jobDetail) {
        this.jobDetail = jobDetail;
    }

    public LocalDateTime getThoiGianXem() {
        return thoiGianXem;
    }

    public void setThoiGianXem(LocalDateTime thoiGianXem) {
        this.thoiGianXem = thoiGianXem;
    }
}