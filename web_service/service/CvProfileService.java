package com.example.demo.service;

import com.example.demo.entity.CvProfile;
import com.example.demo.entity.User;
import com.example.demo.repository.CvProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CvProfileService {

    @Autowired
    private CvProfileRepository cvProfileRepository;

    // Flag để tránh vòng lặp khi đồng bộ
    private final ThreadLocal<Boolean> syncingToProfile = new ThreadLocal<>();

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

        // Nếu người dùng muốn đặt hồ sơ này làm mặc định
        if (cvProfile.getLaMacDinh()) {
            // Hủy bỏ trạng thái mặc định của các hồ sơ khác
            unsetDefaultCvProfileForUser(currentUser);
            cvProfile.setLaMacDinh(true);
        } else {
            // Nếu không có hồ sơ nào là mặc định và đây là hồ sơ đầu tiên, đặt làm mặc định
            if (getDefaultCvProfile(currentUser) == null) {
                cvProfile.setLaMacDinh(true);
            }
        }

        return cvProfileRepository.save(cvProfile);
    }

    @Autowired
    private ProfileService profileService;

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

            CvProfile savedCvProfile = cvProfileRepository.save(existingCvProfile);

            // Nếu đây là hồ sơ mặc định, đồng bộ thông tin sang hồ sơ cá nhân
            // Chỉ đồng bộ nếu chưa có flag tránh vòng lặp
            if (savedCvProfile.getLaMacDinh() && !Boolean.TRUE.equals(syncingToProfile.get())) {
                syncingToProfile.set(true);
                syncCvProfileToProfile(savedCvProfile);
                syncingToProfile.set(false);
            }

            return savedCvProfile;
        }
        return null;
    }

    /**
     * Đồng bộ thông tin từ hồ sơ CV mặc định sang hồ sơ cá nhân
     */
    private void syncCvProfileToProfile(CvProfile cvProfile) {
        try {
            // Lấy hồ sơ cá nhân của người dùng
            Optional<com.example.demo.entity.Profile> profileOpt = profileService.getProfileByUser(cvProfile.getNguoiTimViec());

            if (profileOpt.isPresent()) {
                com.example.demo.entity.Profile profile = profileOpt.get();

                // Cập nhật thông tin từ hồ sơ CV sang hồ sơ cá nhân
                profile.setHoTen(cvProfile.getHoTen());
                profile.setGioiTinh(cvProfile.getGioiTinh());
                profile.setNgaySinh(cvProfile.getNgaySinh());
                profile.setSoDienThoai(cvProfile.getSoDienThoai());
                profile.setTrinhDoHocVan(cvProfile.getTrinhDoHocVan());
                profile.setTinhTrangHocVan(cvProfile.getTinhTrangHocVan());
                profile.setKinhNghiem(cvProfile.getKinhNghiem());
                profile.setTongNamKinhNghiem(cvProfile.getTongNamKinhNghiem());
                profile.setGioiThieuBanThan(cvProfile.getGioiThieuBanThan());
                profile.setUrlAnhDaiDien(cvProfile.getUrlAnhDaiDien());
                profile.setUrlCv(cvProfile.getUrlCv());
                profile.setViTriMongMuon(cvProfile.getViTriMongMuon());
                profile.setThoiGianMongMuon(cvProfile.getThoiGianMongMuon());
                profile.setLoaiThoiGianLamViec(cvProfile.getLoaiThoiGianLamViec());
                profile.setHinhThucLamViec(cvProfile.getHinhThucLamViec());
                profile.setLoaiLuongMongMuon(cvProfile.getLoaiLuongMongMuon());
                profile.setMucLuongMongMuon(cvProfile.getMucLuongMongMuon());

                // Cập nhật lại hồ sơ cá nhân
                profileService.updateProfile(profile);
            }
        } catch (Exception e) {
            // Ghi log nếu có lỗi xảy ra trong quá trình đồng bộ
            System.err.println("Lỗi khi đồng bộ hồ sơ CV sang hồ sơ cá nhân: " + e.getMessage());
        }
    }

    @Transactional
    public boolean deleteCvProfile(Integer id, User currentUser) {
        CvProfile cvProfile = getCvProfileById(id, currentUser);
        if (cvProfile != null) {
            // Không cho phép xóa hồ sơ mặc định nếu không còn hồ sơ nào khác
            List<CvProfile> userCvProfiles = getAllCvProfilesByUser(currentUser);
            if (cvProfile.getLaMacDinh() && userCvProfiles.size() <= 1) {
                throw new RuntimeException("Không thể xóa hồ sơ mặc định duy nhất");
            }

            // Kiểm tra thêm nếu hồ sơ đang được sử dụng trong ứng tuyển
            // (nếu có logic liên quan đến việc không cho phép xóa hồ sơ đang được sử dụng)

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