package com.example.demo.controller.api;

import com.example.demo.entity.Profile;
import com.example.demo.entity.User;
import com.example.demo.service.ProfileService;
import com.example.demo.service.UserService;
import com.example.demo.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/profiles")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiProfileController {

    @Autowired
    private ProfileService profileService;
    
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllProfiles() {
        List<Profile> profiles = profileService.getAllProfiles();
        List<Map<String, Object>> profileList = profiles.stream()
            .map(this::convertProfileToMap)
            .collect(Collectors.toList());
        return ApiResponseUtil.success("Profiles retrieved successfully", profileList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfileById(@PathVariable Integer id) {
        return profileService.getProfileById(id)
            .map(profile -> {
                Map<String, Object> profileMap = convertProfileToMap(profile);
                return ApiResponseUtil.success("Profile retrieved successfully", profileMap);
            })
            .orElse(ApiResponseUtil.error("Profile not found with id: " + id));
    }

    @PostMapping
    public ResponseEntity<?> createProfile(@RequestBody Profile profile) {
        Profile savedProfile = profileService.saveProfile(profile);
        Map<String, Object> profileMap = convertProfileToMap(savedProfile);
        return ApiResponseUtil.created(profileMap);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable Integer id, @RequestBody Profile profile) {
        if (!profileService.getProfileById(id).isPresent()) {
            return ApiResponseUtil.error("Profile not found with id: " + id);
        }
        profile.setMaHoSo(id);
        Profile updatedProfile = profileService.updateProfile(profile);
        Map<String, Object> profileMap = convertProfileToMap(updatedProfile);
        return ApiResponseUtil.success("Profile updated successfully", profileMap);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProfile(@PathVariable Integer id) {
        if (!profileService.getProfileById(id).isPresent()) {
            return ApiResponseUtil.error("Profile not found with id: " + id);
        }
        profileService.deleteProfile(id);
        return ApiResponseUtil.noContent();
    }
    
    // API mới: Lấy hồ sơ của người dùng hiện tại
    @GetMapping("/my-profile")
    public ResponseEntity<?> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            Optional<User> user = userService.getUserByTaiKhoan(username);
            if (user.isPresent()) {
                Optional<Profile> profile = profileService.getProfileByUser(user.get());
                if (profile.isPresent()) {
                    // Chuyển đổi profile sang dạng map để tránh circular reference
                    Map<String, Object> profileMap = convertProfileToMap(profile.get());
                    return ApiResponseUtil.success("Profile retrieved successfully", profileMap);
                } else {
                    return ApiResponseUtil.error("Profile not found for current user");
                }
            } else {
                return ApiResponseUtil.error("User not found");
            }
        } else {
            return ApiResponseUtil.error("User not authenticated");
        }
    }
    
    // API mới: Tạo hồ sơ cho người dùng hiện tại
    @PostMapping("/my-profile")
    public ResponseEntity<?> createMyProfile(@RequestBody Profile profile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            Optional<User> user = userService.getUserByTaiKhoan(username);
            if (user.isPresent()) {
                try {
                    Profile createdProfile = profileService.createProfileForUser(user.get(), profile.getHoTen(), profile.getGioiTinh());
                    // Cập nhật các trường khác của hồ sơ
                    createdProfile.setUrlAnhDaiDien(profile.getUrlAnhDaiDien());
                    createdProfile.setNgaySinh(profile.getNgaySinh());
                    createdProfile.setSoDienThoai(profile.getSoDienThoai());
                    createdProfile.setTrinhDoHocVan(profile.getTrinhDoHocVan());
                    createdProfile.setTinhTrangHocVan(profile.getTinhTrangHocVan());
                    createdProfile.setKinhNghiem(profile.getKinhNghiem());
                    createdProfile.setTongNamKinhNghiem(profile.getTongNamKinhNghiem());
                    createdProfile.setGioiThieuBanThan(profile.getGioiThieuBanThan());
                    createdProfile.setUrlCv(profile.getUrlCv());
                    createdProfile.setCongKhai(profile.getCongKhai());
                    createdProfile.setViTriMongMuon(profile.getViTriMongMuon());
                    createdProfile.setThoiGianMongMuon(profile.getThoiGianMongMuon());
                    createdProfile.setLoaiThoiGianLamViec(profile.getLoaiThoiGianLamViec());
                    createdProfile.setHinhThucLamViec(profile.getHinhThucLamViec());
                    createdProfile.setLoaiLuongMongMuon(profile.getLoaiLuongMongMuon());
                    createdProfile.setMucLuongMongMuon(profile.getMucLuongMongMuon());
                    
                    Profile savedProfile = profileService.updateProfile(createdProfile);
                    Map<String, Object> profileMap = convertProfileToMap(savedProfile);
                    return ApiResponseUtil.created(profileMap);
                } catch (RuntimeException e) {
                    return ApiResponseUtil.error(e.getMessage());
                }
            } else {
                return ApiResponseUtil.error("User not found");
            }
        } else {
            return ApiResponseUtil.error("User not authenticated");
        }
    }
    
    // API mới: Cập nhật hồ sơ của người dùng hiện tại
    @PutMapping("/my-profile")
    public ResponseEntity<?> updateMyProfile(@RequestBody Profile profile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            Optional<User> user = userService.getUserByTaiKhoan(username);
            if (user.isPresent()) {
                Optional<Profile> existingProfile = profileService.getProfileByUser(user.get());
                if (existingProfile.isPresent()) {
                    Profile updatedProfile = existingProfile.get();
                    // Cập nhật các trường của hồ sơ
                    updatedProfile.setHoTen(profile.getHoTen());
                    updatedProfile.setGioiTinh(profile.getGioiTinh());
                    updatedProfile.setUrlAnhDaiDien(profile.getUrlAnhDaiDien());
                    updatedProfile.setNgaySinh(profile.getNgaySinh());
                    updatedProfile.setSoDienThoai(profile.getSoDienThoai());
                    updatedProfile.setTrinhDoHocVan(profile.getTrinhDoHocVan());
                    updatedProfile.setTinhTrangHocVan(profile.getTinhTrangHocVan());
                    updatedProfile.setKinhNghiem(profile.getKinhNghiem());
                    updatedProfile.setTongNamKinhNghiem(profile.getTongNamKinhNghiem());
                    updatedProfile.setGioiThieuBanThan(profile.getGioiThieuBanThan());
                    updatedProfile.setUrlCv(profile.getUrlCv());
                    updatedProfile.setCongKhai(profile.getCongKhai());
                    updatedProfile.setViTriMongMuon(profile.getViTriMongMuon());
                    updatedProfile.setThoiGianMongMuon(profile.getThoiGianMongMuon());
                    updatedProfile.setLoaiThoiGianLamViec(profile.getLoaiThoiGianLamViec());
                    updatedProfile.setHinhThucLamViec(profile.getHinhThucLamViec());
                    updatedProfile.setLoaiLuongMongMuon(profile.getLoaiLuongMongMuon());
                    updatedProfile.setMucLuongMongMuon(profile.getMucLuongMongMuon());
                    updatedProfile.setNgayCapNhat(java.time.LocalDateTime.now());
                    
                    Profile savedProfile = profileService.updateProfile(updatedProfile);
                    Map<String, Object> profileMap = convertProfileToMap(savedProfile);
                    return ApiResponseUtil.success("Profile updated successfully", profileMap);
                } else {
                    return ApiResponseUtil.error("Profile not found for current user");
                }
            } else {
                return ApiResponseUtil.error("User not found");
            }
        } else {
            return ApiResponseUtil.error("User not authenticated");
        }
    }

    // Helper method để chuyển đổi Profile sang Map để tránh circular reference
    private Map<String, Object> convertProfileToMap(Profile profile) {
        Map<String, Object> profileMap = new HashMap<>();
        profileMap.put("maHoSo", profile.getMaHoSo());
        profileMap.put("hoTen", profile.getHoTen());
        profileMap.put("gioiTinh", profile.getGioiTinh());
        profileMap.put("ngaySinh", profile.getNgaySinh());
        profileMap.put("soDienThoai", profile.getSoDienThoai());
        profileMap.put("trinhDoHocVan", profile.getTrinhDoHocVan());
        profileMap.put("tinhTrangHocVan", profile.getTinhTrangHocVan());
        profileMap.put("kinhNghiem", profile.getKinhNghiem());
        profileMap.put("tongNamKinhNghiem", profile.getTongNamKinhNghiem());
        profileMap.put("gioiThieuBanThan", profile.getGioiThieuBanThan());
        profileMap.put("urlAnhDaiDien", profile.getUrlAnhDaiDien());
        profileMap.put("urlCv", profile.getUrlCv());
        profileMap.put("congKhai", profile.getCongKhai());
        profileMap.put("viTriMongMuon", profile.getViTriMongMuon());
        profileMap.put("thoiGianMongMuon", profile.getThoiGianMongMuon());
        profileMap.put("loaiThoiGianLamViec", profile.getLoaiThoiGianLamViec());
        profileMap.put("hinhThucLamViec", profile.getHinhThucLamViec());
        profileMap.put("loaiLuongMongMuon", profile.getLoaiLuongMongMuon());
        profileMap.put("mucLuongMongMuon", profile.getMucLuongMongMuon());
        profileMap.put("ngayTao", profile.getNgayTao());
        profileMap.put("ngayCapNhat", profile.getNgayCapNhat());
        // Không bao gồm thông tin user để tránh circular reference
        return profileMap;
    }
}