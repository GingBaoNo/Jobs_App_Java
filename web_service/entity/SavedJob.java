package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "saved_jobs", uniqueConstraints = @UniqueConstraint(columnNames = {"ma_nguoi_dung", "ma_cong_viec"}))
public class SavedJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_cv_da_luu")
    private Integer maCvDaLuu;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "ma_cong_viec", nullable = false)
    private JobDetail jobDetail;

    @Column(name = "ngay_luu", nullable = false)
    private LocalDateTime ngayLuu;

    // Constructors
    public SavedJob() {
        this.ngayLuu = LocalDateTime.now();
    }

    public SavedJob(User user, JobDetail jobDetail) {
        this.user = user;
        this.jobDetail = jobDetail;
        this.ngayLuu = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getMaCvDaLuu() {
        return maCvDaLuu;
    }

    public void setMaCvDaLuu(Integer maCvDaLuu) {
        this.maCvDaLuu = maCvDaLuu;
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

    public LocalDateTime getNgayLuu() {
        return ngayLuu;
    }

    public void setNgayLuu(LocalDateTime ngayLuu) {
        this.ngayLuu = ngayLuu;
    }
}