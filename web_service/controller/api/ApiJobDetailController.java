package com.example.demo.controller.api;

import com.example.demo.entity.Company;
import com.example.demo.entity.JobDetail;
import com.example.demo.service.JobDetailService;
import com.example.demo.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/job-details")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiJobDetailController {

    @Autowired
    private JobDetailService jobDetailService;

    @GetMapping
    public ResponseEntity<?> getAllJobDetails() {
        List<JobDetail> jobDetails = jobDetailService.getAllJobs();
        // Tạo danh sách job đơn giản để tránh circular reference
        List<Map<String, Object>> simplifiedJobs = jobDetails.stream().map(this::convertJobDetailToMap).toList();
        return ApiResponseUtil.success("Job details retrieved successfully", simplifiedJobs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJobDetailById(@PathVariable Integer id) {
        JobDetail jobDetail = jobDetailService.getJobById(id);
        if (jobDetail != null) {
            // Trả về job detail với thông tin công ty đầy đủ (bao gồm logo)
            Map<String, Object> jobDetailMap = convertJobDetailToMap(jobDetail);
            return ApiResponseUtil.success("Job detail retrieved successfully", jobDetailMap);
        } else {
            return ApiResponseUtil.error("Job detail not found with id: " + id);
        }
    }

    @PostMapping
    public ResponseEntity<?> createJobDetail(@RequestBody JobDetail jobDetail) {
        JobDetail savedJobDetail = jobDetailService.saveJob(jobDetail);
        Map<String, Object> jobDetailMap = convertJobDetailToMap(savedJobDetail);
        return ApiResponseUtil.created(jobDetailMap);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJobDetail(@PathVariable Integer id, @RequestBody JobDetail jobDetail) {
        if (jobDetailService.getJobById(id) == null) {
            return ApiResponseUtil.error("Job detail not found with id: " + id);
        }
        jobDetail.setMaCongViec(id);
        JobDetail updatedJobDetail = jobDetailService.updateJob(jobDetail);
        Map<String, Object> jobDetailMap = convertJobDetailToMap(updatedJobDetail);
        return ApiResponseUtil.success("Job detail updated successfully", jobDetailMap);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJobDetail(@PathVariable Integer id) {
        if (jobDetailService.getJobById(id) == null) {
            return ApiResponseUtil.error("Job detail not found with id: " + id);
        }
        jobDetailService.deleteJob(id);
        return ApiResponseUtil.noContent();
    }

    @GetMapping("/featured")
    public ResponseEntity<?> getFeaturedJobs() {
        // Lấy các công việc nổi bật - những công việc được duyệt, còn hiệu lực và có nhiều lượt xem
        List<JobDetail> featuredJobs = jobDetailService.getFeaturedJobs();
        // Tạo danh sách job đơn giản để tránh circular reference
        List<Map<String, Object>> simplifiedJobs = featuredJobs.stream().map(this::convertJobDetailToMap).toList();
        return ApiResponseUtil.success("Featured jobs retrieved successfully", simplifiedJobs);
    }

    // Helper method để chuyển đổi JobDetail thành Map để tránh circular reference
    private Map<String, Object> convertJobDetailToMap(JobDetail job) {
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
        // Thêm thông tin công ty (bao gồm logo) vào
        if (job.getCompany() != null) {
            Company company = job.getCompany();
            Map<String, Object> companyInfo = new HashMap<>();
            companyInfo.put("maCongTy", company.getMaCongTy());
            companyInfo.put("tenCongTy", company.getTenCongTy());
            companyInfo.put("diaChi", company.getDiaChi());
            companyInfo.put("lienHeCty", company.getLienHeCty());
            companyInfo.put("hinhAnhCty", company.getHinhAnhCty()); // Đây là trường chứa logo công ty
            companyInfo.put("daXacThuc", company.getDaXacThuc());
            jobMap.put("company", companyInfo);
        }
        jobMap.put("workField", job.getWorkField());
        jobMap.put("workType", job.getWorkType());
        return jobMap;
    }
}