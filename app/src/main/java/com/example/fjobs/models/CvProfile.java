package com.example.fjobs.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CvProfile implements Parcelable {
    @SerializedName("maHoSoCv")
    private Integer maHoSoCv;

    @SerializedName("maNguoiTimViec")
    private Integer maNguoiTimViec;

    @SerializedName("tenHoSo")
    private String tenHoSo;

    @SerializedName("moTa")
    private String moTa;

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
    private BigDecimal tongNamKinhNghiem;

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

    @SerializedName("laMacDinh")
    private Boolean laMacDinh;

    // Constructors
    public CvProfile() {}

    public CvProfile(String tenHoSo, String hoTen, String gioiTinh, String soDienThoai) {
        this.tenHoSo = tenHoSo;
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.soDienThoai = soDienThoai;
    }

    // Getters and Setters
    public Integer getMaHoSoCv() {
        return maHoSoCv;
    }

    public void setMaHoSoCv(Integer maHoSoCv) {
        this.maHoSoCv = maHoSoCv;
    }

    public Integer getMaNguoiTimViec() {
        return maNguoiTimViec;
    }

    public void setMaNguoiTimViec(Integer maNguoiTimViec) {
        this.maNguoiTimViec = maNguoiTimViec;
    }

    public String getTenHoSo() {
        return tenHoSo;
    }

    public void setTenHoSo(String tenHoSo) {
        this.tenHoSo = tenHoSo;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
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

    public String getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }

    public String getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(String ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

    public Boolean getLaMacDinh() {
        return laMacDinh;
    }

    public void setLaMacDinh(Boolean laMacDinh) {
        this.laMacDinh = laMacDinh;
    }

    // Parcelable implementation
    public static final Parcelable.Creator<CvProfile> CREATOR = new Parcelable.Creator<CvProfile>() {
        @Override
        public CvProfile createFromParcel(Parcel source) {
            return new CvProfile(source);
        }

        @Override
        public CvProfile[] newArray(int size) {
            return new CvProfile[size];
        }
    };

    public CvProfile(Parcel in) {
        this.maHoSoCv = (Integer) in.readValue(Integer.class.getClassLoader());
        this.maNguoiTimViec = (Integer) in.readValue(Integer.class.getClassLoader());
        this.tenHoSo = in.readString();
        this.moTa = in.readString();
        this.urlAnhDaiDien = in.readString();
        this.hoTen = in.readString();
        this.gioiTinh = in.readString();
        this.ngaySinh = in.readString();
        this.soDienThoai = in.readString();
        this.trinhDoHocVan = in.readString();
        this.tinhTrangHocVan = in.readString();
        this.kinhNghiem = in.readString();
        String bigDecimalStr = in.readString();
        this.tongNamKinhNghiem = bigDecimalStr != null ? new BigDecimal(bigDecimalStr) : null;
        this.gioiThieuBanThan = in.readString();
        this.urlCv = in.readString();
        this.congKhai = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.viTriMongMuon = in.readString();
        this.thoiGianMongMuon = in.readString();
        this.loaiThoiGianLamViec = in.readString();
        this.hinhThucLamViec = in.readString();
        this.loaiLuongMongMuon = in.readString();
        this.mucLuongMongMuon = (Integer) in.readValue(Integer.class.getClassLoader());
        this.ngayTao = in.readString();
        this.ngayCapNhat = in.readString();
        this.laMacDinh = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.maHoSoCv);
        dest.writeValue(this.maNguoiTimViec);
        dest.writeString(this.tenHoSo);
        dest.writeString(this.moTa);
        dest.writeString(this.urlAnhDaiDien);
        dest.writeString(this.hoTen);
        dest.writeString(this.gioiTinh);
        dest.writeString(this.ngaySinh);
        dest.writeString(this.soDienThoai);
        dest.writeString(this.trinhDoHocVan);
        dest.writeString(this.tinhTrangHocVan);
        dest.writeString(this.kinhNghiem);
        dest.writeString(this.tongNamKinhNghiem != null ? this.tongNamKinhNghiem.toString() : null);
        dest.writeString(this.gioiThieuBanThan);
        dest.writeString(this.urlCv);
        dest.writeValue(this.congKhai);
        dest.writeString(this.viTriMongMuon);
        dest.writeString(this.thoiGianMongMuon);
        dest.writeString(this.loaiThoiGianLamViec);
        dest.writeString(this.hinhThucLamViec);
        dest.writeString(this.loaiLuongMongMuon);
        dest.writeValue(this.mucLuongMongMuon);
        dest.writeString(this.ngayTao);
        dest.writeString(this.ngayCapNhat);
        dest.writeValue(this.laMacDinh);
    }
}