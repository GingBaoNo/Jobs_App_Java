package com.example.demo.controller.api;

import com.example.demo.entity.AppliedJob;
import com.example.demo.entity.JobDetail;
import com.example.demo.entity.User;
import com.example.demo.service.AppliedJobService;
import com.example.demo.service.JobDetailService;
import com.example.demo.service.UserService;
import com.example.demo.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/applied-jobs")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiAppliedJobController {

    @Autowired
    private AppliedJobService appliedJobService;

    @Autowired
    private UserService userService;

    @Autowired
    private JobDetailService jobDetailService;

    // Ứng tuyển vào công việc
    @PostMapping
    public ResponseEntity<?> applyForJob(@RequestBody AppliedJobRequest request) {
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
            AppliedJob appliedJob = appliedJobService.applyForJob(user.get(), jobDetail);
            return ApiResponseUtil.created(appliedJob);
        } catch (RuntimeException e) {
            return ApiResponseUtil.error(e.getMessage());
        }
    }

    // Lấy các công việc đã ứng tuyển của người dùng hiện tại
    @GetMapping("/my-applications")
    public ResponseEntity<?> getMyApplications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponseUtil.error("User not authenticated");
        }

        String username = authentication.getName();
        Optional<User> user = userService.getUserByTaiKhoan(username);
        if (!user.isPresent()) {
            return ApiResponseUtil.error("User not found");
        }

        List<AppliedJob> appliedJobs = appliedJobService.getAppliedJobsByEmployee(user.get());
        return ApiResponseUtil.success("My applications retrieved successfully", appliedJobs);
    }

    // Hủy ứng tuyển
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelApplication(@PathVariable Integer id) {
        Optional<AppliedJob> appliedJobOpt = appliedJobService.getAppliedJobById(id);
        if (!appliedJobOpt.isPresent()) {
            return ApiResponseUtil.error("Applied job not found with id: " + id);
        }

        AppliedJob appliedJob = appliedJobOpt.get();

        // Kiểm tra quyền: chỉ người đã ứng tuyển mới có thể hủy
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponseUtil.error("User not authenticated");
        }

        String username = authentication.getName();
        Optional<User> user = userService.getUserByTaiKhoan(username);
        if (!user.isPresent()) {
            return ApiResponseUtil.error("User not found");
        }

        if (!appliedJob.getEmployee().getMaNguoiDung().equals(user.get().getMaNguoiDung())) {
            return ApiResponseUtil.error("You don't have permission to cancel this application");
        }

        appliedJobService.deleteAppliedJob(id);
        return ApiResponseUtil.noContent();
    }

    // Cập nhật trạng thái ứng tuyển (dành cho nhà tuyển dụng)
    // Request classes
    public static class AppliedJobRequest {
        private Integer jobDetailId;

        public Integer getJobDetailId() {
            return jobDetailId;
        }

        public void setJobDetailId(Integer jobDetailId) {
            this.jobDetailId = jobDetailId;
        }
    }
}