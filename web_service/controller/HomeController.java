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
import com.example.demo.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    
    @Autowired
    private CompanyService companyService;

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        try {
            // Dữ liệu cho trang chủ
            model.addAttribute("title", "Tìm Việc Làm Nhanh Chóng - Kết Nối Nhà Tuyển Dụng Và Ứng Viên");

            // Thống kê
            List<JobDetail> allJobs = jobDetailService.getAllJobs();
            long totalJobs = allJobs != null ? allJobs.size() : 0;
            model.addAttribute("totalJobs", totalJobs);
            System.out.println("Total jobs: " + totalJobs);

            long totalCompanies = companyService.getAllCompanies() != null ? companyService.getAllCompanies().size() : 0;
            model.addAttribute("totalCompanies", totalCompanies);
            System.out.println("Total companies: " + totalCompanies);

            // Số lượng ứng viên (người dùng vai trò NV)
            long totalApplicants = userService.getAllUsers().stream()
                .filter(u -> u.getRole() != null && "NV".equals(u.getRole().getTenVaiTro()))
                .count();
            model.addAttribute("totalApplicants", totalApplicants);
            System.out.println("Total applicants: " + totalApplicants);

            // Số người đã tìm được việc (ước lượng)
            model.addAttribute("totalEmployed", totalApplicants > 0 ? totalApplicants * 60 / 100 : 0);

            // Việc làm nổi bật (đã duyệt)
            List<JobDetail> featuredJobs = jobDetailService.getJobsByTrangThaiDuyet("Đã duyệt");
            model.addAttribute("featuredJobs", featuredJobs != null ? featuredJobs : new java.util.ArrayList<>());
            System.out.println("Featured jobs: " + (featuredJobs != null ? featuredJobs.size() : 0));

            // Lĩnh vực phổ biến
            List<WorkField> workFields = workFieldService.getAllWorkFields();
            model.addAttribute("workFields", workFields != null ? workFields : new java.util.ArrayList<>());
            System.out.println("Work fields: " + (workFields != null ? workFields.size() : 0));

            // Hình thức làm việc
            List<WorkType> workTypes = workTypeService.getAllWorkTypes();
            model.addAttribute("workTypes", workTypes != null ? workTypes : new java.util.ArrayList<>());

            // Top lĩnh vực có nhiều việc làm
            if (allJobs != null && !allJobs.isEmpty()) {
                Map<WorkField, Long> jobsByField = allJobs.stream()
                    .filter(job -> job.getWorkField() != null)
                    .collect(Collectors.groupingBy(JobDetail::getWorkField, Collectors.counting()));

                List<Map.Entry<WorkField, Long>> topFields = jobsByField.entrySet().stream()
                    .sorted(Map.Entry.<WorkField, Long>comparingByValue().reversed())
                    .limit(8)
                    .collect(Collectors.toList());
                model.addAttribute("topFields", topFields);
                System.out.println("Top fields: " + topFields.size());
            } else {
                model.addAttribute("topFields", new java.util.ArrayList<>());
            }

        } catch (Exception e) {
            System.err.println("Error loading home page data: " + e.getMessage());
            e.printStackTrace();
            // Vẫn return trang chủ ngay cả khi có lỗi
            model.addAttribute("totalJobs", 0);
            model.addAttribute("totalCompanies", 0);
            model.addAttribute("totalApplicants", 0);
            model.addAttribute("totalEmployed", 0);
            model.addAttribute("featuredJobs", new java.util.ArrayList<>());
            model.addAttribute("workFields", new java.util.ArrayList<>());
            model.addAttribute("workTypes", new java.util.ArrayList<>());
            model.addAttribute("topFields", new java.util.ArrayList<>());
        }

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
                user.getEmail(),
                user.getSoDienThoai(),
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
}