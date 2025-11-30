package com.example.demo.controller;

import com.example.demo.entity.Company;
import com.example.demo.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CompanyDetailController {

    @Autowired
    private CompanyService companyService;

    // Trang chi tiết công ty - không yêu cầu đăng nhập
    @GetMapping("/company/{id}")
    public String companyDetail(@PathVariable Integer id, Model model) {
        Company company = companyService.getCompanyById(id)
            .orElse(null);
        
        if (company == null || !company.getDaXacThuc()) {
            // Nếu công ty không tồn tại hoặc chưa được xác thực, chuyển hướng về trang danh sách
            return "redirect:/companies";
        }
        
        model.addAttribute("company", company);
        model.addAttribute("title", company.getTenCongTy() + " - Chi tiết công ty");
        
        return "public/company-detail";
    }
}