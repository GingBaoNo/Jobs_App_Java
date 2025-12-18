package com.example.demo.controller.api;

import com.example.demo.entity.WorkDiscipline;
import com.example.demo.service.WorkDisciplineService;
import com.example.demo.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
        return workDisciplineService.getWorkDisciplineById(id)
            .map(workDiscipline -> ApiResponseUtil.success("Work discipline retrieved successfully", workDiscipline))
            .orElse(ApiResponseUtil.error("Work discipline not found with id: " + id));
    }

    @GetMapping("/field/{id}")
    public ResponseEntity<?> getWorkDisciplinesByWorkField(@PathVariable Integer id) {
        List<WorkDiscipline> workDisciplines = workDisciplineService.getWorkDisciplinesByWorkFieldId(id);
        return ApiResponseUtil.success("Work disciplines retrieved successfully", workDisciplines);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchWorkDisciplines(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ApiResponseUtil.error("Keyword parameter is required");
        }
        List<WorkDiscipline> workDisciplines = workDisciplineService.getWorkDisciplinesBySearch(keyword);
        return ApiResponseUtil.success("Work disciplines searched successfully", workDisciplines);
    }

    @PostMapping
    public ResponseEntity<?> createWorkDiscipline(@RequestBody WorkDiscipline workDiscipline) {
        WorkDiscipline savedWorkDiscipline = workDisciplineService.saveWorkDiscipline(workDiscipline);
        return ApiResponseUtil.created(savedWorkDiscipline);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWorkDiscipline(@PathVariable Integer id, @RequestBody WorkDiscipline workDiscipline) {
        if (!workDisciplineService.getWorkDisciplineById(id).isPresent()) {
            return ApiResponseUtil.error("Work discipline not found with id: " + id);
        }
        workDiscipline.setMaNganh(id);
        WorkDiscipline updatedWorkDiscipline = workDisciplineService.updateWorkDiscipline(workDiscipline);
        return ApiResponseUtil.success("Work discipline updated successfully", updatedWorkDiscipline);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWorkDiscipline(@PathVariable Integer id) {
        if (!workDisciplineService.getWorkDisciplineById(id).isPresent()) {
            return ApiResponseUtil.error("Work discipline not found with id: " + id);
        }
        workDisciplineService.deleteWorkDiscipline(id);
        return ApiResponseUtil.noContent();
    }
}