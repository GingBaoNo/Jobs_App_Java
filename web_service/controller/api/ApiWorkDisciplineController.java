package com.example.demo.controller.api;

import com.example.demo.entity.WorkDiscipline;
import com.example.demo.service.WorkDisciplineService;
import com.example.demo.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/work-disciplines")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiWorkDisciplineController {

    @Autowired
    private WorkDisciplineService workDisciplineService;

    @GetMapping
    public ResponseEntity<?> getAllWorkDisciplines() {
        List<WorkDiscipline> workDisciplines = workDisciplineService.getAllWorkDisciplines();
        return ApiResponseUtil.success("Work disciplines retrieved successfully", workDisciplines);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWorkDisciplineById(@PathVariable Integer id) {
        WorkDiscipline workDiscipline = workDisciplineService.getWorkDisciplineById(id);
        if (workDiscipline != null) {
            return ApiResponseUtil.success("Work discipline retrieved successfully", workDiscipline);
        } else {
            return ApiResponseUtil.error("Work discipline not found with id: " + id);
        }
    }

    @GetMapping("/field/{workFieldId}")
    public ResponseEntity<?> getWorkDisciplinesByWorkField(@PathVariable Integer workFieldId) {
        List<WorkDiscipline> workDisciplines = workDisciplineService.getWorkDisciplinesByWorkFieldId(workFieldId);
        return ApiResponseUtil.success("Work disciplines retrieved successfully", workDisciplines);
    }

    @PostMapping
    public ResponseEntity<?> createWorkDiscipline(@RequestBody WorkDiscipline workDiscipline) {
        WorkDiscipline savedWorkDiscipline = workDisciplineService.saveWorkDiscipline(workDiscipline);
        return ApiResponseUtil.created(savedWorkDiscipline);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWorkDiscipline(@PathVariable Integer id, @RequestBody WorkDiscipline workDiscipline) {
        WorkDiscipline existingWorkDiscipline = workDisciplineService.getWorkDisciplineById(id);
        if (existingWorkDiscipline == null) {
            return ApiResponseUtil.error("Work discipline not found with id: " + id);
        }
        workDiscipline.setMaNganh(id);
        WorkDiscipline updatedWorkDiscipline = workDisciplineService.updateWorkDiscipline(workDiscipline);
        return ApiResponseUtil.success("Work discipline updated successfully", updatedWorkDiscipline);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWorkDiscipline(@PathVariable Integer id) {
        WorkDiscipline existingWorkDiscipline = workDisciplineService.getWorkDisciplineById(id);
        if (existingWorkDiscipline == null) {
            return ApiResponseUtil.error("Work discipline not found with id: " + id);
        }
        workDisciplineService.deleteWorkDiscipline(id);
        return ApiResponseUtil.noContent();
    }
}