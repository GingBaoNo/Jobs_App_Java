package com.example.demo.controller.api;

import com.example.demo.entity.JobDetail;
import com.example.demo.entity.SavedJob;
import com.example.demo.entity.User;
import com.example.demo.service.JobDetailService;
import com.example.demo.service.SavedJobService;
import com.example.demo.service.UserService;
import com.example.demo.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/saved-jobs")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiSavedJobController {

    @Autowired
    private SavedJobService savedJobService;

    @Autowired
    private UserService userService;

    @Autowired
    private JobDetailService jobDetailService;

    // Lưu công việc
    @PostMapping
    public ResponseEntity<?> saveJob(@RequestBody SaveJobRequest request) {
        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponseUtil.error("User not authenticated");
        }

        String username = authentication.getName();
        Optional<User> user = userService.getUserByTaiKhoan(username);
        if (!user.isPresent()) {
            return ApiResponseUtil.error("User not found");
        }

        JobDetail jobDetail = jobDetailService.getJobById(request.getJobDetailId());
        if (jobDetail == null) {
            return ApiResponseUtil.error("Job detail not found with id: " + request.getJobDetailId());
        }

        try {
            SavedJob savedJob = savedJobService.saveJob(user.get(), jobDetail);
            return ApiResponseUtil.created(savedJob);
        } catch (RuntimeException e) {
            return ApiResponseUtil.error(e.getMessage());
        }
    }

    // Lấy các công việc đã lưu của người dùng hiện tại
    @GetMapping("/my-saved-jobs")
    public ResponseEntity<?> getMySavedJobs() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponseUtil.error("User not authenticated");
        }

        String username = authentication.getName();
        Optional<User> user = userService.getUserByTaiKhoan(username);
        if (!user.isPresent()) {
            return ApiResponseUtil.error("User not found");
        }

        List<SavedJob> savedJobs = savedJobService.getSavedJobsByUser(user.get());
        return ApiResponseUtil.success("My saved jobs retrieved successfully", savedJobs);
    }

    // Xóa công việc đã lưu theo công việc (hủy lưu)
    @DeleteMapping
    public ResponseEntity<?> unsaveJob(@RequestBody UnsaveJobRequest request) {
        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponseUtil.error("User not authenticated");
        }

        String username = authentication.getName();
        Optional<User> user = userService.getUserByTaiKhoan(username);
        if (!user.isPresent()) {
            return ApiResponseUtil.error("User not found");
        }

        JobDetail jobDetail = jobDetailService.getJobById(request.getJobDetailId());
        if (jobDetail == null) {
            return ApiResponseUtil.error("Job detail not found with id: " + request.getJobDetailId());
        }

        try {
            savedJobService.removeSavedJob(user.get(), jobDetail);
            return ApiResponseUtil.success("Job removed from saved jobs successfully", null);
        } catch (Exception e) {
            return ApiResponseUtil.error("Error removing saved job: " + e.getMessage());
        }
    }

    // Kiểm tra xem công việc đã được lưu chưa
    @GetMapping("/check/{jobId}")
    public ResponseEntity<?> checkIfJobSaved(@PathVariable Integer jobId) {
        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponseUtil.error("User not authenticated");
        }

        String username = authentication.getName();
        Optional<User> user = userService.getUserByTaiKhoan(username);
        if (!user.isPresent()) {
            return ApiResponseUtil.error("User not found");
        }

        JobDetail jobDetail = jobDetailService.getJobById(jobId);
        if (jobDetail == null) {
            return ApiResponseUtil.error("Job detail not found with id: " + jobId);
        }

        Optional<SavedJob> savedJob = savedJobService.getSavedJobByUserAndJobDetail(user.get(), jobDetail);

        Map<String, Object> response = new HashMap<>();
        response.put("isSaved", savedJob.isPresent());
        response.put("savedJobId", savedJob.map(SavedJob::getMaCvDaLuu).orElse(null));

        return ApiResponseUtil.success("Job saved status checked successfully", response);
    }

    // Request classes
    public static class SaveJobRequest {
        private Integer jobDetailId;

        public Integer getJobDetailId() {
            return jobDetailId;
        }

        public void setJobDetailId(Integer jobDetailId) {
            this.jobDetailId = jobDetailId;
        }
    }

    public static class UnsaveJobRequest {
        private Integer jobDetailId;

        public Integer getJobDetailId() {
            return jobDetailId;
        }

        public void setJobDetailId(Integer jobDetailId) {
            this.jobDetailId = jobDetailId;
        }
    }
}