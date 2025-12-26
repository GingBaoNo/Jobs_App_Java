package com.example.demo.controller.api;

import com.example.demo.entity.JobPosition;
import com.example.demo.service.JobPositionService;
import com.example.demo.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        JobPosition jobPosition = jobPositionService.getJobPositionById(id);
        if (jobPosition != null) {
            return ApiResponseUtil.success("Job position retrieved successfully", jobPosition);
        } else {
            return ApiResponseUtil.error("Job position not found with id: " + id);
        }
    }

    @GetMapping("/discipline/{workDisciplineId}")
    public ResponseEntity<?> getJobPositionsByWorkDiscipline(@PathVariable Integer workDisciplineId) {
        List<JobPosition> jobPositions = jobPositionService.getJobPositionsByWorkDisciplineId(workDisciplineId);
        return ApiResponseUtil.success("Job positions retrieved successfully", jobPositions);
    }

    @PostMapping
    public ResponseEntity<?> createJobPosition(@RequestBody JobPosition jobPosition) {
        JobPosition savedJobPosition = jobPositionService.saveJobPosition(jobPosition);
        return ApiResponseUtil.created(savedJobPosition);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJobPosition(@PathVariable Integer id, @RequestBody JobPosition jobPosition) {
        JobPosition existingJobPosition = jobPositionService.getJobPositionById(id);
        if (existingJobPosition == null) {
            return ApiResponseUtil.error("Job position not found with id: " + id);
        }
        jobPosition.setMaViTri(id);
        JobPosition updatedJobPosition = jobPositionService.updateJobPosition(jobPosition);
        return ApiResponseUtil.success("Job position updated successfully", updatedJobPosition);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJobPosition(@PathVariable Integer id) {
        JobPosition existingJobPosition = jobPositionService.getJobPositionById(id);
        if (existingJobPosition == null) {
            return ApiResponseUtil.error("Job position not found with id: " + id);
        }
        jobPositionService.deleteJobPosition(id);
        return ApiResponseUtil.noContent();
    }
}