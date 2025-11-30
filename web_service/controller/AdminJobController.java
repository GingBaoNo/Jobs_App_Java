package com.example.demo.controller;

import com.example.demo.entity.JobDetail;
import com.example.demo.service.JobDetailService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AdminJobController {
    
    @Autowired
    private JobDetailService jobDetailService;
    
    @Autowired
    private UserService userService;
    
    // Trang quản lý tin tuyển dụng chờ duyệt
    @GetMapping("/admin/jobs/pending")
    public String pendingJobs(Model model) {
        // Lấy các công việc đang chờ duyệt
        List<JobDetail> pendingJobs = jobDetailService.getJobsByTrangThaiDuyet("Chờ duyệt");
        model.addAttribute("jobs", pendingJobs);
        model.addAttribute("title", "Quản lý tin tuyển dụng chờ duyệt");
        return "admin/jobs-pending";
    }
    
    // Duyệt tin tuyển dụng
    @PostMapping("/admin/job/{id}/approve")
    public String approveJob(@PathVariable Integer id, Model model) {
        JobDetail job = jobDetailService.getJobById(id);
        if (job != null) {
            job.setTrangThaiDuyet("Đã duyệt");
            jobDetailService.updateJob(job);
            model.addAttribute("successMessage", "Duyệt tin tuyển dụng thành công!");
        } else {
            model.addAttribute("errorMessage", "Không tìm thấy tin tuyển dụng!");
        }
        return "redirect:/admin/jobs/pending";
    }
    
    // Từ chối tin tuyển dụng
    @PostMapping("/admin/job/{id}/reject")
    public String rejectJob(@PathVariable Integer id, Model model) {
        JobDetail job = jobDetailService.getJobById(id);
        if (job != null) {
            job.setTrangThaiDuyet("Từ chối");
            jobDetailService.updateJob(job);
            model.addAttribute("successMessage", "Từ chối tin tuyển dụng thành công!");
        } else {
            model.addAttribute("errorMessage", "Không tìm thấy tin tuyển dụng!");
        }
        return "redirect:/admin/jobs/pending";
    }
    
    // Trang quản lý tất cả tin tuyển dụng
    @GetMapping("/admin/jobs")
    public String allJobs(Model model) {
        List<JobDetail> allJobs = jobDetailService.getAllJobs();
        model.addAttribute("jobs", allJobs);
        model.addAttribute("title", "Quản lý tất cả tin tuyển dụng");
        return "admin/jobs-all";
    }
}