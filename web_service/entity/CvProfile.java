package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cv_profiles")
@Data
public class CvProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_ho_so_cv")
    private Integer maHoSoCv;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nguoi_tim_viec", nullable = false)
    private User nguoiTimViec;

    @Column(name = "ten_ho_so", nullable = false)
    private String tenHoSo;

    @Column(name = "mo_ta", columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

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

    @Column(name = "kinh_nghiem", columnDefinition = "NVARCHAR(MAX)")
    private String kinhNghiem;

    @Column(name = "tong_nam_kinh_nghiem", precision = 4, scale = 2)
    private BigDecimal tongNamKinhNghiem = BigDecimal.ZERO;

    @Column(name = "gioi_thieu_ban_than", columnDefinition = "NVARCHAR(MAX)")
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

    @CreationTimestamp
    @Column(name = "ngay_tao", nullable = true, updatable = false)
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat", nullable = true)
    private LocalDateTime ngayCapNhat;

    @Column(name = "la_mac_dinh")
    private Boolean laMacDinh = false;
}