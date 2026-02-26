package com.example.demo.controller.api;

import com.example.demo.entity.CvProfile;
import com.example.demo.entity.User;
import com.example.demo.service.CvProfileService;
import com.example.demo.service.UserService;
import com.example.demo.utils.ApiResponseUtil;
import com.example.demo.utils.FileUploadUtil;
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

    @Autowired
    private com.example.demo.repository.CvProfileRepository cvProfileRepository;

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

    // API: Tạo hồ sơ CV mới với upload file
    @PostMapping(value = "/create-with-files", consumes = "multipart/form-data")
    public ResponseEntity<?> createCvProfileWithFiles(
            @RequestParam(value = "tenHoSo", required = true) String tenHoSo,
            @RequestParam(value = "moTa", required = false) String moTa,
            @RequestParam(value = "hoTen", required = false) String hoTen,
            @RequestParam(value = "gioiTinh", required = false) String gioiTinh,
            @RequestParam(value = "ngaySinh", required = false) String ngaySinhStr,
            @RequestParam(value = "soDienThoai", required = false) String soDienThoai,
            @RequestParam(value = "trinhDoHocVan", required = false) String trinhDoHocVan,
            @RequestParam(value = "tinhTrangHocVan", required = false) String tinhTrangHocVan,
            @RequestParam(value = "kinhNghiem", required = false) String kinhNghiem,
            @RequestParam(value = "tongNamKinhNghiem", required = false) String tongNamKinhNghiemStr,
            @RequestParam(value = "gioiThieuBanThan", required = false) String gioiThieuBanThan,
            @RequestParam(value = "congKhai", required = false) Boolean congKhai,
            @RequestParam(value = "viTriMongMuon", required = false) String viTriMongMuon,
            @RequestParam(value = "thoiGianMongMuon", required = false) String thoiGianMongMuon,
            @RequestParam(value = "loaiThoiGianLamViec", required = false) String loaiThoiGianLamViec,
            @RequestParam(value = "hinhThucLamViec", required = false) String hinhThucLamViec,
            @RequestParam(value = "loaiLuongMongMuon", required = false) String loaiLuongMongMuon,
            @RequestParam(value = "mucLuongMongMuon", required = false) Integer mucLuongMongMuon,
            @RequestParam(value = "laMacDinh", required = false) Boolean laMacDinh,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile,
            @RequestParam(value = "cvFile", required = false) MultipartFile cvFile) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            Optional<User> user = userService.getUserByTaiKhoan(username);
            if (user.isPresent()) {
                try {
                    // Tạo hồ sơ CV mới với các thông tin cơ bản
                    CvProfile newCvProfile = new CvProfile();
                    newCvProfile.setTenHoSo(tenHoSo);
                    newCvProfile.setMoTa(moTa);
                    newCvProfile.setHoTen(hoTen != null ? hoTen : "");
                    newCvProfile.setGioiTinh(gioiTinh != null ? gioiTinh : "");

                    if (ngaySinhStr != null && !ngaySinhStr.isEmpty()) {
                        java.time.LocalDate ngaySinh = java.time.LocalDate.parse(ngaySinhStr);
                        newCvProfile.setNgaySinh(ngaySinh);
                    }

                    newCvProfile.setSoDienThoai(soDienThoai);
                    newCvProfile.setTrinhDoHocVan(trinhDoHocVan);
                    newCvProfile.setTinhTrangHocVan(tinhTrangHocVan);
                    newCvProfile.setKinhNghiem(kinhNghiem);

                    if (tongNamKinhNghiemStr != null && !tongNamKinhNghiemStr.isEmpty()) {
                        java.math.BigDecimal tongNamKinhNghiem = new java.math.BigDecimal(tongNamKinhNghiemStr);
                        newCvProfile.setTongNamKinhNghiem(tongNamKinhNghiem);
                    }

                    newCvProfile.setGioiThieuBanThan(gioiThieuBanThan);
                    newCvProfile.setCongKhai(congKhai != null ? congKhai : false);
                    newCvProfile.setViTriMongMuon(viTriMongMuon);
                    newCvProfile.setThoiGianMongMuon(thoiGianMongMuon);
                    newCvProfile.setLoaiThoiGianLamViec(loaiThoiGianLamViec);
                    newCvProfile.setHinhThucLamViec(hinhThucLamViec);
                    newCvProfile.setLoaiLuongMongMuon(loaiLuongMongMuon);
                    newCvProfile.setMucLuongMongMuon(mucLuongMongMuon);
                    newCvProfile.setLaMacDinh(laMacDinh != null ? laMacDinh : false);

                    // Xử lý upload avatar nếu có
                    if (avatarFile != null && !avatarFile.isEmpty()) {
                        String uploadDir = "uploads/avatars/";
                        String fileName = avatarFile.getOriginalFilename();
                        String savedFileName = FileUploadUtil.saveFile(uploadDir, fileName, avatarFile);
                        String avatarUrl = "/uploads/avatars/" + savedFileName;
                        newCvProfile.setUrlAnhDaiDien(avatarUrl);
                    }

                    // Xử lý upload CV nếu có
                    if (cvFile != null && !cvFile.isEmpty()) {
                        String uploadDir = "uploads/cvs/";
                        String fileName = cvFile.getOriginalFilename();
                        String savedFileName = FileUploadUtil.saveFile(uploadDir, fileName, cvFile);
                        String cvUrl = "/uploads/cvs/" + savedFileName;
                        newCvProfile.setUrlCv(cvUrl);
                    }

                    // Gọi service để tạo hồ sơ CV mới
                    CvProfile createdCvProfile = cvProfileService.createCvProfile(newCvProfile, user.get());
                    Map<String, Object> cvProfileMap = convertCvProfileToMap(createdCvProfile);
                    return ApiResponseUtil.created(cvProfileMap);
                } catch (Exception e) {
                    return ApiResponseUtil.error("Error creating CV profile with files: " + e.getMessage());
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
                        // Upload file CV thực tế và cập nhật đường dẫn
                        String uploadDir = "uploads/cvs/";
                        String fileName = cvFile.getOriginalFilename();
                        String savedFileName = FileUploadUtil.saveFile(uploadDir, fileName, cvFile);
                        String cvUrl = "/uploads/cvs/" + savedFileName;

                        // Cập nhật đường dẫn file CV cho hồ sơ
                        cvProfile.setUrlCv(cvUrl);

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

    // API: Upload avatar cho hồ sơ CV cụ thể
    @PostMapping("/{id}/upload-avatar")
    public ResponseEntity<?> uploadAvatar(@PathVariable Integer id, @RequestParam("avatar") MultipartFile avatarFile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            Optional<User> user = userService.getUserByTaiKhoan(username);
            if (user.isPresent()) {
                try {
                    CvProfile cvProfile = cvProfileService.getCvProfileById(id, user.get());
                    if (cvProfile != null) {
                        // Upload file avatar thực tế và cập nhật đường dẫn
                        String uploadDir = "uploads/avatars/";
                        String fileName = avatarFile.getOriginalFilename();
                        String savedFileName = FileUploadUtil.saveFile(uploadDir, fileName, avatarFile);
                        String avatarUrl = "/uploads/avatars/" + savedFileName;

                        // Cập nhật đường dẫn avatar cho hồ sơ
                        cvProfile.setUrlAnhDaiDien(avatarUrl);

                        CvProfile updatedCvProfile = cvProfileService.updateCvProfile(id, cvProfile, user.get());
                        Map<String, Object> cvProfileMap = convertCvProfileToMap(updatedCvProfile);
                        return ApiResponseUtil.success("Avatar uploaded successfully", cvProfileMap);
                    } else {
                        return ApiResponseUtil.error("CV Profile not found with id: " + id);
                    }
                } catch (Exception e) {
                    return ApiResponseUtil.error("Error uploading avatar: " + e.getMessage());
                }
            } else {
                return ApiResponseUtil.error("User not found");
            }
        } else {
            return ApiResponseUtil.error("User not authenticated");
        }
    }

    // API: Cập nhật hồ sơ CV cùng với upload avatar và CV
    @PutMapping(value = "/{id}/update-with-files", consumes = "multipart/form-data")
    public ResponseEntity<?> updateCvProfileWithFiles(
            @PathVariable Integer id,
            @RequestParam(value = "tenHoSo", required = false) String tenHoSo,
            @RequestParam(value = "moTa", required = false) String moTa,
            @RequestParam(value = "hoTen", required = false) String hoTen,
            @RequestParam(value = "gioiTinh", required = false) String gioiTinh,
            @RequestParam(value = "ngaySinh", required = false) String ngaySinhStr,
            @RequestParam(value = "soDienThoai", required = false) String soDienThoai,
            @RequestParam(value = "trinhDoHocVan", required = false) String trinhDoHocVan,
            @RequestParam(value = "tinhTrangHocVan", required = false) String tinhTrangHocVan,
            @RequestParam(value = "kinhNghiem", required = false) String kinhNghiem,
            @RequestParam(value = "tongNamKinhNghiem", required = false) String tongNamKinhNghiemStr,
            @RequestParam(value = "gioiThieuBanThan", required = false) String gioiThieuBanThan,
            @RequestParam(value = "congKhai", required = false) Boolean congKhai,
            @RequestParam(value = "viTriMongMuon", required = false) String viTriMongMuon,
            @RequestParam(value = "thoiGianMongMuon", required = false) String thoiGianMongMuon,
            @RequestParam(value = "loaiThoiGianLamViec", required = false) String loaiThoiGianLamViec,
            @RequestParam(value = "hinhThucLamViec", required = false) String hinhThucLamViec,
            @RequestParam(value = "loaiLuongMongMuon", required = false) String loaiLuongMongMuon,
            @RequestParam(value = "mucLuongMongMuon", required = false) Integer mucLuongMongMuon,
            @RequestParam(value = "laMacDinh", required = false) Boolean laMacDinh,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile,
            @RequestParam(value = "cvFile", required = false) MultipartFile cvFile) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            Optional<User> user = userService.getUserByTaiKhoan(username);
            if (user.isPresent()) {
                try {
                    // Lấy hồ sơ hiện tại
                    CvProfile existingCvProfile = cvProfileService.getCvProfileById(id, user.get());
                    if (existingCvProfile == null) {
                        return ApiResponseUtil.error("CV Profile not found with id: " + id);
                    }

                    // Cập nhật các trường nếu có
                    if (tenHoSo != null) existingCvProfile.setTenHoSo(tenHoSo);
                    if (moTa != null) existingCvProfile.setMoTa(moTa);
                    if (hoTen != null) existingCvProfile.setHoTen(hoTen);
                    if (gioiTinh != null) existingCvProfile.setGioiTinh(gioiTinh);

                    if (ngaySinhStr != null && !ngaySinhStr.isEmpty()) {
                        java.time.LocalDate ngaySinh = java.time.LocalDate.parse(ngaySinhStr);
                        existingCvProfile.setNgaySinh(ngaySinh);
                    }

                    if (soDienThoai != null) existingCvProfile.setSoDienThoai(soDienThoai);
                    if (trinhDoHocVan != null) existingCvProfile.setTrinhDoHocVan(trinhDoHocVan);
                    if (tinhTrangHocVan != null) existingCvProfile.setTinhTrangHocVan(tinhTrangHocVan);
                    if (kinhNghiem != null) existingCvProfile.setKinhNghiem(kinhNghiem);

                    if (tongNamKinhNghiemStr != null && !tongNamKinhNghiemStr.isEmpty()) {
                        java.math.BigDecimal tongNamKinhNghiem = new java.math.BigDecimal(tongNamKinhNghiemStr);
                        existingCvProfile.setTongNamKinhNghiem(tongNamKinhNghiem);
                    }

                    if (gioiThieuBanThan != null) existingCvProfile.setGioiThieuBanThan(gioiThieuBanThan);
                    if (congKhai != null) existingCvProfile.setCongKhai(congKhai);
                    if (viTriMongMuon != null) existingCvProfile.setViTriMongMuon(viTriMongMuon);
                    if (thoiGianMongMuon != null) existingCvProfile.setThoiGianMongMuon(thoiGianMongMuon);
                    if (loaiThoiGianLamViec != null) existingCvProfile.setLoaiThoiGianLamViec(loaiThoiGianLamViec);
                    if (hinhThucLamViec != null) existingCvProfile.setHinhThucLamViec(hinhThucLamViec);
                    if (loaiLuongMongMuon != null) existingCvProfile.setLoaiLuongMongMuon(loaiLuongMongMuon);
                    if (mucLuongMongMuon != null) existingCvProfile.setMucLuongMongMuon(mucLuongMongMuon);
                    if (laMacDinh != null) existingCvProfile.setLaMacDinh(laMacDinh);

                    // Xử lý upload avatar nếu có
                    if (avatarFile != null && !avatarFile.isEmpty()) {
                        // Xóa avatar cũ nếu tồn tại
                        if (existingCvProfile.getUrlAnhDaiDien() != null && !existingCvProfile.getUrlAnhDaiDien().isEmpty()) {
                            String oldFileName = existingCvProfile.getUrlAnhDaiDien().substring(existingCvProfile.getUrlAnhDaiDien().lastIndexOf("/") + 1);
                            FileUploadUtil.deleteFile("uploads/avatars/", oldFileName);
                        }

                        String uploadDir = "uploads/avatars/";
                        String fileName = avatarFile.getOriginalFilename();
                        String savedFileName = FileUploadUtil.saveFile(uploadDir, fileName, avatarFile);
                        String avatarUrl = "/uploads/avatars/" + savedFileName;
                        existingCvProfile.setUrlAnhDaiDien(avatarUrl);
                    }

                    // Xử lý upload CV nếu có
                    if (cvFile != null && !cvFile.isEmpty()) {
                        // Xóa CV cũ nếu tồn tại
                        if (existingCvProfile.getUrlCv() != null && !existingCvProfile.getUrlCv().isEmpty()) {
                            String oldFileName = existingCvProfile.getUrlCv().substring(existingCvProfile.getUrlCv().lastIndexOf("/") + 1);
                            FileUploadUtil.deleteFile("uploads/cvs/", oldFileName);
                        }

                        String uploadDir = "uploads/cvs/";
                        String fileName = cvFile.getOriginalFilename();
                        String savedFileName = FileUploadUtil.saveFile(uploadDir, fileName, cvFile);
                        String cvUrl = "/uploads/cvs/" + savedFileName;
                        existingCvProfile.setUrlCv(cvUrl);
                    }

                    // Gọi service để cập nhật hồ sơ CV
                    CvProfile updatedCvProfile = cvProfileService.updateCvProfile(id, existingCvProfile, user.get());
                    Map<String, Object> cvProfileMap = convertCvProfileToMap(updatedCvProfile);
                    return ApiResponseUtil.success("CV Profile updated successfully", cvProfileMap);
                } catch (Exception e) {
                    return ApiResponseUtil.error("Error updating CV profile with files: " + e.getMessage());
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