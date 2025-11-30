package com.example.demo.controller.api;

import com.example.demo.entity.Company;
import com.example.demo.entity.JobDetail;
import com.example.demo.service.CompanyService;
import com.example.demo.service.JobDetailService;
import com.example.demo.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/companies")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiCompanyController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private JobDetailService jobDetailService;

    @GetMapping
    public ResponseEntity<?> getAllCompanies() {
        List<Company> companies = companyService.getAllCompanies();
        return ApiResponseUtil.success("Companies retrieved successfully", companies);
    }

    @GetMapping("/verified")
    public ResponseEntity<?> getVerifiedCompanies() {
        List<Company> companies = companyService.getVerifiedCompanies();
        return ApiResponseUtil.success("Verified companies retrieved successfully", companies);
    }

    @GetMapping("/featured")
    public ResponseEntity<?> getFeaturedCompanies() {
        // Lấy các công ty nổi bật - các công ty đã xác thực và có nhiều việc làm
        List<Company> featuredCompanies = companyService.getFeaturedCompanies();
        return ApiResponseUtil.success("Featured companies retrieved successfully", featuredCompanies);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCompanies(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ApiResponseUtil.error("Keyword parameter is required");
        }
        List<Company> companies = companyService.getAllCompanies().stream()
            .filter(company -> company.getTenCongTy().toLowerCase().contains(keyword.toLowerCase()))
            .toList();
        return ApiResponseUtil.success("Companies searched successfully", companies);
    }

    @GetMapping("/page")
    public ResponseEntity<?> getCompaniesWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "maCongTy") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Company> companyPage = companyService.getCompaniesWithPagination(pageable);

        return ApiResponseUtil.success("Companies retrieved successfully with pagination", companyPage);
    }

    @GetMapping("/verified/page")
    public ResponseEntity<?> getVerifiedCompaniesWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "maCongTy") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Company> companyPage = companyService.getVerifiedCompaniesWithPagination(pageable);

        return ApiResponseUtil.success("Verified companies retrieved successfully with pagination", companyPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCompanyById(@PathVariable Integer id) {
        return companyService.getCompanyById(id)
            .map(company -> ApiResponseUtil.success("Company retrieved successfully", company))
            .orElse(ApiResponseUtil.error("Company not found with id: " + id));
    }

    @GetMapping("/{id}/jobs")
    public ResponseEntity<?> getCompanyWithJobs(@PathVariable Integer id) {
        Optional<Company> companyOpt = companyService.getCompanyById(id);
        if (!companyOpt.isPresent()) {
            return ApiResponseUtil.error("Company not found with id: " + id);
        }

        Company company = companyOpt.get();
        List<JobDetail> jobs = jobDetailService.getJobsByCompany(company);

        // Remove company reference from each job to prevent circular reference
        List<Map<String, Object>> simplifiedJobs = jobs.stream().map(job -> {
            Map<String, Object> jobMap = new HashMap<>();
            jobMap.put("maCongViec", job.getMaCongViec());
            jobMap.put("tieuDe", job.getTieuDe());
            jobMap.put("luong", job.getLuong());
            jobMap.put("loaiLuong", job.getLoaiLuong());
            jobMap.put("gioBatDau", job.getGioBatDau());
            jobMap.put("gioKetThuc", job.getGioKetThuc());
            jobMap.put("coTheThuongLuongGio", job.getCoTheThuongLuongGio());
            jobMap.put("gioiTinhYeuCau", job.getGioiTinhYeuCau());
            jobMap.put("soLuongTuyen", job.getSoLuongTuyen());
            jobMap.put("ngayLamViec", job.getNgayLamViec());
            jobMap.put("thoiHanLamViec", job.getThoiHanLamViec());
            jobMap.put("coTheThuongLuongNgay", job.getCoTheThuongLuongNgay());
            jobMap.put("chiTiet", job.getChiTiet());
            jobMap.put("yeuCauCongViec", job.getYeuCauCongViec());  // Thêm trường mới
            jobMap.put("quyenLoi", job.getQuyenLoi());              // Thêm trường mới
            jobMap.put("ngayKetThucTuyenDung", job.getNgayKetThucTuyenDung());
            jobMap.put("ngayDang", job.getNgayDang());
            jobMap.put("luotXem", job.getLuotXem());
            jobMap.put("trangThaiDuyet", job.getTrangThaiDuyet());
            jobMap.put("trangThaiTinTuyen", job.getTrangThaiTinTuyen());
            // Don't include the company reference to avoid circular reference
            jobMap.put("workField", job.getWorkField());
            jobMap.put("workType", job.getWorkType());
            return jobMap;
        }).toList();

        // Create a custom response that includes both company info and simplified jobs
        Map<String, Object> response = new HashMap<>();
        response.put("company", company);
        response.put("jobs", simplifiedJobs);
        response.put("jobCount", simplifiedJobs.size());

        return ApiResponseUtil.success("Company and its jobs retrieved successfully", response);
    }

    @PostMapping
    public ResponseEntity<?> createCompany(@RequestBody Company company) {
        Company savedCompany = companyService.saveCompany(company);
        return ApiResponseUtil.created(savedCompany);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCompany(@PathVariable Integer id, @RequestBody Company company) {
        if (!companyService.getCompanyById(id).isPresent()) {
            return ApiResponseUtil.error("Company not found with id: " + id);
        }
        company.setMaCongTy(id);
        Company updatedCompany = companyService.updateCompany(company);
        return ApiResponseUtil.success("Company updated successfully", updatedCompany);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCompany(@PathVariable Integer id) {
        if (!companyService.getCompanyById(id).isPresent()) {
            return ApiResponseUtil.error("Company not found with id: " + id);
        }
        companyService.deleteCompany(id);
        return ApiResponseUtil.noContent();
    }
}