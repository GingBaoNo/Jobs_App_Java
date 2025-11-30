package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "jobdetails")
public class JobDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_cong_viec")
    private Integer maCongViec;

    @ManyToOne
    @JoinColumn(name = "ma_cong_ty", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "ma_linh_vuc", nullable = false)
    private WorkField workField;

    @ManyToOne
    @JoinColumn(name = "ma_hinh_thuc", nullable = false)
    private WorkType workType;

    @Column(name = "tieu_de", nullable = false)
    private String tieuDe;

    @Column(name = "luong")
    private Integer luong;

    @Column(name = "loai_luong")
    private String loaiLuong;

    @Column(name = "gio_bat_dau")
    private LocalTime gioBatDau;

    @Column(name = "gio_ket_thuc")
    private LocalTime gioKetThuc;

    @Column(name = "co_the_thuong_luong_gio")
    private String coTheThuongLuongGio;

    @Column(name = "gioi_tinh_yeu_cau")
    private String gioiTinhYeuCau;

    @Column(name = "so_luong_tuyen")
    private Integer soLuongTuyen;

    @Column(name = "ngay_lam_viec")
    private String ngayLamViec;

    @Column(name = "thoi_han_lam_viec")
    private String thoiHanLamViec;

    @Column(name = "co_the_thuong_luong_ngay")
    private String coTheThuongLuongNgay;

    @Lob
    @Column(name = "chi_tiet")
    private String chiTiet;

    @Lob
    @Column(name = "yeu_cau_cong_viec")
    private String yeuCauCongViec;

    @Lob
    @Column(name = "quyen_loi")
    private String quyenLoi;

    @Column(name = "ngay_ket_thuc_tuyen_dung")
    private LocalDate ngayKetThucTuyenDung;

    @Column(name = "ngay_dang")
    private LocalDateTime ngayDang;

    @Column(name = "luot_xem")
    private Integer luotXem = 0;

    @Column(name = "trang_thai_duyet", nullable = false)
    private String trangThaiDuyet = "Chờ duyệt";

    @Column(name = "trang_thai_tin_tuyen", nullable = false)
    private String trangThaiTinTuyen = "Mở";

    // Constructors
    public JobDetail() {}

    public JobDetail(Company company, WorkField workField, WorkType workType, String tieuDe) {
        this.company = company;
        this.workField = workField;
        this.workType = workType;
        this.tieuDe = tieuDe;
        this.ngayDang = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getMaCongViec() {
        return maCongViec;
    }

    public void setMaCongViec(Integer maCongViec) {
        this.maCongViec = maCongViec;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public WorkField getWorkField() {
        return workField;
    }

    public void setWorkField(WorkField workField) {
        this.workField = workField;
    }

    public WorkType getWorkType() {
        return workType;
    }

    public void setWorkType(WorkType workType) {
        this.workType = workType;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public Integer getLuong() {
        return luong;
    }

    public void setLuong(Integer luong) {
        this.luong = luong;
    }

    public String getLoaiLuong() {
        return loaiLuong;
    }

    public void setLoaiLuong(String loaiLuong) {
        this.loaiLuong = loaiLuong;
    }

    public LocalTime getGioBatDau() {
        return gioBatDau;
    }

    public void setGioBatDau(LocalTime gioBatDau) {
        this.gioBatDau = gioBatDau;
    }

    public LocalTime getGioKetThuc() {
        return gioKetThuc;
    }

    public void setGioKetThuc(LocalTime gioKetThuc) {
        this.gioKetThuc = gioKetThuc;
    }

    public String getCoTheThuongLuongGio() {
        return coTheThuongLuongGio;
    }

    public void setCoTheThuongLuongGio(String coTheThuongLuongGio) {
        this.coTheThuongLuongGio = coTheThuongLuongGio;
    }

    public String getGioiTinhYeuCau() {
        return gioiTinhYeuCau;
    }

    public void setGioiTinhYeuCau(String gioiTinhYeuCau) {
        this.gioiTinhYeuCau = gioiTinhYeuCau;
    }

    public Integer getSoLuongTuyen() {
        return soLuongTuyen;
    }

    public void setSoLuongTuyen(Integer soLuongTuyen) {
        this.soLuongTuyen = soLuongTuyen;
    }

    public String getNgayLamViec() {
        return ngayLamViec;
    }

    public void setNgayLamViec(String ngayLamViec) {
        this.ngayLamViec = ngayLamViec;
    }

    public String getThoiHanLamViec() {
        return thoiHanLamViec;
    }

    public void setThoiHanLamViec(String thoiHanLamViec) {
        this.thoiHanLamViec = thoiHanLamViec;
    }

    public String getCoTheThuongLuongNgay() {
        return coTheThuongLuongNgay;
    }

    public void setCoTheThuongLuongNgay(String coTheThuongLuongNgay) {
        this.coTheThuongLuongNgay = coTheThuongLuongNgay;
    }

    public String getChiTiet() {
        return chiTiet;
    }

    public void setChiTiet(String chiTiet) {
        this.chiTiet = chiTiet;
    }

    public LocalDate getNgayKetThucTuyenDung() {
        return ngayKetThucTuyenDung;
    }

    public void setNgayKetThucTuyenDung(LocalDate ngayKetThucTuyenDung) {
        this.ngayKetThucTuyenDung = ngayKetThucTuyenDung;
    }

    public LocalDateTime getNgayDang() {
        return ngayDang;
    }

    public void setNgayDang(LocalDateTime ngayDang) {
        this.ngayDang = ngayDang;
    }

    public Integer getLuotXem() {
        return luotXem;
    }

    public void setLuotXem(Integer luotXem) {
        this.luotXem = luotXem;
    }

    public String getTrangThaiDuyet() {
        return trangThaiDuyet;
    }

    public void setTrangThaiDuyet(String trangThaiDuyet) {
        this.trangThaiDuyet = trangThaiDuyet;
    }

    public String getTrangThaiTinTuyen() {
        return trangThaiTinTuyen;
    }

    public void setTrangThaiTinTuyen(String trangThaiTinTuyen) {
        this.trangThaiTinTuyen = trangThaiTinTuyen;
    }

    public String getYeuCauCongViec() {
        return yeuCauCongViec;
    }

    public void setYeuCauCongViec(String yeuCauCongViec) {
        this.yeuCauCongViec = yeuCauCongViec;
    }

    public String getQuyenLoi() {
        return quyenLoi;
    }

    public void setQuyenLoi(String quyenLoi) {
        this.quyenLoi = quyenLoi;
    }
}