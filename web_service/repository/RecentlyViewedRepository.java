package com.example.demo.repository;

import com.example.demo.entity.JobDetail;
import com.example.demo.entity.RecentlyViewed;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecentlyViewedRepository extends JpaRepository<RecentlyViewed, Integer> {
    List<RecentlyViewed> findByUser(User user);
    List<RecentlyViewed> findByJobDetail(JobDetail jobDetail);
    List<RecentlyViewed> findByUserAndJobDetail(User user, JobDetail jobDetail);
    List<RecentlyViewed> findByUserAndThoiGianXemAfter(User user, LocalDateTime thoiGianXem);
    
    @Query("SELECT rv FROM RecentlyViewed rv WHERE rv.user = :user ORDER BY rv.thoiGianXem DESC")
    List<RecentlyViewed> findTop5ByUserOrderByThoiGianXemDesc(@Param("user") User user);
}