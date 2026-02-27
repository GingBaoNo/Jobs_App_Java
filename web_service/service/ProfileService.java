package com.example.demo.service;

import com.example.demo.entity.CvProfile;
import com.example.demo.entity.Profile;
import com.example.demo.entity.User;
import com.example.demo.repository.ProfileRepository;
import com.example.demo.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private CvProfileService cvProfileService;

    // Flag để tránh vòng lặp khi đồng bộ
    private final ThreadLocal<Boolean> syncingToCv = new ThreadLocal<>();

    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    public Optional<Profile> getProfileById(Integer id) {
        return profileRepository.findById(id);
    }

    public Optional<Profile> getProfileByUser(User user) {
        return profileRepository.findByUser(user);
    }

    public Profile saveProfile(Profile profile) {
        return profileRepository.save(profile);
    }

    public Profile updateProfile(Profile profile) {
        Profile updatedProfile = profileRepository.save(profile);

        // Đồng bộ thông tin sang hồ sơ CV mặc định nếu có
        // Chỉ đồng bộ nếu chưa có flag tránh vòng lặp
        if (!Boolean.TRUE.equals(syncingToCv.get())) {
            syncingToCv.set(true);
            syncProfileToDefaultCvProfile(updatedProfile);
            syncingToCv.set(false);
        }

        return updatedProfile;
    }

    /**
     * Đồng bộ thông tin từ hồ sơ cá nhân sang hồ sơ CV mặc định
     */
    private void syncProfileToDefaultCvProfile(Profile profile) {
        try {
            // Lấy hồ sơ CV mặc định của người dùng
            CvProfile defaultCvProfile = cvProfileService.getDefaultCvProfile(profile.getUser());

            if (defaultCvProfile != null) {
                // Cập nhật thông tin từ hồ sơ cá nhân sang hồ sơ CV mặc định
                defaultCvProfile.setHoTen(profile.getHoTen());
                defaultCvProfile.setGioiTinh(profile.getGioiTinh());
                defaultCvProfile.setNgaySinh(profile.getNgaySinh());
                defaultCvProfile.setSoDienThoai(profile.getSoDienThoai());
                defaultCvProfile.setTrinhDoHocVan(profile.getTrinhDoHocVan());
                defaultCvProfile.setTinhTrangHocVan(profile.getTinhTrangHocVan());
                defaultCvProfile.setKinhNghiem(profile.getKinhNghiem());
                defaultCvProfile.setTongNamKinhNghiem(profile.getTongNamKinhNghiem());
                defaultCvProfile.setGioiThieuBanThan(profile.getGioiThieuBanThan());
                defaultCvProfile.setUrlAnhDaiDien(profile.getUrlAnhDaiDien());
                defaultCvProfile.setUrlCv(profile.getUrlCv());
                defaultCvProfile.setViTriMongMuon(profile.getViTriMongMuon());
                defaultCvProfile.setThoiGianMongMuon(profile.getThoiGianMongMuon());
                defaultCvProfile.setLoaiThoiGianLamViec(profile.getLoaiThoiGianLamViec());
                defaultCvProfile.setHinhThucLamViec(profile.getHinhThucLamViec());
                defaultCvProfile.setLoaiLuongMongMuon(profile.getLoaiLuongMongMuon());
                defaultCvProfile.setMucLuongMongMuon(profile.getMucLuongMongMuon());

                // Cập nhật lại hồ sơ CV mặc định
                cvProfileService.updateCvProfile(defaultCvProfile.getMaHoSoCv(), defaultCvProfile, profile.getUser());
            }
        } catch (Exception e) {
            // Ghi log nếu có lỗi xảy ra trong quá trình đồng bộ
            System.err.println("Lỗi khi đồng bộ hồ sơ cá nhân sang hồ sơ CV mặc định: " + e.getMessage());
        }
    }

    public void deleteProfile(Integer id) {
        profileRepository.deleteById(id);
    }

    public Profile createProfileForUser(User user, String hoTen, String gioiTinh) {
        Optional<Profile> existingProfile = profileRepository.findByUser(user);
        if (existingProfile.isPresent()) {
            throw new RuntimeException("Hồ sơ cho người dùng này đã tồn tại");
        }

        Profile profile = new Profile(user, hoTen, gioiTinh);
        return saveProfile(profile);
    }

    public Profile updateAvatar(User user, MultipartFile avatarFile) throws IOException {
        Optional<Profile> profileOpt = profileRepository.findByUser(user);
        if (!profileOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy hồ sơ cho người dùng này");
        }

        Profile profile = profileOpt.get();

        // Xóa avatar cũ nếu tồn tại
        if (profile.getUrlAnhDaiDien() != null && !profile.getUrlAnhDaiDien().isEmpty()) {
            // Trích xuất tên file từ URL để xóa
            String oldFileName = profile.getUrlAnhDaiDien().substring(profile.getUrlAnhDaiDien().lastIndexOf("/") + 1);
            FileUploadUtil.deleteFile("uploads/avatars/", oldFileName);
        }

        // Upload avatar mới
        String uploadDir = "uploads/avatars/";
        String fileName = avatarFile.getOriginalFilename();
        String savedFileName = FileUploadUtil.saveFile(uploadDir, fileName, avatarFile);
        String avatarUrl = "/uploads/avatars/" + savedFileName;

        profile.setUrlAnhDaiDien(avatarUrl);

        return profileRepository.save(profile);
    }

    public Profile deleteAvatar(User user) throws IOException {
        Optional<Profile> profileOpt = profileRepository.findByUser(user);
        if (!profileOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy hồ sơ cho người dùng này");
        }

        Profile profile = profileOpt.get();

        // Xóa avatar cũ nếu tồn tại
        if (profile.getUrlAnhDaiDien() != null && !profile.getUrlAnhDaiDien().isEmpty()) {
            // Trích xuất tên file từ URL để xóa
            String oldFileName = profile.getUrlAnhDaiDien().substring(profile.getUrlAnhDaiDien().lastIndexOf("/") + 1);
            FileUploadUtil.deleteFile("uploads/avatars/", oldFileName);
        }

        // Đặt lại URL avatar về null
        profile.setUrlAnhDaiDien(null);

        return profileRepository.save(profile);
    }

    public Profile updateCv(User user, MultipartFile cvFile) throws IOException {
        Optional<Profile> profileOpt = profileRepository.findByUser(user);
        if (!profileOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy hồ sơ cho người dùng này");
        }

        Profile profile = profileOpt.get();

        // Xóa CV cũ nếu tồn tại
        if (profile.getUrlCv() != null && !profile.getUrlCv().isEmpty()) {
            // Trích xuất tên file từ URL để xóa
            String oldFileName = profile.getUrlCv().substring(profile.getUrlCv().lastIndexOf("/") + 1);
            FileUploadUtil.deleteFile("uploads/cvs/", oldFileName);
        }

        // Upload CV mới
        String uploadDir = "uploads/cvs/";
        String fileName = cvFile.getOriginalFilename();
        String savedFileName = FileUploadUtil.saveFile(uploadDir, fileName, cvFile);
        String cvUrl = "/uploads/cvs/" + savedFileName;

        profile.setUrlCv(cvUrl);

        return profileRepository.save(profile);
    }

    /**
     * Tự động tạo hồ sơ mặc định nếu người dùng chưa có hồ sơ
     */
    public Profile createDefaultProfileIfNotExists(User user) {
        Optional<Profile> existingProfile = profileRepository.findByUser(user);

        if (!existingProfile.isPresent()) {
            // Tạo hồ sơ mặc định với thông tin cơ bản từ người dùng
            Profile defaultProfile = new Profile();
            defaultProfile.setUser(user);
            defaultProfile.setHoTen(user.getTenHienThi() != null ? user.getTenHienThi() : user.getTaiKhoan());
            defaultProfile.setGioiTinh("Nam"); // Mặc định là Nam, người dùng có thể cập nhật sau
            defaultProfile.setSoDienThoai(user.getSoDienThoai());
            defaultProfile.setNgayTao(java.time.LocalDateTime.now());
            defaultProfile.setNgayCapNhat(java.time.LocalDateTime.now());

            return profileRepository.save(defaultProfile);
        }

        return existingProfile.get();
    }
}