package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class EmployerJobController {
    
    @Autowired
    private JobDetailService jobDetailService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CompanyService companyService;
    
    @Autowired
    private WorkFieldService workFieldService;
    
    @Autowired
    private WorkTypeService workTypeService;

    @Autowired
    private WorkDisciplineService workDisciplineService;

    @Autowired
    private JobPositionService jobPositionService;

    @Autowired
    private ExperienceLevelService experienceLevelService;

    // Trang tạo công việc mới cho nhà tuyển dụng
    @GetMapping("/employer/job/create")
    public String createJobForm(Authentication authentication, Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Company company = companyService.getCompanyByUser(user).orElse(null);
        if (company == null) {
            model.addAttribute("errorMessage", "Bạn cần đăng ký công ty trước khi tạo tin tuyển dụng.");
            return "redirect:/employer/company";
        }

        model.addAttribute("job", new JobDetail());
        model.addAttribute("workFields", workFieldService.getAllWorkFields());
        model.addAttribute("workTypes", workTypeService.getAllWorkTypes());
        model.addAttribute("workDisciplines", workDisciplineService.getAllWorkDisciplines());
        model.addAttribute("jobPositions", jobPositionService.getAllJobPositions());
        model.addAttribute("experienceLevels", experienceLevelService.getAllExperienceLevels());
        model.addAttribute("title", "Đăng tin tuyển dụng mới");

        return "employer/job-create";
    }
    
    // Xử lý đăng bài (lưu vào DB với trạng thái chờ duyệt)
    @PostMapping("/employer/job/create")
    public String createJob(@ModelAttribute JobDetail job,
                           @RequestParam(required = false) Integer maNganh,
                           @RequestParam(required = false) Integer maViTri,
                           @RequestParam(required = false) Integer maCapDoKinhNghiem,
                           Authentication authentication, Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Company company = companyService.getCompanyByUser(user).orElse(null);
        if (company == null) {
            model.addAttribute("errorMessage", "Bạn cần đăng ký công ty trước khi tạo tin tuyển dụng.");
            return "redirect:/employer/company";
        }

        try {
            // Gán công ty cho công việc
            job.setCompany(company);

            // Gán các trường phân cấp nếu được cung cấp
            if (maNganh != null) {
                WorkDiscipline workDiscipline = workDisciplineService.getWorkDisciplineById(maNganh).orElse(null);
                if (workDiscipline != null) {
                    job.setJobPosition(null); // Clear any existing position that might not match the discipline
                }
            }

            if (maViTri != null) {
                JobPosition jobPosition = jobPositionService.getJobPositionById(maViTri).orElse(null);
                if (jobPosition != null) {
                    job.setJobPosition(jobPosition);
                }
            }

            if (maCapDoKinhNghiem != null) {
                ExperienceLevel experienceLevel = experienceLevelService.getExperienceLevelById(maCapDoKinhNghiem).orElse(null);
                if (experienceLevel != null) {
                    job.setExperienceLevel(experienceLevel);
                }
            }

            // Đặt trạng thái chờ duyệt
            job.setTrangThaiDuyet("Chờ duyệt");
            job.setTrangThaiTinTuyen("Mở");

            // Đặt ngày đăng là ngày hiện tại
            job.setNgayDang(java.time.LocalDateTime.now());

            // Đặt ngày hết hạn nếu chưa có
            if (job.getNgayKetThucTuyenDung() == null) {
                job.setNgayKetThucTuyenDung(LocalDate.now().plusDays(30));
            }

            JobDetail savedJob = jobDetailService.saveJob(job);
            model.addAttribute("successMessage", "Đăng tin tuyển dụng thành công! Vui lòng chờ quản trị viên duyệt.");

            return "redirect:/employer/jobs";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi đăng tin tuyển dụng: " + e.getMessage());
            model.addAttribute("workFields", workFieldService.getAllWorkFields());
            model.addAttribute("workTypes", workTypeService.getAllWorkTypes());
            model.addAttribute("workDisciplines", workDisciplineService.getAllWorkDisciplines());
            model.addAttribute("jobPositions", jobPositionService.getAllJobPositions());
            model.addAttribute("experienceLevels", experienceLevelService.getAllExperienceLevels());
            model.addAttribute("job", job);
            return "employer/job-create";
        }
    }
}