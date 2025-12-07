package com.example.fjobs.models;

import com.google.gson.annotations.SerializedName;

public class Profile {
    @SerializedName("maHoSo")
    private Integer maHoSo;


    @SerializedName("urlAnhDaiDien")
    private String urlAnhDaiDien;

    @SerializedName("hoTen")
    private String hoTen;

    @SerializedName("gioiTinh")
    private String gioiTinh;

    @SerializedName("ngaySinh")
    private String ngaySinh;

    @SerializedName("soDienThoai")
    private String soDienThoai;

    @SerializedName("trinhDoHocVan")
    private String trinhDoHocVan;

    @SerializedName("tinhTrangHocVan")
    private String tinhTrangHocVan;

    @SerializedName("kinhNghiem")
    private String kinhNghiem;

    @SerializedName("tongNamKinhNghiem")
    private Float tongNamKinhNghiem;

    @SerializedName("gioiThieuBanThan")
    private String gioiThieuBanThan;

    @SerializedName("urlCv")
    private String urlCv;

    @SerializedName("congKhai")
    private Boolean congKhai;

    @SerializedName("viTriMongMuon")
    private String viTriMongMuon;

    @SerializedName("thoiGianMongMuon")
    private String thoiGianMongMuon;

    @SerializedName("loaiThoiGianLamViec")
    private String loaiThoiGianLamViec;

    @SerializedName("hinhThucLamViec")
    private String hinhThucLamViec;

    @SerializedName("loaiLuongMongMuon")
    private String loaiLuongMongMuon;

    @SerializedName("mucLuongMongMuon")
    private Integer mucLuongMongMuon;

    @SerializedName("ngayTao")
    private String ngayTao;

    @SerializedName("ngayCapNhat")
    private String ngayCapNhat;

    // Constructors
    public Profile() {}

    // Getters and Setters
    public Integer getMaHoSo() {
        return maHoSo;
    }

    public void setMaHoSo(Integer maHoSo) {
        this.maHoSo = maHoSo;
    }


    public String getUrlAnhDaiDien() {
        return urlAnhDaiDien;
    }

    public void setUrlAnhDaiDien(String urlAnhDaiDien) {
        this.urlAnhDaiDien = urlAnhDaiDien;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(String ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getTrinhDoHocVan() {
        return trinhDoHocVan;
    }

    public void setTrinhDoHocVan(String trinhDoHocVan) {
        this.trinhDoHocVan = trinhDoHocVan;
    }

    public String getTinhTrangHocVan() {
        return tinhTrangHocVan;
    }

    public void setTinhTrangHocVan(String tinhTrangHocVan) {
        this.tinhTrangHocVan = tinhTrangHocVan;
    }

    public String getKinhNghiem() {
        return kinhNghiem;
    }

    public void setKinhNghiem(String kinhNghiem) {
        this.kinhNghiem = kinhNghiem;
    }

    public Float getTongNamKinhNghiem() {
        return tongNamKinhNghiem;
    }

    public void setTongNamKinhNghiem(Float tongNamKinhNghiem) {
        this.tongNamKinhNghiem = tongNamKinhNghiem;
    }

    public String getGioiThieuBanThan() {
        return gioiThieuBanThan;
    }

    public void setGioiThieuBanThan(String gioiThieuBanThan) {
        this.gioiThieuBanThan = gioiThieuBanThan;
    }

    public String getUrlCv() {
        return urlCv;
    }

    public void setUrlCv(String urlCv) {
        this.urlCv = urlCv;
    }

    public Boolean getCongKhai() {
        return congKhai;
    }

    public void setCongKhai(Boolean congKhai) {
        this.congKhai = congKhai;
    }

    public String getViTriMongMuon() {
        return viTriMongMuon;
    }

    public void setViTriMongMuon(String viTriMongMuon) {
        this.viTriMongMuon = viTriMongMuon;
    }

    public String getThoiGianMongMuon() {
        return thoiGianMongMuon;
    }

    public void setThoiGianMongMuon(String thoiGianMongMuon) {
        this.thoiGianMongMuon = thoiGianMongMuon;
    }

    public String getLoaiThoiGianLamViec() {
        return loaiThoiGianLamViec;
    }

    public void setLoaiThoiGianLamViec(String loaiThoiGianLamViec) {
        this.loaiThoiGianLamViec = loaiThoiGianLamViec;
    }

    public String getHinhThucLamViec() {
        return hinhThucLamViec;
    }

    public void setHinhThucLamViec(String hinhThucLamViec) {
        this.hinhThucLamViec = hinhThucLamViec;
    }

    public String getLoaiLuongMongMuon() {
        return loaiLuongMongMuon;
    }

    public void setLoaiLuongMongMuon(String loaiLuongMongMuon) {
        this.loaiLuongMongMuon = loaiLuongMongMuon;
    }

    public Integer getMucLuongMongMuon() {
        return mucLuongMongMuon;
    }

    public void setMucLuongMongMuon(Integer mucLuongMongMuon) {
        this.mucLuongMongMuon = mucLuongMongMuon;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }

}