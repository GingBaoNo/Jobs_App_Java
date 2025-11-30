package com.example.demo.controller;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.JobDetail;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import com.example.demo.service.JobDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JobDetailService jobDetailService;
    
    @GetMapping("/")
    public String home(Model model) {
        // Lấy các công việc đã được duyệt và còn hiệu lực (chưa hết hạn)
        java.util.List<JobDetail> activeJobs = jobDetailService.getActiveJobsWithValidDate();
        // Lấy tất cả các công việc đã được duyệt
        java.util.List<JobDetail> allApprovedJobs = jobDetailService.getJobsByTrangThaiDuyet("Đã duyệt");
        
        // Hiển thị jobs theo thứ tự: active jobs trước, sau đó là các job đã hết hạn nhưng đã duyệt
        java.util.Set<Integer> activeJobIds = new java.util.HashSet<>();
        for (JobDetail job : activeJobs) {
            activeJobIds.add(job.getMaCongViec());
        }
        
        java.util.List<JobDetail> combinedJobs = new java.util.ArrayList<>();
        combinedJobs.addAll(activeJobs);
        
        // Thêm các job đã duyệt khác mà không trùng với active jobs
        for (JobDetail job : allApprovedJobs) {
            if (!activeJobIds.contains(job.getMaCongViec())) {
                combinedJobs.add(job);
            }
        }
        
        model.addAttribute("jobs", activeJobs); // For active jobs display
        model.addAttribute("allApprovedJobs", combinedJobs); // For main display on home page
        model.addAttribute("title", "Trang chủ - Hệ Thống Tìm Kiếm Việc Làm");
        return "index";
    }
    
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
    
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        // Truyền danh sách vai trò cho form đăng ký
        model.addAttribute("roles", roleService.getAllRoles());
        return "auth/register";
    }
    
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, 
                              @RequestParam Integer roleId,
                              Model model) {
        try {
            // Lấy vai trò từ ID được gửi từ form
            Role role = roleService.getRoleById(roleId).orElse(null);
            if (role == null) {
                model.addAttribute("errorMessage", "Vai trò không hợp lệ.");
                model.addAttribute("user", user);
                model.addAttribute("roles", roleService.getAllRoles());
                return "auth/register";
            }
            
            // Kiểm tra nếu người dùng đang cố đăng ký vai trò admin
            if ("ADMIN".equals(role.getTenVaiTro())) {
                model.addAttribute("errorMessage", "Không thể đăng ký tài khoản quản trị viên từ trang này.");
                model.addAttribute("user", user);
                model.addAttribute("roles", roleService.getAllRoles());
                return "auth/register";
            }
            
            User registeredUser = userService.registerUser(
                user.getTaiKhoan(), 
                user.getMatKhau(), 
                user.getTenHienThi(), 
                user.getLienHe(), 
                role
            );
            
            model.addAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "auth/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi đăng ký: " + e.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("roles", roleService.getAllRoles());
            return "auth/register";
        }
    }
    
    // Trang hồ sơ - sẽ chuyển hướng tùy theo vai trò người dùng
    @GetMapping("/profile")
    public String profile(Model model) {
        // Trang này sẽ kiểm tra vai trò của người dùng và chuyển hướng tương ứng
        return "redirect:/employee/profile"; // Mặc định chuyển hướng đến hồ sơ nhân viên
    }
}