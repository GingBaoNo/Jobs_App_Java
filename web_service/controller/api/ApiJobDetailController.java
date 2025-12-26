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
import java.util.stream.Collectors;

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
        List<Map<String, Object>> simplifiedJobs = jobDetails.stream().map(this::convertJobDetailToMap).collect(Collectors.toList());
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
        List<Map<String, Object>> simplifiedJobs = featuredJobs.stream().map(this::convertJobDetailToMap).collect(Collectors.toList());
        return ApiResponseUtil.success("Featured jobs retrieved successfully", simplifiedJobs);
    }

    // Endpoint tìm kiếm việc làm
    @GetMapping("/search")
    public ResponseEntity<?> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer workField,
            @RequestParam(required = false) Integer workType,
            @RequestParam(required = false) Integer minSalary,
            @RequestParam(required = false) Integer maxSalary) {

        // Logging để debug
        System.out.println("API searchJobs called with parameters:");
        System.out.println("Keyword: " + keyword);
        System.out.println("WorkField: " + workField);
        System.out.println("WorkType: " + workType);
        System.out.println("MinSalary: " + minSalary);
        System.out.println("MaxSalary: " + maxSalary);

        List<JobDetail> jobs = jobDetailService.searchJobs(keyword, workField, workType, minSalary, maxSalary);

        System.out.println("Number of jobs found: " + jobs.size());

        List<Map<String, Object>> simplifiedJobs = jobs.stream().map(this::convertJobDetailToMap).collect(Collectors.toList());
        return ApiResponseUtil.success("Jobs searched successfully", simplifiedJobs);
    }

    // Endpoint tìm kiếm việc làm mở rộng (không áp dụng điều kiện trạng thái nghiêm ngặt)
    @GetMapping("/search-open")
    public ResponseEntity<?> searchJobsOpen(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer workField,
            @RequestParam(required = false) Integer workType,
            @RequestParam(required = false) Integer minSalary,
            @RequestParam(required = false) Integer maxSalary) {

        // Logging để debug
        System.out.println("API searchJobsOpen called with parameters:");
        System.out.println("Keyword: " + keyword);
        System.out.println("WorkField: " + workField);
        System.out.println("WorkType: " + workType);
        System.out.println("MinSalary: " + minSalary);
        System.out.println("MaxSalary: " + maxSalary);

        List<JobDetail> jobs = jobDetailService.searchJobsOpen(keyword, workField, workType, minSalary, maxSalary);

        System.out.println("Number of jobs found in open search: " + jobs.size());

        List<Map<String, Object>> simplifiedJobs = jobs.stream().map(this::convertJobDetailToMap).collect(Collectors.toList());
        return ApiResponseUtil.success("Jobs searched successfully (open search)", simplifiedJobs);
    }

    // Endpoint tìm kiếm việc làm không áp dụng điều kiện trạng thái (tìm kiếm toàn diện)
    @GetMapping("/search-no-status")
    public ResponseEntity<?> searchJobsNoStatus(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer workField,
            @RequestParam(required = false) Integer workType,
            @RequestParam(required = false) Integer minSalary,
            @RequestParam(required = false) Integer maxSalary) {

        List<JobDetail> jobs = jobDetailService.searchJobsNoStatus(keyword, workField, workType, minSalary, maxSalary);
        List<Map<String, Object>> simplifiedJobs = jobs.stream().map(this::convertJobDetailToMap).collect(Collectors.toList());
        return ApiResponseUtil.success("Jobs searched successfully (no status filter)", simplifiedJobs);
    }

    // Endpoint tìm kiếm việc làm với phân trang
    @GetMapping("/search-with-paging")
    public ResponseEntity<?> searchJobsWithPaging(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer workField,
            @RequestParam(required = false) Integer workType,
            @RequestParam(required = false) Integer minSalary,
            @RequestParam(required = false) Integer maxSalary,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "maCongViec") String sortBy) {

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(sortBy));
        org.springframework.data.domain.Page<JobDetail> jobPage = jobDetailService.searchJobsWithPaging(keyword, workField, workType, minSalary, maxSalary, pageable);

        List<Map<String, Object>> simplifiedJobs = jobPage.getContent().stream().map(this::convertJobDetailToMap).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", simplifiedJobs);
        response.put("page", jobPage.getNumber());
        response.put("size", jobPage.getSize());
        response.put("totalElements", jobPage.getTotalElements());
        response.put("totalPages", jobPage.getTotalPages());
        response.put("first", jobPage.isFirst());
        response.put("last", jobPage.isLast());

        return ApiResponseUtil.success("Jobs searched successfully with pagination", response);
    }

    // Endpoint tìm kiếm việc làm đơn lẻ theo ngành nghề hoặc hình thức
    @GetMapping("/search-single")
    public ResponseEntity<?> searchJobsSingle(
            @RequestParam(required = false) Integer workField,
            @RequestParam(required = false) Integer workType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "maCongViec") String sortBy) {

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(sortBy));
        org.springframework.data.domain.Page<JobDetail> jobPage = jobDetailService.searchJobsBySingleCriteriaWithPaging(workField, workType, pageable);

        List<Map<String, Object>> simplifiedJobs = jobPage.getContent().stream().map(this::convertJobDetailToMap).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", simplifiedJobs);
        response.put("page", jobPage.getNumber());
        response.put("size", jobPage.getSize());
        response.put("totalElements", jobPage.getTotalElements());
        response.put("totalPages", jobPage.getTotalPages());
        response.put("first", jobPage.isFirst());
        response.put("last", jobPage.isLast());

        return ApiResponseUtil.success("Jobs searched successfully with single criteria", response);
    }

    // Endpoint tìm kiếm việc làm kết hợp theo ngành nghề và hình thức (không áp dụng điều kiện trạng thái)
    @GetMapping("/search-combined-correct")
    public ResponseEntity<?> searchJobsCombinedCorrect(
            @RequestParam(required = false) Integer workField,
            @RequestParam(required = false) Integer workType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "maCongViec") String sortBy) {

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(sortBy));
        org.springframework.data.domain.Page<JobDetail> jobPage = jobDetailService.searchJobsByCombinedCriteriaWithPaging(workField, workType, pageable);

        List<Map<String, Object>> simplifiedJobs = jobPage.getContent().stream().map(this::convertJobDetailToMap).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", simplifiedJobs);
        response.put("page", jobPage.getNumber());
        response.put("size", jobPage.getSize());
        response.put("totalElements", jobPage.getTotalElements());
        response.put("totalPages", jobPage.getTotalPages());
        response.put("first", jobPage.isFirst());
        response.put("last", jobPage.isLast());

        return ApiResponseUtil.success("Jobs searched successfully with combined criteria", response);
    }

    // Endpoint tìm kiếm việc làm theo tiêu đề (cho Android API)
    @GetMapping("/search-by-title")
    public ResponseEntity<?> searchJobsByTitle(@RequestParam String title) {
        if (title == null || title.trim().isEmpty()) {
            return ApiResponseUtil.error("Title is required");
        }

        try {
            List<JobDetail> jobs = jobDetailService.searchJobsByTitle(title.trim());
            List<Map<String, Object>> result = jobs.stream().map(this::convertJobDetailToMap).collect(Collectors.toList());
            return ApiResponseUtil.success("Jobs searched successfully by title", result);
        } catch (Exception e) {
            return ApiResponseUtil.error("Error searching jobs by title: " + e.getMessage());
        }
    }

    // Endpoint tìm kiếm việc làm kết hợp theo ngành nghề và hình thức (tên cũ - lỗi chính tả, để tương thích)
    @GetMapping("/search-combined")
    public ResponseEntity<?> searchJobsCombined(
            @RequestParam(required = false) Integer workField,
            @RequestParam(required = false) Integer workType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "maCongViec") String sortBy) {
        return searchJobsCombinedCorrect(workField, workType, page, size, sortBy);
    }

    // Endpoint tìm kiếm việc làm kết hợp theo ngành nghề và hình thức (tên sửa lỗi chính tả)
    @GetMapping("/search-combined-fix")
    public ResponseEntity<?> searchJobsCombinedFix(
            @RequestParam(required = false) Integer workField,
            @RequestParam(required = false) Integer workType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "maCongViec") String sortBy) {
        return searchJobsCombinedCorrect(workField, workType, page, size, sortBy);
    }

    // Endpoint tìm kiếm việc làm theo cấu trúc phân cấp mới (lĩnh vực, ngành, vị trí, cấp độ kinh nghiệm)
    @GetMapping("/search-by-hierarchy")
    public ResponseEntity<?> searchJobsByHierarchy(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer workField,
            @RequestParam(required = false) Integer workDiscipline,
            @RequestParam(required = false) Integer jobPosition,
            @RequestParam(required = false) Integer experienceLevel,
            @RequestParam(required = false) Integer workType) {

        List<JobDetail> jobs = jobDetailService.searchJobsByCombinedCriteriaWithNewHierarchy(
                keyword, workField, workDiscipline, jobPosition, experienceLevel, workType);

        List<Map<String, Object>> simplifiedJobs = jobs.stream().map(this::convertJobDetailToMap).collect(Collectors.toList());
        return ApiResponseUtil.success("Jobs searched successfully by hierarchy", simplifiedJobs);
    }

    // Endpoint tìm kiếm việc làm nâng cao có phân trang
    @GetMapping("/search-advanced")
    public ResponseEntity<?> searchJobsAdvanced(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer workField,
            @RequestParam(required = false) Integer workDiscipline,
            @RequestParam(required = false) Integer jobPosition,
            @RequestParam(required = false) Integer experienceLevel,
            @RequestParam(required = false) Integer workType,
            @RequestParam(required = false) Integer minSalary,
            @RequestParam(required = false) Integer maxSalary,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "maCongViec") String sortBy) {

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(sortBy));
        org.springframework.data.domain.Page<JobDetail> jobPage = jobDetailService.searchJobsAdvancedWithPaging(
                keyword, workField, workDiscipline, jobPosition, experienceLevel, workType, minSalary, maxSalary, pageable);

        List<Map<String, Object>> simplifiedJobs = jobPage.getContent().stream().map(this::convertJobDetailToMap).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", simplifiedJobs);
        response.put("page", jobPage.getNumber());
        response.put("size", jobPage.getSize());
        response.put("totalElements", jobPage.getTotalElements());
        response.put("totalPages", jobPage.getTotalPages());
        response.put("first", jobPage.isFirst());
        response.put("last", jobPage.isLast());

        return ApiResponseUtil.success("Advanced search completed successfully", response);
    }

    // Endpoint tìm kiếm việc làm theo lĩnh vực
    @GetMapping("/by-field/{fieldId}")
    public ResponseEntity<?> searchJobsByField(@PathVariable Integer fieldId) {
        List<JobDetail> jobs = jobDetailService.getJobsByWorkFieldId(fieldId);
        List<Map<String, Object>> simplifiedJobs = jobs.stream().map(this::convertJobDetailToMap).collect(Collectors.toList());
        return ApiResponseUtil.success("Jobs retrieved by field successfully", simplifiedJobs);
    }

    // Endpoint tìm kiếm việc làm theo hình thức
    @GetMapping("/by-type/{typeId}")
    public ResponseEntity<?> searchJobsByType(@PathVariable Integer typeId) {
        List<JobDetail> jobs = jobDetailService.getJobsByWorkTypeId(typeId);
        List<Map<String, Object>> simplifiedJobs = jobs.stream().map(this::convertJobDetailToMap).collect(Collectors.toList());
        return ApiResponseUtil.success("Jobs retrieved by type successfully", simplifiedJobs);
    }

    // Endpoint tìm kiếm việc làm theo vị trí
    @GetMapping("/by-position/{positionId}")
    public ResponseEntity<?> searchJobsByPosition(@PathVariable Integer positionId) {
        List<JobDetail> jobs = jobDetailService.getJobsByJobPositionId(positionId);
        List<Map<String, Object>> simplifiedJobs = jobs.stream().map(this::convertJobDetailToMap).collect(Collectors.toList());
        return ApiResponseUtil.success("Jobs retrieved by position successfully", simplifiedJobs);
    }

    // Endpoint tìm kiếm việc làm theo cấp độ kinh nghiệm
    @GetMapping("/by-experience/{experienceId}")
    public ResponseEntity<?> searchJobsByExperience(@PathVariable Integer experienceId) {
        List<JobDetail> jobs = jobDetailService.getJobsByExperienceLevelId(experienceId);
        List<Map<String, Object>> simplifiedJobs = jobs.stream().map(this::convertJobDetailToMap).collect(Collectors.toList());
        return ApiResponseUtil.success("Jobs retrieved by experience successfully", simplifiedJobs);
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
        jobMap.put("yeuCauCongViec", job.getYeuCauCongViec());
        jobMap.put("quyenLoi", job.getQuyenLoi());
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
            companyInfo.put("moTaCongTy", company.getMoTaCongTy()); // Thêm trường mô tả công ty
            jobMap.put("company", companyInfo);
        }
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
    }
}