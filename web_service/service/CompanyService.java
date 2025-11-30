package com.example.demo.service;

import com.example.demo.entity.Company;
import com.example.demo.entity.User;
import com.example.demo.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobDetailService jobDetailService;

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public List<Company> getCompaniesByDaXacThuc(Boolean daXacThuc) {
        return companyRepository.findByDaXacThuc(daXacThuc);
    }

    public List<Company> getUnverifiedCompanies() {
        // Companies with daXacThuc = false (chưa xác thực)
        return companyRepository.findByDaXacThuc(false);
    }

    public List<Company> getVerifiedCompanies() {
        return companyRepository.findByDaXacThuc(true);
    }

    public List<Company> getRejectedCompanies() {
        // For now, we'll consider all unverified companies as potentially "rejected"
        return companyRepository.findByDaXacThuc(false);
    }

    public Page<Company> getCompaniesWithPagination(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

    public Page<Company> getVerifiedCompaniesWithPagination(Pageable pageable) {
        return companyRepository.findByDaXacThuc(true, pageable);
    }

    public Optional<Company> getCompanyById(Integer id) {
        return companyRepository.findById(id);
    }

    public Optional<Company> getCompanyByUser(User user) {
        return companyRepository.findByUser(user);
    }

    public Company saveCompany(Company company) {
        return companyRepository.save(company);
    }

    public Company updateCompany(Company company) {
        return companyRepository.save(company);
    }

    public void deleteCompany(Integer id) {
        companyRepository.deleteById(id);
    }

    public boolean existsByTenCongTy(String tenCongTy) {
        return companyRepository.existsByTenCongTy(tenCongTy);
    }

    public Company registerCompany(User user, String tenCongTy, String tenNguoiDaiDien, String maSoThue, String diaChi, String lienHeCty) {
        if (companyRepository.existsByTenCongTy(tenCongTy)) {
            throw new RuntimeException("Tên công ty đã tồn tại");
        }

        Company company = new Company(user, tenCongTy);
        company.setTenNguoiDaiDien(tenNguoiDaiDien);
        company.setMaSoThue(maSoThue);
        company.setDiaChi(diaChi);
        company.setLienHeCty(lienHeCty);
        company.setDaXacThuc(false); // Chưa xác thực khi mới đăng ký

        return saveCompany(company);
    }

    public Company approveCompany(Integer companyId) {
        Optional<Company> optionalCompany = companyRepository.findById(companyId);
        if (optionalCompany.isPresent()) {
            Company company = optionalCompany.get();
            company.setDaXacThuc(true);
            return companyRepository.save(company);
        }
        throw new RuntimeException("Company not found with id: " + companyId);
    }

    public Company rejectCompany(Integer companyId) {
        Optional<Company> optionalCompany = companyRepository.findById(companyId);
        if (optionalCompany.isPresent()) {
            Company company = optionalCompany.get();
            // For rejection, we keep daXacThuc as false
            company.setDaXacThuc(false);
            return companyRepository.save(company);
        }
        throw new RuntimeException("Company not found with id: " + companyId);
    }

    // --- New Method: Upload and Save Company Logo ---
    public Company updateCompanyLogo(Integer companyId, MultipartFile logoFile) throws IOException {
        Optional<Company> companyOpt = companyRepository.findById(companyId);
        if (!companyOpt.isPresent()) {
             throw new RuntimeException("Company not found with id: " + companyId);
        }

        Company company = companyOpt.get();

        if (logoFile != null && !logoFile.isEmpty()) {
            // Xác định thư mục lưu trữ logo - sử dụng thư mục uploads ở cấp dự án
            // Điều này giúp tránh vấn đề với thư mục classpath khi build
            String uploadDir = "uploads/companies/";
            String uploadPath = System.getProperty("user.dir") + "/" + uploadDir;

            // Tạo tên file mới để tránh trùng lặp, ví dụ: companyId_originalFilename
            String originalFilename = logoFile.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            String newFilename = "logo_" + companyId + extension;

            // Đường dẫn đầy đủ để lưu file
            java.nio.file.Path uploadPathObj = java.nio.file.Paths.get(uploadPath).normalize();
            // Đảm bảo thư mục tồn tại
            if (!java.nio.file.Files.exists(uploadPathObj)) {
                java.nio.file.Files.createDirectories(uploadPathObj);
            }

            java.nio.file.Path filePath = uploadPathObj.resolve(newFilename);

            // Copy file vào thư mục
            try (InputStream inputStream = logoFile.getInputStream()) {
                java.nio.file.Files.copy(inputStream, filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }

            // Cập nhật đường dẫn logo trong entity
            String logoUrl = "/uploads/companies/" + newFilename; // URL để truy cập từ web
            company.setHinhAnhCty(logoUrl);

            // Lưu lại vào DB
            return companyRepository.save(company);
        } else {
            // Nếu không có file mới, không làm gì cả
            return company;
        }
    }

    public List<Company> getFeaturedCompanies() {
        // Lấy các công ty đã xác thực
        List<Company> verifiedCompanies = companyRepository.findByDaXacThuc(true);

        // Sắp xếp theo số lượng công việc giảm dần
        return verifiedCompanies.stream()
            .sorted((comp1, comp2) -> {
                long jobCount1 = jobDetailService.getJobsByCompany(comp1).size();
                long jobCount2 = jobDetailService.getJobsByCompany(comp2).size();
                return Long.compare(jobCount2, jobCount1); // Sắp xếp giảm dần
            })
            .limit(10) // Giới hạn lại 10 công ty nổi bật
            .toList();
    }
}