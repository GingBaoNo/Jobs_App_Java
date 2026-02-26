package com.example.demo.repository;

import com.example.demo.entity.ExperienceLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceLevelRepository extends JpaRepository<ExperienceLevel, Integer> {
    @Query("SELECT el FROM ExperienceLevel el WHERE el.tenCapDo LIKE CONCAT('%', :keyword, '%')")
    List<ExperienceLevel> findByTenCapDoContaining(@Param("keyword") String keyword);
}