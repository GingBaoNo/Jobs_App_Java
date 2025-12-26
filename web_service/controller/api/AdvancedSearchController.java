package com.example.demo.controller.api;

import com.example.demo.entity.JobDetail;
import com.example.demo.service.JobDetailService;
import com.example.demo.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/advanced-search")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdvancedSearchController {

    @Autowired
    private JobDetailService jobDetailService;

    /**
     * Tìm kiếm nâng cao công việc với nhiều tiêu chí
     * Hỗ trợ cho cả web và mobile app
     */
    @GetMapping("/jobs")
    public ResponseEntity<?> searchJobsAdvanced(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "fieldId", required = false) Integer fieldId,
            @RequestParam(value = "disciplineId", required = false) Integer disciplineId,
            @RequestParam(value = "positionId", required = false) Integer positionId,
            @RequestParam(value = "experienceId", required = false) Integer experienceId,
            @RequestParam(value = "typeId", required = false) Integer typeId,
            @RequestParam(value = "minSalary", required = false) Integer minSalary,
            @RequestParam(value = "maxSalary", required = false) Integer maxSalary,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);

            // Gọi service để tìm kiếm với phân trang
            Page<JobDetail> jobPage = jobDetailService.searchJobsAdvancedWithPaging(
                    keyword, fieldId, disciplineId, positionId, experienceId, typeId,
                    minSalary, maxSalary, pageable);

            // Trả về danh sách công việc phù hợp với bộ lọc
            // Chỉ cần lấy danh sách công việc, không cần thông tin phân trang nếu chỉ muốn danh sách
            List<JobDetail> jobList = jobPage.getContent();

            return ApiResponseUtil.success("Tìm kiếm công việc thành công", jobList);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseUtil.error("Lỗi xảy ra trong quá trình tìm kiếm: " + e.getMessage());
        }
    }

    /**
     * Tìm kiếm nâng cao không phân trang
     */
    @GetMapping("/jobs/all")
    public ResponseEntity<?> searchJobsAdvancedAll(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "fieldId", required = false) Integer fieldId,
            @RequestParam(value = "disciplineId", required = false) Integer disciplineId,
            @RequestParam(value = "positionId", required = false) Integer positionId,
            @RequestParam(value = "experienceId", required = false) Integer experienceId,
            @RequestParam(value = "typeId", required = false) Integer typeId,
            @RequestParam(value = "minSalary", required = false) Integer minSalary,
            @RequestParam(value = "maxSalary", required = false) Integer maxSalary) {

        try {
            List<JobDetail> jobs = jobDetailService.searchJobsByCombinedCriteriaWithNewHierarchy(
                    keyword, fieldId, disciplineId, positionId, experienceId, typeId);

            return ApiResponseUtil.success("Advanced search completed successfully", jobs);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseUtil.error("Error occurred during advanced search: " + e.getMessage());
        }
    }

    /**
     * Tìm kiếm theo ngành nghề
     */
    @GetMapping("/by-field/{fieldId}")
    public ResponseEntity<?> searchJobsByField(@PathVariable Integer fieldId) {
        try {
            List<JobDetail> jobs = jobDetailService.getJobsByWorkFieldId(fieldId);
            return ApiResponseUtil.success("Jobs retrieved by field successfully", jobs);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseUtil.error("Error occurred during search: " + e.getMessage());
        }
    }

    /**
     * Tìm kiếm theo hình thức làm việc
     */
    @GetMapping("/by-type/{typeId}")
    public ResponseEntity<?> searchJobsByType(@PathVariable Integer typeId) {
        try {
            List<JobDetail> jobs = jobDetailService.getJobsByWorkTypeId(typeId);
            return ApiResponseUtil.success("Jobs retrieved by type successfully", jobs);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseUtil.error("Error occurred during search: " + e.getMessage());
        }
    }

    /**
     * Tìm kiếm theo vị trí công việc
     */
    @GetMapping("/by-position/{positionId}")
    public ResponseEntity<?> searchJobsByPosition(@PathVariable Integer positionId) {
        try {
            List<JobDetail> jobs = jobDetailService.getJobsByJobPositionId(positionId);
            return ApiResponseUtil.success("Jobs retrieved by position successfully", jobs);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseUtil.error("Error occurred during search: " + e.getMessage());
        }
    }

    /**
     * Tìm kiếm theo cấp độ kinh nghiệm
     */
    @GetMapping("/by-experience/{experienceId}")
    public ResponseEntity<?> searchJobsByExperience(@PathVariable Integer experienceId) {
        try {
            List<JobDetail> jobs = jobDetailService.getJobsByExperienceLevelId(experienceId);
            return ApiResponseUtil.success("Jobs retrieved by experience successfully", jobs);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseUtil.error("Error occurred during search: " + e.getMessage());
        }
    }
}