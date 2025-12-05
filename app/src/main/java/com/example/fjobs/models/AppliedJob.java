package com.example.fjobs.models;

import com.google.gson.annotations.SerializedName;

public class AppliedJob {
    @SerializedName("maUngTuyen")
    private Integer maUngTuyen;

    @SerializedName("employee")
    private User employee;

    @SerializedName("jobDetail")
    private JobDetail jobDetail;

    @SerializedName("trangThaiUngTuyen")
    private String trangThaiUngTuyen;

    @SerializedName("danhGiaNtd")
    private Integer danhGiaNtd;

    @SerializedName("ngayUngTuyen")
    private String ngayUngTuyen;

    @SerializedName("urlCvUngTuyen")
    private String urlCvUngTuyen;

    // Constructors
    public AppliedJob() {}

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

    public Integer getDanhGiaNtd() {
        return danhGiaNtd;
    }

    public void setDanhGiaNtd(Integer danhGiaNtd) {
        this.danhGiaNtd = danhGiaNtd;
    }

    public String getNgayUngTuyen() {
        return ngayUngTuyen;
    }

    public void setNgayUngTuyen(String ngayUngTuyen) {
        this.ngayUngTuyen = ngayUngTuyen;
    }

    public String getUrlCvUngTuyen() {
        return urlCvUngTuyen;
    }

    public void setUrlCvUngTuyen(String urlCvUngTuyen) {
        this.urlCvUngTuyen = urlCvUngTuyen;
    }
}