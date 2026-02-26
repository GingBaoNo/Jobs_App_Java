package com.example.demo.repository;

import com.example.demo.entity.JobPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPositionRepository extends JpaRepository<JobPosition, Integer> {
    List<JobPosition> findByWorkDiscipline_MaNganh(Integer workDisciplineId);

    @Query("SELECT jp FROM JobPosition jp WHERE jp.tenViTri LIKE CONCAT('%', :keyword, '%')")
    List<JobPosition> findByTenViTriContaining(@Param("keyword") String keyword);
}