package com.example.fjobs.models;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class JobDetail {
    @SerializedName("maCongViec")
    private Integer maCongViec;

    @SerializedName("maCongTy")
    private Integer maCongTy;

    @SerializedName("tieuDe")
    private String tieuDe;

    @SerializedName("luong")
    private Integer luong;

    @SerializedName("loaiLuong")
    private String loaiLuong;

    @SerializedName("chiTiet")
    private String chiTiet;

    @SerializedName("ngayKetThucTuyenDung")
    private String ngayKetThucTuyenDung;

    @SerializedName("ngayDang")
    private String ngayDang;

    @SerializedName("luotXem")
    private Integer luotXem;

    @SerializedName("trangThaiDuyet")
    private String trangThaiDuyet;

    @SerializedName("trangThaiTinTuyen")
    private String trangThaiTinTuyen;

    @SerializedName("company")
    private Company company; // Thêm thông tin công ty

    @SerializedName("soLuongTuyen")
    private Integer soLuongTuyen; // Số lượng tuyển

    @SerializedName("gioiTinhYeuCau")
    private String gioiTinhYeuCau; // Giới tính yêu cầu

    @SerializedName("gioBatDau")
    private String gioBatDau; // Giờ bắt đầu

    @SerializedName("gioKetThuc")
    private String gioKetThuc; // Giờ kết thúc

    @SerializedName("coTheThuongLuongGio")
    private String coTheThuongLuongGio; // Có thể thương lượng giờ

    @SerializedName("ngayLamViec")
    private String ngayLamViec; // Ngày làm việc

    @SerializedName("thoiHanLamViec")
    private String thoiHanLamViec; // Thời hạn làm việc

    @SerializedName("coTheThuongLuongNgay")
    private String coTheThuongLuongNgay; // Có thể thương lượng ngày

    @SerializedName("yeuCauCongViec")
    private String yeuCauCongViec; // Yêu cầu công việc

    @SerializedName("quyenLoi")
    private String quyenLoi; // Quyền lợi

    @SerializedName("jobPosition")
    private JobPosition jobPosition; // Vị trí công việc

    @SerializedName("experienceLevel")
    private ExperienceLevel experienceLevel; // Cấp độ kinh nghiệm

    @SerializedName("kinhDo")
    private BigDecimal kinhDo; // Kinh độ

    @SerializedName("viDo")
    private BigDecimal viDo; // Vĩ độ

    @SerializedName("workField")
    private WorkField workField; // Lĩnh vực công việc

    // Thêm thuộc tính để theo dõi trạng thái lưu việc làm
    private Boolean saved = false;

    // Constructors
    public JobDetail() {}

    public JobDetail(String tieuDe, Integer luong, String loaiLuong, String chiTiet,
                     String ngayKetThucTuyenDung, int maCongTy) {
        this.tieuDe = tieuDe;
        this.luong = luong;
        this.loaiLuong = loaiLuong;
        this.chiTiet = chiTiet;
        this.ngayKetThucTuyenDung = ngayKetThucTuyenDung;
        this.maCongTy = maCongTy;
    }

    // Getters and Setters
    public Integer getMaCongViec() { return maCongViec; }
    public void setMaCongViec(Integer maCongViec) { this.maCongViec = maCongViec; }

    public Integer getMaCongTy() { return maCongTy; }
    public void setMaCongTy(Integer maCongTy) { this.maCongTy = maCongTy; }

    public String getTieuDe() { return tieuDe; }
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }

    public Integer getLuong() { return luong; }
    public void setLuong(Integer luong) { this.luong = luong; }

    public String getLoaiLuong() { return loaiLuong; }
    public void setLoaiLuong(String loaiLuong) { this.loaiLuong = loaiLuong; }

    public String getChiTiet() { return chiTiet; }
    public void setChiTiet(String chiTiet) { this.chiTiet = chiTiet; }

    public String getNgayKetThucTuyenDung() { return ngayKetThucTuyenDung; }
    public void setNgayKetThucTuyenDung(String ngayKetThucTuyenDung) { this.ngayKetThucTuyenDung = ngayKetThucTuyenDung; }

    public String getNgayDang() { return ngayDang; }
    public void setNgayDang(String ngayDang) { this.ngayDang = ngayDang; }

    public Integer getLuotXem() { return luotXem; }
    public void setLuotXem(Integer luotXem) { this.luotXem = luotXem; }

    public String getTrangThaiDuyet() { return trangThaiDuyet; }
    public void setTrangThaiDuyet(String trangThaiDuyet) { this.trangThaiDuyet = trangThaiDuyet; }

    public String getTrangThaiTinTuyen() { return trangThaiTinTuyen; }
    public void setTrangThaiTinTuyen(String trangThaiTinTuyen) { this.trangThaiTinTuyen = trangThaiTinTuyen; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public Integer getSoLuongTuyen() { return soLuongTuyen; }
    public void setSoLuongTuyen(Integer soLuongTuyen) { this.soLuongTuyen = soLuongTuyen; }

    public String getGioiTinhYeuCau() { return gioiTinhYeuCau; }
    public void setGioiTinhYeuCau(String gioiTinhYeuCau) { this.gioiTinhYeuCau = gioiTinhYeuCau; }

    public String getGioBatDau() { return gioBatDau; }
    public void setGioBatDau(String gioBatDau) { this.gioBatDau = gioBatDau; }

    public String getGioKetThuc() { return gioKetThuc; }
    public void setGioKetThuc(String gioKetThuc) { this.gioKetThuc = gioKetThuc; }

    public String getCoTheThuongLuongGio() { return coTheThuongLuongGio; }
    public void setCoTheThuongLuongGio(String coTheThuongLuongGio) { this.coTheThuongLuongGio = coTheThuongLuongGio; }

    public String getNgayLamViec() { return ngayLamViec; }
    public void setNgayLamViec(String ngayLamViec) { this.ngayLamViec = ngayLamViec; }

    public String getThoiHanLamViec() { return thoiHanLamViec; }
    public void setThoiHanLamViec(String thoiHanLamViec) { this.thoiHanLamViec = thoiHanLamViec; }

    public String getCoTheThuongLuongNgay() { return coTheThuongLuongNgay; }
    public void setCoTheThuongLuongNgay(String coTheThuongLuongNgay) { this.coTheThuongLuongNgay = coTheThuongLuongNgay; }

    public String getYeuCauCongViec() { return yeuCauCongViec; }
    public void setYeuCauCongViec(String yeuCauCongViec) { this.yeuCauCongViec = yeuCauCongViec; }

    public String getQuyenLoi() { return quyenLoi; }
    public void setQuyenLoi(String quyenLoi) { this.quyenLoi = quyenLoi; }

    public JobPosition getJobPosition() { return jobPosition; }
    public void setJobPosition(JobPosition jobPosition) { this.jobPosition = jobPosition; }

    public ExperienceLevel getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(ExperienceLevel experienceLevel) { this.experienceLevel = experienceLevel; }

    public BigDecimal getKinhDo() { return kinhDo; }
    public void setKinhDo(BigDecimal kinhDo) { this.kinhDo = kinhDo; }

    public BigDecimal getViDo() { return viDo; }
    public void setViDo(BigDecimal viDo) { this.viDo = viDo; }

    public WorkField getWorkField() { return workField; }
    public void setWorkField(WorkField workField) { this.workField = workField; }

    // Getter và setter cho thuộc tính saved
    public Boolean isSaved() { return saved; }
    public void setSaved(Boolean saved) { this.saved = saved; }
}