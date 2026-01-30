package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "work_fields")
public class WorkField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_linh_vuc")
    private Integer maLinhVuc;

    @Column(name = "ten_linh_vuc", nullable = false, unique = true)
    private String tenLinhVuc;

    // Mối quan hệ One-to-Many với JobDetail
    @JsonIgnore // Ngăn serialize để tránh vòng lặp vô hạn
    @OneToMany(mappedBy = "workField", fetch = FetchType.LAZY) // mappedBy trỏ tới trường 'workField' trong JobDetail
    private List<JobDetail> jobDetails;

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

    public List<JobDetail> getJobDetails() {
        return jobDetails;
    }

    public void setJobDetails(List<JobDetail> jobDetails) {
        this.jobDetails = jobDetails;
    }
}