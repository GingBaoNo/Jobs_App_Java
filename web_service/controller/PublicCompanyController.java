package com.example.demo.controller;

import com.example.demo.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicCompanyController {

    @Autowired
    private CompanyService companyService;

    // Trang danh sách công ty công khai - không yêu cầu đăng nhập
    @GetMapping("/companies")
    public String listCompanies(Model model) {
        model.addAttribute("title", "Danh sách công ty");
        // Chỉ hiển thị các công ty đã xác thực cho tất cả người dùng
        model.addAttribute("companies", companyService.getVerifiedCompanies());
        return "public/companies";
    }
}