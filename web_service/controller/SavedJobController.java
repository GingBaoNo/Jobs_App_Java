package com.example.demo.controller;

import com.example.demo.entity.JobDetail;
import com.example.demo.entity.SavedJob;
import com.example.demo.entity.User;
import com.example.demo.service.JobDetailService;
import com.example.demo.service.SavedJobService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class SavedJobController {

    @Autowired
    private SavedJobService savedJobService;

    @Autowired
    private UserService userService;

    @Autowired
    private JobDetailService jobDetailService;

    // Endpoint để lưu hoặc bỏ lưu công việc từ frontend web
    @PostMapping("/save-job")
    @ResponseBody
    public ResponseEntity<?> saveJob(@RequestBody SaveJobRequest request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"Người dùng chưa đăng nhập\"}");
        }

        String username = authentication.getName();
        Optional<User> user = userService.getUserByTaiKhoan(username);
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"Không tìm thấy người dùng\"}");
        }

        JobDetail jobDetail = jobDetailService.getJobById(request.getJobId());
        if (jobDetail == null) {
            return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"Không tìm thấy công việc\"}");
        }

        try {
            String action = request.getAction();
            if ("save".equals(action)) {
                // Kiểm tra xem công việc đã được lưu chưa
                Optional<SavedJob> existingSaved = savedJobService.getSavedJobByUserAndJobDetail(user.get(), jobDetail);
                if (existingSaved.isPresent()) {
                    return ResponseEntity.ok().body("{\"success\": true, \"message\": \"Công việc đã được lưu trước đó\"}");
                }
                
                // Lưu công việc
                savedJobService.saveJob(user.get(), jobDetail);
                return ResponseEntity.ok().body("{\"success\": true, \"message\": \"Lưu công việc thành công\"}");
            } else if ("remove".equals(action)) {
                // Bỏ lưu công việc
                savedJobService.removeSavedJob(user.get(), jobDetail);
                return ResponseEntity.ok().body("{\"success\": true, \"message\": \"Bỏ lưu công việc thành công\"}");
            } else {
                return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"Hành động không hợp lệ\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }

    // Request class
    public static class SaveJobRequest {
        private Integer jobId;
        private String action; // "save" or "remove"

        public Integer getJobId() {
            return jobId;
        }

        public void setJobId(Integer jobId) {
            this.jobId = jobId;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }
    }
}