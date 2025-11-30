package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applied_jobs", uniqueConstraints = @UniqueConstraint(columnNames = {"ma_nguoi_tim_viec", "ma_cong_viec"}))
public class AppliedJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_ung_tuyen")
    private Integer maUngTuyen;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_tim_viec", nullable = false)
    private User employee;

    @ManyToOne
    @JoinColumn(name = "ma_cong_viec", nullable = false)
    private JobDetail jobDetail;

    @Column(name = "trang_thai_ung_tuyen", nullable = false)
    private String trangThaiUngTuyen = "Đã gửi"; // "Đã gửi", "Đã xem", "Phỏng vấn", "Tuyển dụng", "Từ chối"

    @Column(name = "danh_gia_ntd")
    private Byte danhGiaNtd; // 1-5 sao

    @Column(name = "ngay_ung_tuyen")
    private LocalDateTime ngayUngTuyen;

    // Constructors
    public AppliedJob() {
        this.ngayUngTuyen = LocalDateTime.now();
    }

    public AppliedJob(User employee, JobDetail jobDetail) {
        this.employee = employee;
        this.jobDetail = jobDetail;
        this.ngayUngTuyen = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getMaUngTuyen() {
        return maUngTuyen;
    }

    public void setMaUngTuyen(Integer maUngTuyen) {
        this.maUngTuyen = maUngTuyen;
    }

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }

    public JobDetail getJobDetail() {
        return jobDetail;
    }

    public void setJobDetail(JobDetail jobDetail) {
        this.jobDetail = jobDetail;
    }

    public String getTrangThaiUngTuyen() {
        return trangThaiUngTuyen;
    }

    public void setTrangThaiUngTuyen(String trangThaiUngTuyen) {
        this.trangThaiUngTuyen = trangThaiUngTuyen;
    }

    public Byte getDanhGiaNtd() {
        return danhGiaNtd;
    }

    public void setDanhGiaNtd(Byte danhGiaNtd) {
        this.danhGiaNtd = danhGiaNtd;
    }

    public LocalDateTime getNgayUngTuyen() {
        return ngayUngTuyen;
    }

    public void setNgayUngTuyen(LocalDateTime ngayUngTuyen) {
        this.ngayUngTuyen = ngayUngTuyen;
    }
}