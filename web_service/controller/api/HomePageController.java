package com.example.demo.controller.api;

import com.example.demo.entity.WorkField;
import com.example.demo.service.JobDetailService;
import com.example.demo.service.MarketStatisticsService;
import com.example.demo.service.WorkFieldService;
import com.example.demo.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/home")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HomePageController {

    @Autowired
    private JobDetailService jobDetailService;

    @Autowired
    private WorkFieldService workFieldService;

    @Autowired
    private MarketStatisticsService marketStatisticsService;

    // API lấy công việc mới nhất
    @GetMapping("/latest-jobs")
    public ResponseEntity<?> getLatestJobs() {
        try {
            List<com.example.demo.entity.JobDetail> latestJobs = jobDetailService.getLatestJobs(10);
            return ApiResponseUtil.success("Latest jobs retrieved successfully", latestJobs);
        } catch (Exception e) {
            return ApiResponseUtil.error("Error retrieving latest jobs: " + e.getMessage());
        }
    }

    // API lấy lĩnh vực công việc phổ biến
    @GetMapping("/popular-work-fields")
    public ResponseEntity<?> getPopularWorkFields() {
        try {
            List<WorkField> popularFields = workFieldService.getPopularWorkFields(10);
            return ApiResponseUtil.success("Popular work fields retrieved successfully", popularFields);
        } catch (Exception e) {
            return ApiResponseUtil.error("Error retrieving popular work fields: " + e.getMessage());
        }
    }

    // API lấy thống kê thị trường
    @GetMapping("/market-overview")
    public ResponseEntity<?> getMarketOverview() {
        try {
            Map<String, Object> marketStats = marketStatisticsService.getMarketOverview();
            return ApiResponseUtil.success("Market overview retrieved successfully", marketStats);
        } catch (Exception e) {
            return ApiResponseUtil.error("Error retrieving market overview: " + e.getMessage());
        }
    }
}