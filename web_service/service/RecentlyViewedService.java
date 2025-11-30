package com.example.demo.service;

import com.example.demo.entity.JobDetail;
import com.example.demo.entity.RecentlyViewed;
import com.example.demo.entity.User;
import com.example.demo.repository.RecentlyViewedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecentlyViewedService {
    
    @Autowired
    private RecentlyViewedRepository recentlyViewedRepository;
    
    public List<RecentlyViewed> getAllRecentlyViewed() {
        return recentlyViewedRepository.findAll();
    }
    
    public List<RecentlyViewed> getRecentlyViewedByUser(User user) {
        return recentlyViewedRepository.findByUser(user);
    }
    
    public List<RecentlyViewed> getRecentlyViewedByJobDetail(JobDetail jobDetail) {
        return recentlyViewedRepository.findByJobDetail(jobDetail);
    }
    
    public List<RecentlyViewed> getRecentlyViewedByUserAndAfterTime(User user, LocalDateTime time) {
        return recentlyViewedRepository.findByUserAndThoiGianXemAfter(user, time);
    }
    
    public List<RecentlyViewed> getTop5RecentlyViewedByUser(User user) {
        return recentlyViewedRepository.findTop5ByUserOrderByThoiGianXemDesc(user);
    }
    
    public RecentlyViewed saveRecentlyViewed(RecentlyViewed recentlyViewed) {
        return recentlyViewedRepository.save(recentlyViewed);
    }
    
    public void deleteRecentlyViewed(Integer id) {
        recentlyViewedRepository.deleteById(id);
    }
    
    public void recordViewing(User user, JobDetail jobDetail) {
        // Xóa bản ghi xem gần đây cũ nếu tồn tại
        List<RecentlyViewed> existing = recentlyViewedRepository.findByUserAndJobDetail(user, jobDetail);
        for (RecentlyViewed rv : existing) {
            recentlyViewedRepository.delete(rv);
        }
        
        // Thêm bản ghi mới
        RecentlyViewed recentlyViewed = new RecentlyViewed(user, jobDetail);
        saveRecentlyViewed(recentlyViewed);
    }
}