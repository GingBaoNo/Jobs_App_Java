package com.example.demo.controller;

import com.example.demo.entity.Company;
import com.example.demo.entity.User;
import com.example.demo.service.CompanyService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class EmployerController {
    
    @Autowired
    private CompanyService companyService;
    
    @Autowired
    private UserService userService;
    
    // Trang dashboard của nhà tuyển dụng
    @GetMapping("/employer/dashboard")
    public String employerDashboard(Authentication authentication, Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("title", "Bảng điều khiển Nhà Tuyển Dụng");
            
            // Lấy thông tin công ty nếu có
            Company company = companyService.getCompanyByUser(user).orElse(null);
            model.addAttribute("company", company);
        }
        return "employer/dashboard";
    }
    
    // Trang xem hồ sơ công ty
    @GetMapping("/employer/company")
    public String viewCompanyProfile(Authentication authentication, Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Company company = companyService.getCompanyByUser(user).orElse(null);
        if (company == null) {
            // Nếu chưa có công ty, có thể chuyển hướng đến một trang khác hoặc hiển thị thông báo
            model.addAttribute("errorMessage", "Bạn chưa đăng ký công ty nào.");
            // Trả về trang có thể có nút để bắt đầu đăng ký, hoặc chuyển hướng
            // Ví dụ: chuyển hướng đến đăng ký nếu chưa có
            // return "redirect:/employer/company/register"; // Nếu có trang riêng cho đăng ký
            // Hoặc trả về trang xem với thông báo
            model.addAttribute("company", new Company()); // Để tránh lỗi nếu template cần
        } else {
            model.addAttribute("company", company);
        }

        model.addAttribute("title", "Hồ sơ công ty");
        return "employer/company-profile";
    }

    // Trang cập nhật thông tin công ty
    @GetMapping("/employer/company/edit")
    public String editCompany(Authentication authentication, Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Company company = companyService.getCompanyByUser(user).orElse(null);
        if (company == null) {
            // Nếu chưa có công ty, không thể cập nhật, có thể chuyển hướng đăng ký
            model.addAttribute("errorMessage", "Bạn chưa đăng ký công ty nào để cập nhật.");
            // Có thể chuyển hướng đến đăng ký hoặc trang xem
            return "redirect:/employer/company"; // Quay lại trang xem
        }

        model.addAttribute("company", company);
        model.addAttribute("title", "Cập nhật thông tin công ty");
        return "employer/company"; // Trang cập nhật
    }
    
    // Xử lý đăng ký công ty
    @PostMapping("/employer/company/register")
    public String registerCompany(Authentication authentication, 
                                 @ModelAttribute Company company, 
                                 Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            // Kiểm tra xem công ty đã tồn tại chưa
            if (companyService.existsByTenCongTy(company.getTenCongTy())) {
                model.addAttribute("errorMessage", "Tên công ty đã tồn tại");
                model.addAttribute("company", company);
                model.addAttribute("isRegistration", true);
                return "employer/company";
            }
            
            // Tạo công ty mới
            Company newCompany = companyService.registerCompany(
                user,
                company.getTenCongTy(),
                company.getTenNguoiDaiDien(),
                company.getMaSoThue(),
                company.getDiaChi(),
                company.getLienHeCty()
            );
            
            model.addAttribute("successMessage", "Đăng ký công ty thành công!");
            model.addAttribute("company", newCompany);
            model.addAttribute("isRegistration", false);
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi đăng ký công ty: " + e.getMessage());
            model.addAttribute("company", company);
            model.addAttribute("isRegistration", true);
            return "employer/company";
        }
        
        return "employer/company";
    }

    // Xử lý upload logo công ty
    @PostMapping("/employer/company/update-logo")
    public String updateCompanyLogo(Authentication authentication,
                                   @RequestParam("logoFile") MultipartFile logoFile,
                                   @RequestParam("companyId") Integer companyId,
                                   Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Company existingCompany = companyService.getCompanyById(companyId).orElse(null);
        if (existingCompany == null || !existingCompany.getUser().getMaNguoiDung().equals(user.getMaNguoiDung())) {
            // Không tìm thấy công ty hoặc công ty không thuộc về người dùng này
            model.addAttribute("errorMessage", "Không thể cập nhật logo cho công ty này.");
            return "employer/company"; // Trở lại trang quản lý công ty
        }

        try {
            companyService.updateCompanyLogo(companyId, logoFile);
            model.addAttribute("successMessage", "Cập nhật logo thành công!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi cập nhật logo: " + e.getMessage());
            e.printStackTrace();
        }

        // Load lại thông tin công ty để hiển thị trên view
        Company updatedCompany = companyService.getCompanyById(companyId).orElse(null);
        model.addAttribute("company", updatedCompany);
        model.addAttribute("isRegistration", false);
        return "employer/company";
    }

    // Xử lý cập nhật thông tin công ty (bao gồm cả logo nếu có)
    @PostMapping("/employer/company/update")
    public String updateCompany(Authentication authentication,
                                @RequestParam("maCongTy") Integer companyId, // Lấy ID từ form
                                @RequestParam("tenCongTy") String tenCongTy,
                                @RequestParam("tenNguoiDaiDien") String tenNguoiDaiDien,
                                @RequestParam("diaChi") String diaChi,
                                @RequestParam("maSoThue") String maSoThue,
                                @RequestParam("lienHeCty") String lienHeCty,
                                @RequestParam(value = "logoFile", required = false) MultipartFile logoFile, // Thêm logo file, có thể không có
                                Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        // Lấy công ty hiện tại của người dùng
        Company existingCompany = companyService.getCompanyByUser(user).orElse(null);
        if (existingCompany == null || !existingCompany.getMaCongTy().equals(companyId)) {
            // Không tìm thấy công ty hoặc công ty không thuộc về người dùng này hoặc ID không khớp
            model.addAttribute("errorMessage", "Không thể cập nhật thông tin công ty này.");
            return "employer/company"; // Trở lại trang cập nhật công ty
        }

        // Cập nhật logo nếu có file mới
        if (logoFile != null && !logoFile.isEmpty()) {
             try {
                // Phương thức updateCompanyLogo đã xử lý việc lưu file và cập nhật hinhAnhCty
                companyService.updateCompanyLogo(companyId, logoFile);
                // load lại company sau khi update logo để có URL mới
                existingCompany = companyService.getCompanyById(companyId).orElse(existingCompany);
                model.addAttribute("successMessage", "Cập nhật thông tin và logo công ty thành công!");
            } catch (Exception e) {
                model.addAttribute("errorMessage", "Lỗi khi cập nhật logo: " + e.getMessage());
                e.printStackTrace();
                // Không return, vẫn cố cập nhật thông tin text nếu logo lỗi
            }
        }

        // Cập nhật các trường thông tin khác
        existingCompany.setTenCongTy(tenCongTy);
        existingCompany.setTenNguoiDaiDien(tenNguoiDaiDien);
        existingCompany.setDiaChi(diaChi);
        existingCompany.setMaSoThue(maSoThue);
        existingCompany.setLienHeCty(lienHeCty);

        // Lưu lại thông tin chung vào DB
        try {
            companyService.updateCompany(existingCompany);
            // Nếu chưa có success message từ logo, gán message cho update text
            if (!model.containsAttribute("successMessage")) {
                model.addAttribute("successMessage", "Cập nhật thông tin công ty thành công!");
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi cập nhật thông tin công ty: " + e.getMessage());
            e.printStackTrace();
        }

        // Load lại thông tin công ty để hiển thị trên view
        model.addAttribute("company", existingCompany);
        // model.addAttribute("isRegistration", false); // Trang company.html nay chỉ là update, không cần biến này nữa nếu template được cập nhật
        return "employer/company";
    }

}