package com.example.demo.controller;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.Company;
import com.example.demo.entity.WorkField;
import com.example.demo.entity.WorkType;
import com.example.demo.entity.JobDetail;
import com.example.demo.entity.Message;
import com.example.demo.entity.WorkDiscipline;
import com.example.demo.entity.JobPosition;
import com.example.demo.entity.ExperienceLevel;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import com.example.demo.service.CompanyService;
import com.example.demo.service.WorkFieldService;
import com.example.demo.service.WorkTypeService;
import com.example.demo.service.JobDetailService;
import com.example.demo.service.MessageService;
import com.example.demo.service.WorkDisciplineService;
import com.example.demo.service.JobPositionService;
import com.example.demo.service.ExperienceLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Autowired
    private MessageService messageService;

    @Autowired
    private WorkDisciplineService workDisciplineService;

    @Autowired
    private JobPositionService jobPositionService;

    @Autowired
    private ExperienceLevelService experienceLevelService;

    // Trang dashboard của admin
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Authentication authentication, Model model) {
        // Lấy thông tin vai trò NTD và NV
        Optional<Role> ntdRoleOpt = roleService.getRoleByTenVaiTro("NTD");
        Optional<Role> nvRoleOpt = roleService.getRoleByTenVaiTro("NV");

        List<User> ntdUsers = new ArrayList<>();
        List<User> nvUsers = new ArrayList<>();

        if (ntdRoleOpt.isPresent()) {
            ntdUsers = userService.getUsersByRole(ntdRoleOpt.get());
        }

        if (nvRoleOpt.isPresent()) {
            nvUsers = userService.getUsersByRole(nvRoleOpt.get());
        }

        List<JobDetail> allJobs = jobDetailService.getAllJobs();
        List<JobDetail> pendingJobs = jobDetailService.getJobsByTrangThaiDuyet("Chờ duyệt");
        List<JobDetail> approvedJobs = jobDetailService.getJobsByTrangThaiDuyet("Đã duyệt");
        List<Company> companies = companyService.getAllCompanies();

        // Lấy 5 công việc mới nhất theo ngày đăng
        List<JobDetail> recentJobs = allJobs.stream()
            .sorted((j1, j2) -> j2.getNgayDang().compareTo(j1.getNgayDang()))
            .limit(5)
            .toList();

        model.addAttribute("ntdUsers", ntdUsers);
        model.addAttribute("nvUsers", nvUsers);
        model.addAttribute("allJobs", allJobs);
        model.addAttribute("pendingJobs", pendingJobs);
        model.addAttribute("approvedJobs", approvedJobs);
        model.addAttribute("companies", companies);
        model.addAttribute("recentJobs", recentJobs);
        // Lấy số lượng tin nhắn chưa đọc (nếu có)
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
            if (user != null) {
                List<Message> unreadMessages = messageService.getUnreadMessagesByReceiver(user);
                model.addAttribute("totalMessages", unreadMessages.size());
            }
        }
        model.addAttribute("title", "Bảng điều khiển Admin");
        return "admin/dashboard";
    }

    // Trang quản lý người dùng
    @GetMapping("/admin/users")
    public String manageUsers(@RequestParam(value = "search", required = false) String search, Model model) {
        List<User> users;
        if (search != null && !search.trim().isEmpty()) {
            users = userService.getUsersBySearch(search);
        } else {
            users = userService.getAllUsers();
        }
        model.addAttribute("users", users);
        model.addAttribute("title", "Quản lý người dùng");
        model.addAttribute("searchQuery", search != null ? search : "");
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
    public String manageRoles(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Role> roles;
        if (search != null && !search.trim().isEmpty()) {
            roles = roleService.getRolesBySearch(search);
        } else {
            roles = roleService.getAllRoles();
        }

        // Tạo map để lưu số lượng người dùng cho từng vai trò
        Map<Integer, Integer> roleUserCount = new HashMap<>();
        for (Role role : roles) {
            List<User> users = userService.getUsersByRole(role);
            roleUserCount.put(role.getMaVaiTro(), users.size());
        }

        model.addAttribute("roles", roles);
        model.addAttribute("roleUserCount", roleUserCount);
        model.addAttribute("title", "Quản lý vai trò");
        model.addAttribute("searchQuery", search != null ? search : "");
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
    public String manageWorkFields(@RequestParam(value = "search", required = false) String search, Model model) {
        try {
            List<WorkField> workFields;
            if (search != null && !search.trim().isEmpty()) {
                workFields = workFieldService.getWorkFieldsBySearch(search);
            } else {
                workFields = workFieldService.getAllWorkFields();
            }
            model.addAttribute("workFields", workFields);
            model.addAttribute("title", "Quản lý ngành nghề");
            model.addAttribute("searchQuery", search != null ? search : "");
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
                WorkField workField = workFieldService.getWorkFieldById(id);
                if (workField != null) {
                    workField.setTenLinhVuc(tenLinhVuc.trim());
                    workFieldService.updateWorkField(workField);
                }
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
    public String manageWorkTypes(@RequestParam(value = "search", required = false) String search, Model model) {
        try {
            List<WorkType> workTypes;
            if (search != null && !search.trim().isEmpty()) {
                workTypes = workTypeService.getWorkTypesBySearch(search);
            } else {
                workTypes = workTypeService.getAllWorkTypes();
            }
            model.addAttribute("workTypes", workTypes);
            model.addAttribute("title", "Quản lý hình thức làm việc");
            model.addAttribute("searchQuery", search != null ? search : "");
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
                WorkType workType = workTypeService.getWorkTypeById(id);
                if (workType != null) {
                    workType.setTenHinhThuc(tenHinhThuc.trim());
                    workTypeService.updateWorkType(workType);
                }
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

    // Trang quản lý ngành nghề (work_disciplines)
    @GetMapping("/admin/work-disciplines")
    public String manageWorkDisciplines(@RequestParam(value = "search", required = false) String search, Model model) {
        try {
            List<WorkDiscipline> workDisciplines;
            if (search != null && !search.trim().isEmpty()) {
                workDisciplines = workDisciplineService.getWorkDisciplinesBySearch(search);
            } else {
                workDisciplines = workDisciplineService.getAllWorkDisciplines();
            }

            // Lấy tất cả lĩnh vực để tạo dropdown
            List<WorkField> workFields = workFieldService.getAllWorkFields();

            model.addAttribute("workDisciplines", workDisciplines);
            model.addAttribute("workFields", workFields);
            model.addAttribute("title", "Quản lý ngành nghề");
            model.addAttribute("searchQuery", search != null ? search : "");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi tải danh sách ngành nghề");
        }
        return "admin/work-disciplines";
    }

    // Tạo ngành nghề mới
    @PostMapping("/admin/work-disciplines/create")
    public String createWorkDiscipline(@RequestParam String tenNganh, @RequestParam Integer maLinhVuc) {
        try {
            if (tenNganh != null && !tenNganh.trim().isEmpty() && maLinhVuc != null) {
                WorkField workField = workFieldService.getWorkFieldById(maLinhVuc);
                if (workField != null) {
                    WorkDiscipline workDiscipline = new WorkDiscipline(tenNganh.trim(), workField);
                    workDisciplineService.saveWorkDiscipline(workDiscipline);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/work-disciplines";
    }

    // Cập nhật ngành nghề
    @PostMapping("/admin/work-disciplines/{id}/update")
    public String updateWorkDiscipline(@PathVariable Integer id, @RequestParam String tenNganh, @RequestParam Integer maLinhVuc) {
        try {
            if (id != null && tenNganh != null && !tenNganh.trim().isEmpty() && maLinhVuc != null) {
                WorkDiscipline workDiscipline = workDisciplineService.getWorkDisciplineById(id);
                if (workDiscipline != null) {
                    WorkField workField = workFieldService.getWorkFieldById(maLinhVuc);
                    if (workField != null) {
                        workDiscipline.setTenNganh(tenNganh.trim());
                        workDiscipline.setWorkField(workField);
                        workDisciplineService.updateWorkDiscipline(workDiscipline);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/work-disciplines";
    }

    // Xóa ngành nghề
    @PostMapping("/admin/work-disciplines/{id}/delete")
    public String deleteWorkDiscipline(@PathVariable Integer id) {
        try {
            if (id != null) {
                workDisciplineService.deleteWorkDiscipline(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/work-disciplines";
    }

    // Trang quản lý vị trí công việc
    @GetMapping("/admin/job-positions")
    public String manageJobPositions(@RequestParam(value = "search", required = false) String search, Model model) {
        try {
            List<JobPosition> jobPositions;
            if (search != null && !search.trim().isEmpty()) {
                jobPositions = jobPositionService.getJobPositionsBySearch(search);
            } else {
                jobPositions = jobPositionService.getAllJobPositions();
            }

            // Lấy tất cả ngành và lĩnh vực để tạo dropdown
            List<WorkDiscipline> workDisciplines = workDisciplineService.getAllWorkDisciplines();
            List<WorkField> workFields = workFieldService.getAllWorkFields();

            model.addAttribute("jobPositions", jobPositions);
            model.addAttribute("workDisciplines", workDisciplines);
            model.addAttribute("workFields", workFields);
            model.addAttribute("title", "Quản lý vị trí công việc");
            model.addAttribute("searchQuery", search != null ? search : "");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi tải danh sách vị trí công việc");
        }
        return "admin/job-positions";
    }

    // Tạo vị trí công việc mới
    @PostMapping("/admin/job-positions/create")
    public String createJobPosition(@RequestParam String tenViTri, @RequestParam Integer maNganh) {
        try {
            if (tenViTri != null && !tenViTri.trim().isEmpty() && maNganh != null) {
                WorkDiscipline workDiscipline = workDisciplineService.getWorkDisciplineById(maNganh);
                if (workDiscipline != null) {
                    JobPosition jobPosition = new JobPosition(tenViTri.trim(), workDiscipline);
                    jobPositionService.saveJobPosition(jobPosition);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/job-positions";
    }

    // Cập nhật vị trí công việc
    @PostMapping("/admin/job-positions/{id}/update")
    public String updateJobPosition(@PathVariable Integer id, @RequestParam String tenViTri, @RequestParam Integer maNganh) {
        try {
            if (id != null && tenViTri != null && !tenViTri.trim().isEmpty() && maNganh != null) {
                JobPosition jobPosition = jobPositionService.getJobPositionById(id);
                if (jobPosition != null) {
                    WorkDiscipline workDiscipline = workDisciplineService.getWorkDisciplineById(maNganh);
                    if (workDiscipline != null) {
                        jobPosition.setTenViTri(tenViTri.trim());
                        jobPosition.setWorkDiscipline(workDiscipline);
                        jobPositionService.updateJobPosition(jobPosition);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/job-positions";
    }

    // Xóa vị trí công việc
    @PostMapping("/admin/job-positions/{id}/delete")
    public String deleteJobPosition(@PathVariable Integer id) {
        try {
            if (id != null) {
                jobPositionService.deleteJobPosition(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/job-positions";
    }

    // Trang quản lý cấp độ kinh nghiệm
    @GetMapping("/admin/experience-levels")
    public String manageExperienceLevels(@RequestParam(value = "search", required = false) String search, Model model) {
        try {
            List<ExperienceLevel> experienceLevels;
            if (search != null && !search.trim().isEmpty()) {
                experienceLevels = experienceLevelService.getExperienceLevelsBySearch(search);
            } else {
                experienceLevels = experienceLevelService.getAllExperienceLevels();
            }

            model.addAttribute("experienceLevels", experienceLevels);
            model.addAttribute("title", "Quản lý cấp độ kinh nghiệm");
            model.addAttribute("searchQuery", search != null ? search : "");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi tải danh sách cấp độ kinh nghiệm");
        }
        return "admin/experience-levels";
    }

    // Tạo cấp độ kinh nghiệm mới
    @PostMapping("/admin/experience-levels/create")
    public String createExperienceLevel(@RequestParam String tenCapDo) {
        try {
            if (tenCapDo != null && !tenCapDo.trim().isEmpty()) {
                ExperienceLevel experienceLevel = new ExperienceLevel(tenCapDo.trim());
                experienceLevelService.saveExperienceLevel(experienceLevel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/experience-levels";
    }

    // Cập nhật cấp độ kinh nghiệm
    @PostMapping("/admin/experience-levels/{id}/update")
    public String updateExperienceLevel(@PathVariable Integer id, @RequestParam String tenCapDo) {
        try {
            if (id != null && tenCapDo != null && !tenCapDo.trim().isEmpty()) {
                experienceLevelService.getExperienceLevelById(id).ifPresent(experienceLevel -> {
                    experienceLevel.setTenCapDo(tenCapDo.trim());
                    experienceLevelService.updateExperienceLevel(experienceLevel);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/experience-levels";
    }

    // Xóa cấp độ kinh nghiệm
    @PostMapping("/admin/experience-levels/{id}/delete")
    public String deleteExperienceLevel(@PathVariable Integer id) {
        try {
            if (id != null) {
                experienceLevelService.deleteExperienceLevel(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/experience-levels";
    }
}