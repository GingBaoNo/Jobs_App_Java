package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmployeeController {
    
    // Trang dashboard cho người xin việc
    @GetMapping("/employee/profile")
    public String employeeProfile(Model model) {
        model.addAttribute("title", "Hồ sơ người xin việc");
        return "employee/profile";
    }
    
    // Trang quản lý hồ sơ cá nhân
    @GetMapping("/employee/my-profile")
    public String myProfile(Model model) {
        model.addAttribute("title", "Thông tin cá nhân");
        return "employee/my-profile";
    }
    
    // Trang việc làm đã ứng tuyển
    @GetMapping("/employee/applied-jobs")
    public String appliedJobs(Model model) {
        model.addAttribute("title", "Việc làm đã ứng tuyển");
        return "employee/applied-jobs";
    }
    
    // Trang việc làm đã lưu
    @GetMapping("/employee/saved-jobs")
    public String savedJobs(Model model) {
        model.addAttribute("title", "Việc làm đã lưu");
        return "employee/saved-jobs";
    }
}