package com.example.demo.controller.api;

import com.example.demo.entity.JobPosition;
import com.example.demo.service.JobPositionService;
import com.example.demo.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/job-positions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiJobPositionController {

    @Autowired
    private JobPositionService jobPositionService;

    @GetMapping
    public ResponseEntity<?> getAllJobPositions() {
        List<JobPosition> jobPositions = jobPositionService.getAllJobPositions();
        return ApiResponseUtil.success("Job positions retrieved successfully", jobPositions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJobPositionById(@PathVariable Integer id) {
        return jobPositionService.getJobPositionById(id)
            .map(jobPosition -> ApiResponseUtil.success("Job position retrieved successfully", jobPosition))
            .orElse(ApiResponseUtil.error("Job position not found with id: " + id));
    }

    @GetMapping("/discipline/{id}")
    public ResponseEntity<?> getJobPositionsByWorkDiscipline(@PathVariable Integer id) {
        List<JobPosition> jobPositions = jobPositionService.getJobPositionsByWorkDisciplineId(id);
        return ApiResponseUtil.success("Job positions retrieved successfully", jobPositions);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchJobPositions(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ApiResponseUtil.error("Keyword parameter is required");
        }
        List<JobPosition> jobPositions = jobPositionService.getJobPositionsBySearch(keyword);
        return ApiResponseUtil.success("Job positions searched successfully", jobPositions);
    }

    @PostMapping
    public ResponseEntity<?> createJobPosition(@RequestBody JobPosition jobPosition) {
        JobPosition savedJobPosition = jobPositionService.saveJobPosition(jobPosition);
        return ApiResponseUtil.created(savedJobPosition);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJobPosition(@PathVariable Integer id, @RequestBody JobPosition jobPosition) {
        if (!jobPositionService.getJobPositionById(id).isPresent()) {
            return ApiResponseUtil.error("Job position not found with id: " + id);
        }
        jobPosition.setMaViTri(id);
        JobPosition updatedJobPosition = jobPositionService.updateJobPosition(jobPosition);
        return ApiResponseUtil.success("Job position updated successfully", updatedJobPosition);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJobPosition(@PathVariable Integer id) {
        if (!jobPositionService.getJobPositionById(id).isPresent()) {
            return ApiResponseUtil.error("Job position not found with id: " + id);
        }
        jobPositionService.deleteJobPosition(id);
        return ApiResponseUtil.noContent();
    }
}