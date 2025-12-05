package com.example.demo.controller;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.JobDetail;
import com.example.demo.entity.WorkField;
import com.example.demo.entity.WorkType;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import com.example.demo.service.JobDetailService;
import com.example.demo.service.WorkFieldService;
import com.example.demo.service.WorkTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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

    @Autowired
    private WorkFieldService workFieldService;

    @Autowired
    private WorkTypeService workTypeService;

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            // Nếu người dùng đã đăng nhập, chuyển hướng dựa trên vai trò
            String username = authentication.getName();
            User user = userService.getUserByTaiKhoan(username).orElse(null);
            if (user != null) {
                String roleName = user.getRole().getTenVaiTro(); // Lấy tên vai trò từ entity Role
                if ("NTD".equals(roleName)) {
                    return "redirect:/employer/dashboard";
                } else if ("ADMIN".equals(roleName)) {
                    return "redirect:/admin/dashboard";
                }
                // Các vai trò khác (ví dụ: NV nếu vẫn còn) sẽ không bị redirect ở đây
                // nhưng giao diện của index.html sẽ được thay đổi để không phục vụ NV nữa.
            }
        }

        // Nếu chưa đăng nhập, hiển thị trang chủ mới cho NTD và Admin
        model.addAttribute("title", "Chào mừng đến với Web Service Tuyển Dụng");
        // Có thể truyền thêm dữ liệu nếu cần cho trang chủ công khai (thường ít có)
        // Ví dụ: Thống kê số lượng công ty đang hoạt động, hoặc số lượng NTD đã đăng ký?
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
    public String profile(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userService.getUserByTaiKhoan(username).orElse(null);
            if (user != null) {
                String roleName = user.getRole().getTenVaiTro();
                if ("NTD".equals(roleName)) {
                    return "redirect:/employer/dashboard";
                } else if ("ADMIN".equals(roleName)) {
                    return "redirect:/admin/dashboard";
                } else if ("NV".equals(roleName)) {
                    // Trả về trang thông báo nếu là người tìm việc
                    return "employee-not-supported";
                }
                // Các vai trò khác (nếu có) có thể được xử lý nếu cần
            }
        }
        // Nếu không xác thực được hoặc không tìm thấy người dùng, quay về trang chủ
        return "redirect:/";
    }
}