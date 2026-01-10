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
import org.springframework.http.HttpStatus;
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
        // Chuyển đổi danh sách công ty để bao gồm tọa độ
        List<Map<String, Object>> companyList = companies.stream().map(company -> {
            Map<String, Object> companyMap = new HashMap<>();
            companyMap.put("maCongTy", company.getMaCongTy());
            companyMap.put("tenCongTy", company.getTenCongTy());
            companyMap.put("tenNguoiDaiDien", company.getTenNguoiDaiDien());
            companyMap.put("maSoThue", company.getMaSoThue());
            companyMap.put("diaChi", company.getDiaChi());
            companyMap.put("emailCty", company.getEmailCty());
            companyMap.put("soDienThoaiCty", company.getSoDienThoaiCty());
            companyMap.put("hinhAnhCty", company.getHinhAnhCty());
            companyMap.put("daXacThuc", company.getDaXacThuc());
            companyMap.put("trangThai", company.getTrangThai());
            companyMap.put("ngayTao", company.getNgayTao());
            companyMap.put("moTaCongTy", company.getMoTaCongTy());
            // Thêm tọa độ công ty
            companyMap.put("kinhDo", company.getKinhDo());
            companyMap.put("viDo", company.getViDo());
            return companyMap;
        }).toList();
        return ApiResponseUtil.success("Companies retrieved successfully", companyList);
    }

    @GetMapping("/verified")
    public ResponseEntity<?> getVerifiedCompanies() {
        List<Company> companies = companyService.getVerifiedCompanies();
        // Chuyển đổi danh sách công ty để bao gồm tọa độ
        List<Map<String, Object>> companyList = companies.stream().map(company -> {
            Map<String, Object> companyMap = new HashMap<>();
            companyMap.put("maCongTy", company.getMaCongTy());
            companyMap.put("tenCongTy", company.getTenCongTy());
            companyMap.put("tenNguoiDaiDien", company.getTenNguoiDaiDien());
            companyMap.put("maSoThue", company.getMaSoThue());
            companyMap.put("diaChi", company.getDiaChi());
            companyMap.put("emailCty", company.getEmailCty());
            companyMap.put("soDienThoaiCty", company.getSoDienThoaiCty());
            companyMap.put("hinhAnhCty", company.getHinhAnhCty());
            companyMap.put("daXacThuc", company.getDaXacThuc());
            companyMap.put("trangThai", company.getTrangThai());
            companyMap.put("ngayTao", company.getNgayTao());
            companyMap.put("moTaCongTy", company.getMoTaCongTy());
            // Thêm tọa độ công ty
            companyMap.put("kinhDo", company.getKinhDo());
            companyMap.put("viDo", company.getViDo());
            return companyMap;
        }).toList();
        return ApiResponseUtil.success("Verified companies retrieved successfully", companyList);
    }

    @GetMapping("/featured")
    public ResponseEntity<?> getFeaturedCompanies() {
        // Lấy các công ty nổi bật - các công ty đã xác thực và có nhiều việc làm
        List<Company> featuredCompanies = companyService.getFeaturedCompanies();
        // Chuyển đổi danh sách công ty để bao gồm tọa độ
        List<Map<String, Object>> companyList = featuredCompanies.stream().map(company -> {
            Map<String, Object> companyMap = new HashMap<>();
            companyMap.put("maCongTy", company.getMaCongTy());
            companyMap.put("tenCongTy", company.getTenCongTy());
            companyMap.put("tenNguoiDaiDien", company.getTenNguoiDaiDien());
            companyMap.put("maSoThue", company.getMaSoThue());
            companyMap.put("diaChi", company.getDiaChi());
            companyMap.put("emailCty", company.getEmailCty());
            companyMap.put("soDienThoaiCty", company.getSoDienThoaiCty());
            companyMap.put("hinhAnhCty", company.getHinhAnhCty());
            companyMap.put("daXacThuc", company.getDaXacThuc());
            companyMap.put("trangThai", company.getTrangThai());
            companyMap.put("ngayTao", company.getNgayTao());
            companyMap.put("moTaCongTy", company.getMoTaCongTy());
            // Thêm tọa độ công ty
            companyMap.put("kinhDo", company.getKinhDo());
            companyMap.put("viDo", company.getViDo());
            return companyMap;
        }).toList();
        return ApiResponseUtil.success("Featured companies retrieved successfully", companyList);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCompanies(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ApiResponseUtil.error("Keyword parameter is required");
        }
        List<Company> companies = companyService.getAllCompanies().stream()
            .filter(company -> company.getTenCongTy().toLowerCase().contains(keyword.toLowerCase()))
            .toList();
        // Chuyển đổi danh sách công ty để bao gồm tọa độ
        List<Map<String, Object>> companyList = companies.stream().map(company -> {
            Map<String, Object> companyMap = new HashMap<>();
            companyMap.put("maCongTy", company.getMaCongTy());
            companyMap.put("tenCongTy", company.getTenCongTy());
            companyMap.put("tenNguoiDaiDien", company.getTenNguoiDaiDien());
            companyMap.put("maSoThue", company.getMaSoThue());
            companyMap.put("diaChi", company.getDiaChi());
            companyMap.put("emailCty", company.getEmailCty());
            companyMap.put("soDienThoaiCty", company.getSoDienThoaiCty());
            companyMap.put("hinhAnhCty", company.getHinhAnhCty());
            companyMap.put("daXacThuc", company.getDaXacThuc());
            companyMap.put("trangThai", company.getTrangThai());
            companyMap.put("ngayTao", company.getNgayTao());
            companyMap.put("moTaCongTy", company.getMoTaCongTy());
            // Thêm tọa độ công ty
            companyMap.put("kinhDo", company.getKinhDo());
            companyMap.put("viDo", company.getViDo());
            return companyMap;
        }).toList();
        return ApiResponseUtil.success("Companies searched successfully", companyList);
    }

    @GetMapping("/page")
    public ResponseEntity<?> getCompaniesWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "maCongTy") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Company> companyPage = companyService.getCompaniesWithPagination(pageable);

        // Chuyển đổi danh sách công ty để bao gồm tọa độ
        List<Map<String, Object>> companyList = companyPage.getContent().stream().map(company -> {
            Map<String, Object> companyMap = new HashMap<>();
            companyMap.put("maCongTy", company.getMaCongTy());
            companyMap.put("tenCongTy", company.getTenCongTy());
            companyMap.put("tenNguoiDaiDien", company.getTenNguoiDaiDien());
            companyMap.put("maSoThue", company.getMaSoThue());
            companyMap.put("diaChi", company.getDiaChi());
            companyMap.put("emailCty", company.getEmailCty());
            companyMap.put("soDienThoaiCty", company.getSoDienThoaiCty());
            companyMap.put("hinhAnhCty", company.getHinhAnhCty());
            companyMap.put("daXacThuc", company.getDaXacThuc());
            companyMap.put("trangThai", company.getTrangThai());
            companyMap.put("ngayTao", company.getNgayTao());
            companyMap.put("moTaCongTy", company.getMoTaCongTy());
            // Thêm tọa độ công ty
            companyMap.put("kinhDo", company.getKinhDo());
            companyMap.put("viDo", company.getViDo());
            return companyMap;
        }).toList();

        // Tạo một bản sao của Page với danh sách đã chuyển đổi
        Map<String, Object> response = new HashMap<>();
        response.put("content", companyList);
        response.put("pageable", companyPage.getPageable());
        response.put("totalElements", companyPage.getTotalElements());
        response.put("totalPages", companyPage.getTotalPages());
        response.put("last", companyPage.isLast());
        response.put("first", companyPage.isFirst());
        response.put("number", companyPage.getNumber());
        response.put("size", companyPage.getSize());
        response.put("numberOfElements", companyPage.getNumberOfElements());
        response.put("empty", companyPage.isEmpty());

        return ApiResponseUtil.success("Companies retrieved successfully with pagination", response);
    }

    @GetMapping("/verified/page")
    public ResponseEntity<?> getVerifiedCompaniesWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "maCongTy") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Company> companyPage = companyService.getVerifiedCompaniesWithPagination(pageable);

        // Chuyển đổi danh sách công ty để bao gồm tọa độ
        List<Map<String, Object>> companyList = companyPage.getContent().stream().map(company -> {
            Map<String, Object> companyMap = new HashMap<>();
            companyMap.put("maCongTy", company.getMaCongTy());
            companyMap.put("tenCongTy", company.getTenCongTy());
            companyMap.put("tenNguoiDaiDien", company.getTenNguoiDaiDien());
            companyMap.put("maSoThue", company.getMaSoThue());
            companyMap.put("diaChi", company.getDiaChi());
            companyMap.put("emailCty", company.getEmailCty());
            companyMap.put("soDienThoaiCty", company.getSoDienThoaiCty());
            companyMap.put("hinhAnhCty", company.getHinhAnhCty());
            companyMap.put("daXacThuc", company.getDaXacThuc());
            companyMap.put("trangThai", company.getTrangThai());
            companyMap.put("ngayTao", company.getNgayTao());
            companyMap.put("moTaCongTy", company.getMoTaCongTy());
            // Thêm tọa độ công ty
            companyMap.put("kinhDo", company.getKinhDo());
            companyMap.put("viDo", company.getViDo());
            return companyMap;
        }).toList();

        // Tạo một bản sao của Page với danh sách đã chuyển đổi
        Map<String, Object> response = new HashMap<>();
        response.put("content", companyList);
        response.put("pageable", companyPage.getPageable());
        response.put("totalElements", companyPage.getTotalElements());
        response.put("totalPages", companyPage.getTotalPages());
        response.put("last", companyPage.isLast());
        response.put("first", companyPage.isFirst());
        response.put("number", companyPage.getNumber());
        response.put("size", companyPage.getSize());
        response.put("numberOfElements", companyPage.getNumberOfElements());
        response.put("empty", companyPage.isEmpty());

        return ApiResponseUtil.success("Verified companies retrieved successfully with pagination", response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCompanyById(@PathVariable Integer id) {
        Optional<Company> companyOpt = companyService.getCompanyById(id);
        if (companyOpt.isPresent()) {
            Company company = companyOpt.get();
            // Tạo một map để trả về thông tin công ty với tọa độ
            Map<String, Object> companyInfo = new HashMap<>();
            companyInfo.put("maCongTy", company.getMaCongTy());
            companyInfo.put("tenCongTy", company.getTenCongTy());
            companyInfo.put("tenNguoiDaiDien", company.getTenNguoiDaiDien());
            companyInfo.put("maSoThue", company.getMaSoThue());
            companyInfo.put("diaChi", company.getDiaChi());
            companyInfo.put("emailCty", company.getEmailCty());
            companyInfo.put("soDienThoaiCty", company.getSoDienThoaiCty());
            companyInfo.put("hinhAnhCty", company.getHinhAnhCty());
            companyInfo.put("daXacThuc", company.getDaXacThuc());
            companyInfo.put("trangThai", company.getTrangThai());
            companyInfo.put("ngayTao", company.getNgayTao());
            companyInfo.put("moTaCongTy", company.getMoTaCongTy());
            // Thêm tọa độ công ty
            companyInfo.put("kinhDo", company.getKinhDo());
            companyInfo.put("viDo", company.getViDo());

            return ApiResponseUtil.success("Company retrieved successfully", companyInfo);
        } else {
            return ApiResponseUtil.error("Company not found with id: " + id);
        }
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
            // Thêm thông tin tọa độ cho công việc
            jobMap.put("kinhDo", job.getKinhDo());
            jobMap.put("viDo", job.getViDo());
            // Don't include the company reference to avoid circular reference
            jobMap.put("workField", job.getWorkField());
            jobMap.put("workType", job.getWorkType());
            // Thêm các trường mới: jobPosition và experienceLevel
            if (job.getJobPosition() != null) {
                Map<String, Object> jobPositionInfo = new HashMap<>();
                jobPositionInfo.put("maViTri", job.getJobPosition().getMaViTri());
                jobPositionInfo.put("tenViTri", job.getJobPosition().getTenViTri());
                jobPositionInfo.put("workDiscipline", job.getJobPosition().getWorkDiscipline());
                jobMap.put("jobPosition", jobPositionInfo);
            } else {
                jobMap.put("jobPosition", null);
            }
            if (job.getExperienceLevel() != null) {
                Map<String, Object> experienceLevelInfo = new HashMap<>();
                experienceLevelInfo.put("maCapDo", job.getExperienceLevel().getMaCapDo());
                experienceLevelInfo.put("tenCapDo", job.getExperienceLevel().getTenCapDo());
                jobMap.put("experienceLevel", experienceLevelInfo);
            } else {
                jobMap.put("experienceLevel", null);
            }
            return jobMap;
        }).toList();

        // Create a custom response that includes both company info and simplified jobs
        Map<String, Object> response = new HashMap<>();
        // Include company info with coordinates
        Map<String, Object> companyInfo = new HashMap<>();
        companyInfo.put("maCongTy", company.getMaCongTy());
        companyInfo.put("tenCongTy", company.getTenCongTy());
        companyInfo.put("tenNguoiDaiDien", company.getTenNguoiDaiDien());
        companyInfo.put("maSoThue", company.getMaSoThue());
        companyInfo.put("diaChi", company.getDiaChi());
        companyInfo.put("emailCty", company.getEmailCty());
        companyInfo.put("hinhAnhCty", company.getHinhAnhCty());
        companyInfo.put("daXacThuc", company.getDaXacThuc());
        companyInfo.put("trangThai", company.getTrangThai());
        companyInfo.put("ngayTao", company.getNgayTao());
        companyInfo.put("moTaCongTy", company.getMoTaCongTy());
        // Thêm tọa độ công ty
        companyInfo.put("kinhDo", company.getKinhDo());
        companyInfo.put("viDo", company.getViDo());

        response.put("company", companyInfo);
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