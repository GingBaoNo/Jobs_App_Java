package com.example.fjobs.models;

import com.google.gson.annotations.SerializedName;

public class SavedJob {
    @SerializedName("maCvDaLuu")
    private Integer maCvDaLuu;

    @SerializedName("user")
    private User user;

    @SerializedName("jobDetail")
    private JobDetail jobDetail;

    @SerializedName("ngayLuu")
    private String ngayLuu;

    // Constructors
    public SavedJob() {}

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

    public String getNgayLuu() {
        return ngayLuu;
    }

    public void setNgayLuu(String ngayLuu) {
        this.ngayLuu = ngayLuu;
    }
}