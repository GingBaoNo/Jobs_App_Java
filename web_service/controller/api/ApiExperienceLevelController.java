package com.example.demo.controller.api;

import com.example.demo.entity.ExperienceLevel;
import com.example.demo.service.ExperienceLevelService;
import com.example.demo.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/experience-levels")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiExperienceLevelController {

    @Autowired
    private ExperienceLevelService experienceLevelService;

    @GetMapping
    public ResponseEntity<?> getAllExperienceLevels() {
        List<ExperienceLevel> experienceLevels = experienceLevelService.getAllExperienceLevels();
        return ApiResponseUtil.success("Experience levels retrieved successfully", experienceLevels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExperienceLevelById(@PathVariable Integer id) {
        return experienceLevelService.getExperienceLevelById(id)
            .map(experienceLevel -> ApiResponseUtil.success("Experience level retrieved successfully", experienceLevel))
            .orElse(ApiResponseUtil.error("Experience level not found with id: " + id));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchExperienceLevels(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ApiResponseUtil.error("Keyword parameter is required");
        }
        List<ExperienceLevel> experienceLevels = experienceLevelService.getExperienceLevelsBySearch(keyword);
        return ApiResponseUtil.success("Experience levels searched successfully", experienceLevels);
    }

    @PostMapping
    public ResponseEntity<?> createExperienceLevel(@RequestBody ExperienceLevel experienceLevel) {
        ExperienceLevel savedExperienceLevel = experienceLevelService.saveExperienceLevel(experienceLevel);
        return ApiResponseUtil.created(savedExperienceLevel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExperienceLevel(@PathVariable Integer id, @RequestBody ExperienceLevel experienceLevel) {
        if (!experienceLevelService.getExperienceLevelById(id).isPresent()) {
            return ApiResponseUtil.error("Experience level not found with id: " + id);
        }
        experienceLevel.setMaCapDo(id);
        ExperienceLevel updatedExperienceLevel = experienceLevelService.updateExperienceLevel(experienceLevel);
        return ApiResponseUtil.success("Experience level updated successfully", updatedExperienceLevel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExperienceLevel(@PathVariable Integer id) {
        if (!experienceLevelService.getExperienceLevelById(id).isPresent()) {
            return ApiResponseUtil.error("Experience level not found with id: " + id);
        }
        experienceLevelService.deleteExperienceLevel(id);
        return ApiResponseUtil.noContent();
    }
}