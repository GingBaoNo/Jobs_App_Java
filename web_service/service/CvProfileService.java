package com.example.demo.service;

import com.example.demo.entity.CvProfile;
import com.example.demo.entity.User;
import com.example.demo.repository.CvProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CvProfileService {

    @Autowired
    private CvProfileRepository cvProfileRepository;

    public List<CvProfile> getAllCvProfilesByUser(User user) {
        return cvProfileRepository.findByNguoiTimViec(user);
    }

    public List<CvProfile> getPublicCvProfilesByUser(User user) {
        return cvProfileRepository.findByNguoiTimViecAndCongKhai(user, true);
    }

    public CvProfile getCvProfileById(Integer id, User currentUser) {
        Optional<CvProfile> cvProfile = cvProfileRepository.findById(id);
        if (cvProfile.isPresent() && cvProfile.get().getNguoiTimViec().equals(currentUser)) {
            return cvProfile.get();
        }
        return null;
    }

    public CvProfile getDefaultCvProfile(User user) {
        return cvProfileRepository.findByNguoiTimViecAndLaMacDinh(user, true);
    }

    public CvProfile createCvProfile(CvProfile cvProfile, User currentUser) {
        // Kiểm tra xem tên hồ sơ đã tồn tại chưa
        if (cvProfileRepository.existsByNguoiTimViecAndTenHoSo(currentUser, cvProfile.getTenHoSo())) {
            throw new RuntimeException("Tên hồ sơ đã tồn tại");
        }
        
        cvProfile.setNguoiTimViec(currentUser);
        
        // Nếu đây là hồ sơ đầu tiên hoặc hồ sơ mặc định đang được tạo, đặt làm mặc định
        if (getDefaultCvProfile(currentUser) == null || cvProfile.getLaMacDinh()) {
            cvProfile.setLaMacDinh(true);
        } else {
            cvProfile.setLaMacDinh(false); // Chỉ có một hồ sơ mặc định
        }
        
        return cvProfileRepository.save(cvProfile);
    }

    public CvProfile updateCvProfile(Integer id, CvProfile updatedCvProfile, User currentUser) {
        CvProfile existingCvProfile = getCvProfileById(id, currentUser);
        if (existingCvProfile != null) {
            // Kiểm tra tên hồ sơ mới có trùng với hồ sơ khác không (trừ hồ sơ đang cập nhật)
            if (!updatedCvProfile.getTenHoSo().equals(existingCvProfile.getTenHoSo()) &&
                cvProfileRepository.existsByNguoiTimViecAndTenHoSo(currentUser, updatedCvProfile.getTenHoSo())) {
                throw new RuntimeException("Tên hồ sơ đã tồn tại");
            }
            
            // Cập nhật các thuộc tính
            existingCvProfile.setTenHoSo(updatedCvProfile.getTenHoSo());
            existingCvProfile.setMoTa(updatedCvProfile.getMoTa());
            existingCvProfile.setUrlAnhDaiDien(updatedCvProfile.getUrlAnhDaiDien());
            existingCvProfile.setHoTen(updatedCvProfile.getHoTen());
            existingCvProfile.setGioiTinh(updatedCvProfile.getGioiTinh());
            existingCvProfile.setNgaySinh(updatedCvProfile.getNgaySinh());
            existingCvProfile.setSoDienThoai(updatedCvProfile.getSoDienThoai());
            existingCvProfile.setTrinhDoHocVan(updatedCvProfile.getTrinhDoHocVan());
            existingCvProfile.setTinhTrangHocVan(updatedCvProfile.getTinhTrangHocVan());
            existingCvProfile.setKinhNghiem(updatedCvProfile.getKinhNghiem());
            existingCvProfile.setTongNamKinhNghiem(updatedCvProfile.getTongNamKinhNghiem());
            existingCvProfile.setGioiThieuBanThan(updatedCvProfile.getGioiThieuBanThan());
            existingCvProfile.setUrlCv(updatedCvProfile.getUrlCv());
            existingCvProfile.setCongKhai(updatedCvProfile.getCongKhai());
            existingCvProfile.setViTriMongMuon(updatedCvProfile.getViTriMongMuon());
            existingCvProfile.setThoiGianMongMuon(updatedCvProfile.getThoiGianMongMuon());
            existingCvProfile.setLoaiThoiGianLamViec(updatedCvProfile.getLoaiThoiGianLamViec());
            existingCvProfile.setHinhThucLamViec(updatedCvProfile.getHinhThucLamViec());
            existingCvProfile.setLoaiLuongMongMuon(updatedCvProfile.getLoaiLuongMongMuon());
            existingCvProfile.setMucLuongMongMuon(updatedCvProfile.getMucLuongMongMuon());
            
            // Nếu hồ sơ này được đặt làm mặc định, hủy mặc định của các hồ sơ khác
            if (updatedCvProfile.getLaMacDinh()) {
                unsetDefaultCvProfileForUser(currentUser);
                existingCvProfile.setLaMacDinh(true);
            }
            
            return cvProfileRepository.save(existingCvProfile);
        }
        return null;
    }

    public boolean deleteCvProfile(Integer id, User currentUser) {
        CvProfile cvProfile = getCvProfileById(id, currentUser);
        if (cvProfile != null) {
            // Không cho phép xóa hồ sơ mặc định nếu không còn hồ sơ nào khác
            List<CvProfile> userCvProfiles = getAllCvProfilesByUser(currentUser);
            if (cvProfile.getLaMacDinh() && userCvProfiles.size() <= 1) {
                throw new RuntimeException("Không thể xóa hồ sơ mặc định duy nhất");
            }
            
            cvProfileRepository.delete(cvProfile);
            return true;
        }
        return false;
    }

    public void setAsDefaultCvProfile(Integer id, User currentUser) {
        // Bỏ mặc định của hồ sơ hiện tại
        unsetDefaultCvProfileForUser(currentUser);
        
        // Đặt hồ sơ mới làm mặc định
        CvProfile cvProfile = getCvProfileById(id, currentUser);
        if (cvProfile != null) {
            cvProfile.setLaMacDinh(true);
            cvProfileRepository.save(cvProfile);
        }
    }

    private void unsetDefaultCvProfileForUser(User user) {
        CvProfile defaultCvProfile = getDefaultCvProfile(user);
        if (defaultCvProfile != null) {
            defaultCvProfile.setLaMacDinh(false);
            cvProfileRepository.save(defaultCvProfile);
        }
    }
}