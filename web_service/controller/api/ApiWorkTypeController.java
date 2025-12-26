package com.example.demo.controller.api;

import com.example.demo.entity.WorkType;
import com.example.demo.service.WorkTypeService;
import com.example.demo.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/work-types")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiWorkTypeController {

    @Autowired
    private WorkTypeService workTypeService;

    @GetMapping
    public ResponseEntity<?> getAllWorkTypes() {
        List<WorkType> workTypes = workTypeService.getAllWorkTypes();
        return ApiResponseUtil.success("Work types retrieved successfully", workTypes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWorkTypeById(@PathVariable Integer id) {
        WorkType workType = workTypeService.getWorkTypeById(id);
        if (workType != null) {
            return ApiResponseUtil.success("Work type retrieved successfully", workType);
        } else {
            return ApiResponseUtil.error("Work type not found with id: " + id);
        }
    }

    @PostMapping
    public ResponseEntity<?> createWorkType(@RequestBody WorkType workType) {
        WorkType savedWorkType = workTypeService.saveWorkType(workType);
        return ApiResponseUtil.created(savedWorkType);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWorkType(@PathVariable Integer id, @RequestBody WorkType workType) {
        WorkType existingWorkType = workTypeService.getWorkTypeById(id);
        if (existingWorkType == null) {
            return ApiResponseUtil.error("Work type not found with id: " + id);
        }
        workType.setMaHinhThuc(id);
        WorkType updatedWorkType = workTypeService.updateWorkType(workType);
        return ApiResponseUtil.success("Work type updated successfully", updatedWorkType);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWorkType(@PathVariable Integer id) {
        WorkType existingWorkType = workTypeService.getWorkTypeById(id);
        if (existingWorkType == null) {
            return ApiResponseUtil.error("Work type not found with id: " + id);
        }
        workTypeService.deleteWorkType(id);
        return ApiResponseUtil.noContent();
    }
}