package com.example.demo.controller.api;

import com.example.demo.entity.CvProfile;
import com.example.demo.entity.User;
import com.example.demo.service.CvProfileService;
import com.example.demo.service.UserService;
import com.example.demo.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/cv-profiles")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiCvProfileController {

    @Autowired
    private CvProfileService cvProfileService;

    @Autowired
    private UserService userService;

    // API: Lấy tất cả hồ sơ CV của người dùng hiện tại
    @GetMapping
    public ResponseEntity<?> getAllCvProfiles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            Optional<User> user = userService.getUserByTaiKhoan(username);
            if (user.isPresent()) {
                List<CvProfile> cvProfiles = cvProfileService.getAllCvProfilesByUser(user.get());
                List<Map<String, Object>> cvProfileList = cvProfiles.stream()
                    .map(this::convertCvProfileToMap)
                    .collect(Collectors.toList());
                return ApiResponseUtil.success("CV Profiles retrieved successfully", cvProfileList);
            } else {
                return ApiResponseUtil.error("User not found");
            }
        } else {
            return ApiResponseUtil.error("User not authenticated");
        }
    }

    // API: Lấy hồ sơ CV của người dùng hiện tại (endpoint tương tự)
    @GetMapping("/my-cv-profiles")
    public ResponseEntity<?> getMyCvProfiles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            Optional<User> user = userService.getUserByTaiKhoan(username);
            if (user.isPresent()) {
                List<CvProfile> cvProfiles = cvProfileService.getAllCvProfilesByUser(user.get());
                List<Map<String, Object>> cvProfileList = cvProfiles.stream()
                    .map(this::convertCvProfileToMap)
                    .collect(Collectors.toList());
                return ApiResponseUtil.success("My CV Profiles retrieved successfully", cvProfileList);
            } else {
                return ApiResponseUtil.error("User not found");
            }
        } else {
            return ApiResponseUtil.error("User not authenticated");
        }
    }

    // API: Lấy hồ sơ CV theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCvProfileById(@PathVariable Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            Optional<User> user = userService.getUserByTaiKhoan(username);
            if (user.isPresent()) {
                CvProfile cvProfile = cvProfileService.getCvProfileById(id, user.get());
                if (cvProfile != null) {
                    Map<String, Object> cvProfileMap = convertCvProfileToMap(cvProfile);
                    return ApiResponseUtil.success("CV Profile retrieved successfully", cvProfileMap);
                } else {
                    return ApiResponseUtil.error("CV Profile not found with id: " + id);
                }
            } else {
                return ApiResponseUtil.error("User not found");
            }
        } else {
            return ApiResponseUtil.error("User not authenticated");
        }
    }

    // API: Tạo hồ sơ CV mới
    @PostMapping
    public ResponseEntity<?> createCvProfile(@RequestBody CvProfile cvProfile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            Optional<User> user = userService.getUserByTaiKhoan(username);
            if (user.isPresent()) {
                try {
                    CvProfile createdCvProfile = cvProfileService.createCvProfile(cvProfile, user.get());
                    Map<String, Object> cvProfileMap = convertCvProfileToMap(createdCvProfile);
                    return ApiResponseUtil.created(cvProfileMap);
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

    // API: Cập nhật hồ sơ CV
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCvProfile(@PathVariable Integer id, @RequestBody CvProfile cvProfile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            Optional<User> user = userService.getUserByTaiKhoan(username);
            if (user.isPresent()) {
                try {
                    CvProfile updatedCvProfile = cvProfileService.updateCvProfile(id, cvProfile, user.get());
                    if (updatedCvProfile != null) {
                        Map<String, Object> cvProfileMap = convertCvProfileToMap(updatedCvProfile);
                        return ApiResponseUtil.success("CV Profile updated successfully", cvProfileMap);
                    } else {
                        return ApiResponseUtil.error("CV Profile not found with id: " + id);
                    }
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

    // API: Xóa hồ sơ CV
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCvProfile(@PathVariable Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            Optional<User> user = userService.getUserByTaiKhoan(username);
            if (user.isPresent()) {
                try {
                    boolean deleted = cvProfileService.deleteCvProfile(id, user.get());
                    if (deleted) {
                        return ApiResponseUtil.noContent();
                    } else {
                        return ApiResponseUtil.error("CV Profile not found with id: " + id);
                    }
                } catch (RuntimeException e) {
                    return ApiResponseUtil.error("Cannot delete CV Profile: " + e.getMessage());
                }
            } else {
                return ApiResponseUtil.error("User not found");
            }
        } else {
            return ApiResponseUtil.error("User not authenticated");
        }
    }

    // API: Đặt hồ sơ CV làm mặc định
    @PutMapping("/{id}/set-default")
    public ResponseEntity<?> setAsDefaultCvProfile(@PathVariable Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            Optional<User> user = userService.getUserByTaiKhoan(username);
            if (user.isPresent()) {
                cvProfileService.setAsDefaultCvProfile(id, user.get());
                return ApiResponseUtil.success("CV Profile set as default successfully", null);
            } else {
                return ApiResponseUtil.error("User not found");
            }
        } else {
            return ApiResponseUtil.error("User not authenticated");
        }
    }

    // API: Lấy hồ sơ CV mặc định của người dùng
    @GetMapping("/default")
    public ResponseEntity<?> getDefaultCvProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            Optional<User> user = userService.getUserByTaiKhoan(username);
            if (user.isPresent()) {
                CvProfile defaultCvProfile = cvProfileService.getDefaultCvProfile(user.get());
                if (defaultCvProfile != null) {
                    Map<String, Object> cvProfileMap = convertCvProfileToMap(defaultCvProfile);
                    return ApiResponseUtil.success("Default CV Profile retrieved successfully", cvProfileMap);
                } else {
                    return ApiResponseUtil.success("No default CV Profile found", null);
                }
            } else {
                return ApiResponseUtil.error("User not found");
            }
        } else {
            return ApiResponseUtil.error("User not authenticated");
        }
    }

    // API: Upload file CV cho hồ sơ cụ thể
    @PostMapping("/{id}/upload-cv")
    public ResponseEntity<?> uploadCv(@PathVariable Integer id, @RequestParam("cvFile") MultipartFile cvFile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            Optional<User> user = userService.getUserByTaiKhoan(username);
            if (user.isPresent()) {
                try {
                    CvProfile cvProfile = cvProfileService.getCvProfileById(id, user.get());
                    if (cvProfile != null) {
                        // Cập nhật đường dẫn file CV - cần có phương thức xử lý upload file trong service
                        // Tạm thời chỉ cập nhật đường dẫn
                        String filePath = "/uploads/cv/" + cvFile.getOriginalFilename(); // Đường dẫn tạm
                        cvProfile.setUrlCv(filePath);
                        
                        CvProfile updatedCvProfile = cvProfileService.updateCvProfile(id, cvProfile, user.get());
                        Map<String, Object> cvProfileMap = convertCvProfileToMap(updatedCvProfile);
                        return ApiResponseUtil.success("CV uploaded successfully", cvProfileMap);
                    } else {
                        return ApiResponseUtil.error("CV Profile not found with id: " + id);
                    }
                } catch (Exception e) {
                    return ApiResponseUtil.error("Error uploading CV: " + e.getMessage());
                }
            } else {
                return ApiResponseUtil.error("User not found");
            }
        } else {
            return ApiResponseUtil.error("User not authenticated");
        }
    }

    // Helper method để chuyển đổi CvProfile sang Map để tránh circular reference
    private Map<String, Object> convertCvProfileToMap(CvProfile cvProfile) {
        Map<String, Object> cvProfileMap = new HashMap<>();
        cvProfileMap.put("maHoSoCv", cvProfile.getMaHoSoCv());
        cvProfileMap.put("tenHoSo", cvProfile.getTenHoSo());
        cvProfileMap.put("moTa", cvProfile.getMoTa());
        cvProfileMap.put("hoTen", cvProfile.getHoTen());
        cvProfileMap.put("gioiTinh", cvProfile.getGioiTinh());
        cvProfileMap.put("ngaySinh", cvProfile.getNgaySinh());
        cvProfileMap.put("soDienThoai", cvProfile.getSoDienThoai());
        cvProfileMap.put("trinhDoHocVan", cvProfile.getTrinhDoHocVan());
        cvProfileMap.put("tinhTrangHocVan", cvProfile.getTinhTrangHocVan());
        cvProfileMap.put("kinhNghiem", cvProfile.getKinhNghiem());
        cvProfileMap.put("tongNamKinhNghiem", cvProfile.getTongNamKinhNghiem());
        cvProfileMap.put("gioiThieuBanThan", cvProfile.getGioiThieuBanThan());
        cvProfileMap.put("urlAnhDaiDien", cvProfile.getUrlAnhDaiDien());
        cvProfileMap.put("urlCv", cvProfile.getUrlCv());
        cvProfileMap.put("congKhai", cvProfile.getCongKhai());
        cvProfileMap.put("viTriMongMuon", cvProfile.getViTriMongMuon());
        cvProfileMap.put("thoiGianMongMuon", cvProfile.getThoiGianMongMuon());
        cvProfileMap.put("loaiThoiGianLamViec", cvProfile.getLoaiThoiGianLamViec());
        cvProfileMap.put("hinhThucLamViec", cvProfile.getHinhThucLamViec());
        cvProfileMap.put("loaiLuongMongMuon", cvProfile.getLoaiLuongMongMuon());
        cvProfileMap.put("mucLuongMongMuon", cvProfile.getMucLuongMongMuon());
        cvProfileMap.put("ngayTao", cvProfile.getNgayTao());
        cvProfileMap.put("ngayCapNhat", cvProfile.getNgayCapNhat());
        cvProfileMap.put("laMacDinh", cvProfile.getLaMacDinh());
        // Không bao gồm thông tin user để tránh circular reference
        return cvProfileMap;
    }
}