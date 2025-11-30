package com.example.demo.controller;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.Company;
import com.example.demo.entity.WorkField;
import com.example.demo.entity.WorkType;
import com.example.demo.entity.JobDetail;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import com.example.demo.service.CompanyService;
import com.example.demo.service.WorkFieldService;
import com.example.demo.service.WorkTypeService;
import com.example.demo.service.JobDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private CompanyService companyService;
    
    @Autowired
    private WorkFieldService workFieldService;
    
    @Autowired
    private WorkTypeService workTypeService;
    
    @Autowired
    private JobDetailService jobDetailService;
    
    // Trang dashboard của admin
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        List<User> users = userService.getAllUsers();
        List<JobDetail> allJobs = jobDetailService.getAllJobs();
        List<JobDetail> pendingJobs = jobDetailService.getJobsByTrangThaiDuyet("Chờ duyệt");
        List<JobDetail> approvedJobs = jobDetailService.getJobsByTrangThaiDuyet("Đã duyệt");
        List<Company> companies = companyService.getAllCompanies();

        // Lấy 5 công việc mới nhất theo ngày đăng
        List<JobDetail> recentJobs = allJobs.stream()
            .sorted((j1, j2) -> j2.getNgayDang().compareTo(j1.getNgayDang()))
            .limit(5)
            .toList();

        model.addAttribute("users", users);
        model.addAttribute("allJobs", allJobs);
        model.addAttribute("pendingJobs", pendingJobs);
        model.addAttribute("approvedJobs", approvedJobs);
        model.addAttribute("companies", companies);
        model.addAttribute("recentJobs", recentJobs);
        model.addAttribute("title", "Bảng điều khiển Admin");
        return "admin/dashboard";
    }
    
    // Trang quản lý người dùng
    @GetMapping("/admin/users")
    public String manageUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("title", "Quản lý người dùng");
        return "admin/users";
    }
    
    // Trang quản lý công ty - hiển thị các công ty đang chờ duyệt
    @GetMapping("/admin/companies")
    public String manageCompanies(Model model) {
        // Lấy các công ty đang chờ duyệt (chưa được xác thực)
        List<Company> pendingCompanies = companyService.getUnverifiedCompanies();
        List<Company> verifiedCompanies = companyService.getVerifiedCompanies();
        
        model.addAttribute("pendingCompanies", pendingCompanies);
        model.addAttribute("verifiedCompanies", verifiedCompanies);
        model.addAttribute("title", "Quản lý công ty");
        return "admin/companies";
    }
    
    // Xác nhận công ty
    @PostMapping("/admin/companies/{id}/approve")
    public String approveCompany(@PathVariable Integer id) {
        try {
            companyService.approveCompany(id);
        } catch (Exception e) {
            // Could add error handling if needed
        }
        return "redirect:/admin/companies";
    }
    
    // Từ chối công ty
    @PostMapping("/admin/companies/{id}/reject")
    public String rejectCompany(@PathVariable Integer id) {
        try {
            companyService.rejectCompany(id);
        } catch (Exception e) {
            // Could add error handling if needed
        }
        return "redirect:/admin/companies";
    }
    
    // Trang quản lý vai trò
    @GetMapping("/admin/roles")
    public String manageRoles(Model model) {
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("roles", roles);
        model.addAttribute("title", "Quản lý vai trò");
        return "admin/roles";
    }
    
    // Tạo vai trò mới
    @PostMapping("/admin/roles/create")
    public String createRole(@RequestParam String roleName) {
        Role role = new Role(roleName);
        roleService.saveRole(role);
        return "redirect:/admin/roles";
    }
    
    // Xóa người dùng
    @PostMapping("/admin/users/{id}/delete")
    public String deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
    
    // Cập nhật vai trò cho người dùng
    @PostMapping("/admin/users/{id}/update-role")
    public String updateUserRole(@PathVariable Integer id, @RequestParam Integer roleId) {
        User user = userService.getUserById(id).orElse(null);
        Role role = roleService.getRoleById(roleId).orElse(null);
        
        if (user != null && role != null) {
            user.setRole(role);
            userService.updateUser(user);
        }
        
        return "redirect:/admin/users";
    }
    
    // Trang quản lý lĩnh vực nghề nghiệp (job categories)
    @GetMapping("/admin/work-fields")
    public String manageWorkFields(Model model) {
        try {
            List<WorkField> workFields = workFieldService.getAllWorkFields();
            model.addAttribute("workFields", workFields);
            model.addAttribute("title", "Quản lý ngành nghề");
        } catch (Exception e) {
            // Log the exception (in a real application, use proper logging)
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi tải danh sách ngành nghề");
        }
        return "admin/work-fields";
    }
    
    // Tạo lĩnh vực nghề nghiệp mới
    @PostMapping("/admin/work-fields/create")
    public String createWorkField(@RequestParam String tenLinhVuc) {
        try {
            if (tenLinhVuc != null && !tenLinhVuc.trim().isEmpty()) {
                WorkField workField = new WorkField(tenLinhVuc.trim());
                workFieldService.saveWorkField(workField);
            }
        } catch (Exception e) {
            // Log the exception (in a real application, use proper logging)
            e.printStackTrace();
        }
        return "redirect:/admin/work-fields";
    }
    
    // Cập nhật lĩnh vực nghề nghiệp
    @PostMapping("/admin/work-fields/{id}/update")
    public String updateWorkField(@PathVariable Integer id, @RequestParam String tenLinhVuc) {
        try {
            if (id != null && tenLinhVuc != null && !tenLinhVuc.trim().isEmpty()) {
                workFieldService.getWorkFieldById(id).ifPresent(workField -> {
                    workField.setTenLinhVuc(tenLinhVuc.trim());
                    workFieldService.updateWorkField(workField);
                });
            }
        } catch (Exception e) {
            // Log the exception (in a real application, use proper logging)
            e.printStackTrace();
        }
        return "redirect:/admin/work-fields";
    }
    
    // Xóa lĩnh vực nghề nghiệp
    @PostMapping("/admin/work-fields/{id}/delete")
    public String deleteWorkField(@PathVariable Integer id) {
        try {
            if (id != null) {
                workFieldService.deleteWorkField(id);
            }
        } catch (Exception e) {
            // Log the exception (in a real application, use proper logging)
            e.printStackTrace();
        }
        return "redirect:/admin/work-fields";
    }
    
    // Trang quản lý hình thức làm việc
    @GetMapping("/admin/work-types")
    public String manageWorkTypes(Model model) {
        try {
            List<WorkType> workTypes = workTypeService.getAllWorkTypes();
            model.addAttribute("workTypes", workTypes);
            model.addAttribute("title", "Quản lý hình thức làm việc");
        } catch (Exception e) {
            // Log the exception (in a real application, use proper logging)
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi tải danh sách hình thức làm việc");
        }
        return "admin/work-types";
    }
    
    // Tạo hình thức làm việc mới
    @PostMapping("/admin/work-types/create")
    public String createWorkType(@RequestParam String tenHinhThuc) {
        try {
            if (tenHinhThuc != null && !tenHinhThuc.trim().isEmpty()) {
                WorkType workType = new WorkType(tenHinhThuc.trim());
                workTypeService.saveWorkType(workType);
            }
        } catch (Exception e) {
            // Log the exception (in a real application, use proper logging)
            e.printStackTrace();
        }
        return "redirect:/admin/work-types";
    }
    
    // Cập nhật hình thức làm việc
    @PostMapping("/admin/work-types/{id}/update")
    public String updateWorkType(@PathVariable Integer id, @RequestParam String tenHinhThuc) {
        try {
            if (id != null && tenHinhThuc != null && !tenHinhThuc.trim().isEmpty()) {
                workTypeService.getWorkTypeById(id).ifPresent(workType -> {
                    workType.setTenHinhThuc(tenHinhThuc.trim());
                    workTypeService.updateWorkType(workType);
                });
            }
        } catch (Exception e) {
            // Log the exception (in a real application, use proper logging)
            e.printStackTrace();
        }
        return "redirect:/admin/work-types";
    }
    
    // Xóa hình thức làm việc
    @PostMapping("/admin/work-types/{id}/delete")
    public String deleteWorkType(@PathVariable Integer id) {
        try {
            if (id != null) {
                workTypeService.deleteWorkType(id);
            }
        } catch (Exception e) {
            // Log the exception (in a real application, use proper logging)
            e.printStackTrace();
        }
        return "redirect:/admin/work-types";
    }
}