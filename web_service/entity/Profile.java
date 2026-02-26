package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "profile")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_ho_so")
    private Integer maHoSo;

    @OneToOne
    @JoinColumn(name = "ma_nguoi_tim_viec", nullable = false)
    private User user;

    @Column(name = "url_anh_dai_dien")
    private String urlAnhDaiDien;

    @Column(name = "ho_ten", nullable = false)
    private String hoTen;

    @Column(name = "gioi_tinh", nullable = false)
    private String gioiTinh;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "trinh_do_hoc_van")
    private String trinhDoHocVan;

    @Column(name = "tinh_trang_hoc_van")
    private String tinhTrangHocVan;

    @Lob
    @Column(name = "kinh_nghiem")
    private String kinhNghiem;

    @Column(name = "tong_nam_kinh_nghiem", precision = 4, scale = 2)
    private BigDecimal tongNamKinhNghiem = BigDecimal.ZERO;

    @Lob
    @Column(name = "gioi_thieu_ban_than")
    private String gioiThieuBanThan;

    @Column(name = "url_cv")
    private String urlCv;

    @Column(name = "cong_khai")
    private Boolean congKhai = false;

    @Column(name = "vi_tri_mong_muon")
    private String viTriMongMuon;

    @Column(name = "thoi_gian_mong_muon")
    private String thoiGianMongMuon;

    @Column(name = "loai_thoi_gian_lam_viec")
    private String loaiThoiGianLamViec;

    @Column(name = "hinh_thuc_lam_viec")
    private String hinhThucLamViec;

    @Column(name = "loai_luong_mong_muon")
    private String loaiLuongMongMuon;

    @Column(name = "muc_luong_mong_muon")
    private Integer mucLuongMongMuon;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    // Constructors
    public Profile() {}

    public Profile(User user, String hoTen, String gioiTinh) {
        this.user = user;
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.ngayTao = LocalDateTime.now();
        this.ngayCapNhat = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getMaHoSo() {
        return maHoSo;
    }

    public void setMaHoSo(Integer maHoSo) {
        this.maHoSo = maHoSo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
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

    public BigDecimal getTongNamKinhNghiem() {
        return tongNamKinhNghiem;
    }

    public void setTongNamKinhNghiem(BigDecimal tongNamKinhNghiem) {
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

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public LocalDateTime getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(LocalDateTime ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }
}