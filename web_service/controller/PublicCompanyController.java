package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicCompanyController {

    // Trang danh sách công ty công khai - không yêu cầu đăng nhập
    @GetMapping("/companies")
    public String listCompanies(Model model) {
        model.addAttribute("title", "Danh sách công ty");
        // Data sẽ được load bằng AJAX từ API
        return "public/companies";
    }
}
