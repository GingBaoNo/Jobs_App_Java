package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.entity.Profile;
import com.example.demo.entity.CvProfile;
import com.example.demo.entity.AppliedJob;
import com.example.demo.entity.SavedJob;
import com.example.demo.service.UserService;
import com.example.demo.service.ProfileService;
import com.example.demo.service.CvProfileService;
import com.example.demo.service.AppliedJobService;
import com.example.demo.service.SavedJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class CandidateController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private CvProfileService cvProfileService;

    @Autowired
    private AppliedJobService appliedJobService;

    @Autowired
    private SavedJobService savedJobService;

    /**
     * Trang hồ sơ ứng viên
     */
    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        User user = userService.getUserByTaiKhoan(username).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        // Chỉ cho phép ứng viên (NV) truy cập
        if (!"NV".equals(user.getRole().getTenVaiTro())) {
            return "redirect:/";
        }

        Profile profile = profileService.getProfileByUser(user).orElse(null);
        if (profile == null) {
            profile = new Profile();
            profile.setUser(user);
        }

        model.addAttribute("profile", profile);
        model.addAttribute("user", user);
        model.addAttribute("title", "Hồ sơ của tôi");

        return "candidate/profile";
    }

    /**
     * Cập nhật hồ sơ
     */
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam(required = false) MultipartFile avatar,
                               @RequestParam(required = false) String hoTen,
                               @RequestParam(required = false) String gioiTinh,
                               @RequestParam(required = false) String ngaySinh,
                               @RequestParam(required = false) String soDienThoai,
                               @RequestParam(required = false) String trinhDoHocVan,
                               @RequestParam(required = false) String kinhNghiem,
                               @RequestParam(required = false) String gioiThieuBanThan,
                               @RequestParam(required = false) String viTriMongMuon,
                               Authentication authentication,
                               Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        User user = userService.getUserByTaiKhoan(username).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        try {
            Profile profile = profileService.getProfileByUser(user).orElse(null);
            if (profile == null) {
                profile = new Profile();
                profile.setUser(user);
            }

            // Cập nhật thông tin
            if (hoTen != null && !hoTen.trim().isEmpty()) {
                profile.setHoTen(hoTen);
            }
            if (gioiTinh != null && !gioiTinh.trim().isEmpty()) {
                profile.setGioiTinh(gioiTinh);
            }
            if (ngaySinh != null && !ngaySinh.trim().isEmpty()) {
                profile.setNgaySinh(java.time.LocalDate.parse(ngaySinh));
            }
            if (soDienThoai != null && !soDienThoai.trim().isEmpty()) {
                profile.setSoDienThoai(soDienThoai);
            }
            if (trinhDoHocVan != null && !trinhDoHocVan.trim().isEmpty()) {
                profile.setTrinhDoHocVan(trinhDoHocVan);
            }
            if (kinhNghiem != null) {
                profile.setKinhNghiem(kinhNghiem);
            }
            if (gioiThieuBanThan != null) {
                profile.setGioiThieuBanThan(gioiThieuBanThan);
            }
            if (viTriMongMuon != null && !viTriMongMuon.trim().isEmpty()) {
                profile.setViTriMongMuon(viTriMongMuon);
            }

            // Xử lý avatar
            if (avatar != null && !avatar.isEmpty()) {
                String avatarUrl = "/uploads/avatars/" + avatar.getOriginalFilename();
                profile.setUrlAnhDaiDien(avatarUrl);
            }

            profile.setNgayCapNhat(java.time.LocalDateTime.now());
            profileService.saveProfile(profile);

            model.addAttribute("successMessage", "Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi cập nhật hồ sơ: " + e.getMessage());
        }

        return "redirect:/profile";
    }

    /**
     * Trang quản lý CV
     */
    @GetMapping("/my-cvs")
    public String myCvs(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        User user = userService.getUserByTaiKhoan(username).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        if (!"NV".equals(user.getRole().getTenVaiTro())) {
            return "redirect:/";
        }

        List<CvProfile> cvProfiles = cvProfileService.getAllCvProfilesByUser(user);
        model.addAttribute("cvProfiles", cvProfiles);
        model.addAttribute("title", "Quản lý CV của tôi");

        return "candidate/my-cvs";
    }

    /**
     * Trang việc làm đã lưu
     */
    @GetMapping("/saved-jobs")
    public String savedJobs(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        User user = userService.getUserByTaiKhoan(username).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        List<SavedJob> savedJobs = savedJobService.getSavedJobsByUser(user);
        model.addAttribute("savedJobs", savedJobs);
        model.addAttribute("title", "Việc làm đã lưu");

        return "candidate/saved-jobs";
    }

    /**
     * Trang lịch sử ứng tuyển
     */
    @GetMapping("/applications")
    public String applications(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        User user = userService.getUserByTaiKhoan(username).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        if (!"NV".equals(user.getRole().getTenVaiTro())) {
            return "redirect:/";
        }

        List<AppliedJob> appliedJobs = appliedJobService.getAllAppliedJobs().stream()
            .filter(job -> job.getEmployee().getMaNguoiDung().equals(user.getMaNguoiDung()))
            .toList();
        
        model.addAttribute("appliedJobs", appliedJobs);
        model.addAttribute("title", "Lịch sử ứng tuyển");

        return "candidate/applications";
    }

    /**
     * Trang tin nhắn
     */
    @GetMapping("/messages")
    public String messages(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        model.addAttribute("title", "Tin nhắn");
        return "candidate/messages";
    }
}
