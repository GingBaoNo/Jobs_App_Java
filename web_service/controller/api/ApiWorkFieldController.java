package com.example.demo.controller.api;

import com.example.demo.entity.WorkField;
import com.example.demo.service.WorkFieldService;
import com.example.demo.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/work-fields")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiWorkFieldController {

    @Autowired
    private WorkFieldService workFieldService;

    @GetMapping
    public ResponseEntity<?> getAllWorkFields() {
        List<WorkField> workFields = workFieldService.getAllWorkFields();
        return ApiResponseUtil.success("Work fields retrieved successfully", workFields);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWorkFieldById(@PathVariable Integer id) {
        WorkField workField = workFieldService.getWorkFieldById(id);
        if (workField != null) {
            return ApiResponseUtil.success("Work field retrieved successfully", workField);
        } else {
            return ApiResponseUtil.error("Work field not found with id: " + id);
        }
    }

    @PostMapping
    public ResponseEntity<?> createWorkField(@RequestBody WorkField workField) {
        WorkField savedWorkField = workFieldService.saveWorkField(workField);
        return ApiResponseUtil.created(savedWorkField);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWorkField(@PathVariable Integer id, @RequestBody WorkField workField) {
        WorkField existingWorkField = workFieldService.getWorkFieldById(id);
        if (existingWorkField == null) {
            return ApiResponseUtil.error("Work field not found with id: " + id);
        }
        workField.setMaLinhVuc(id);
        WorkField updatedWorkField = workFieldService.updateWorkField(workField);
        return ApiResponseUtil.success("Work field updated successfully", updatedWorkField);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWorkField(@PathVariable Integer id) {
        WorkField existingWorkField = workFieldService.getWorkFieldById(id);
        if (existingWorkField == null) {
            return ApiResponseUtil.error("Work field not found with id: " + id);
        }
        workFieldService.deleteWorkField(id);
        return ApiResponseUtil.noContent();
    }
}