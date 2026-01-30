package com.example.demo.service;

import com.example.demo.repository.JobDetailRepository;
import com.example.demo.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MarketStatisticsService {

    @Autowired
    private JobDetailRepository jobDetailRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public Map<String, Object> getMarketOverview() {
        Map<String, Object> statistics = new HashMap<>();

        // Đếm tổng số công việc đang hoạt động
        int totalJobs = jobDetailRepository.countByTrangThaiDuyetAndTrangThaiTinTuyen("Đã duyệt", "Mở");

        // Đếm tổng số công ty đã xác thực
        int totalCompanies = companyRepository.countByDaXacThucTrue();

        statistics.put("totalJobs", totalJobs);
        statistics.put("totalCompanies", totalCompanies);

        return statistics;
    }
}