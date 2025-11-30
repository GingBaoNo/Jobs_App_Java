package com.example.demo.repository;

import com.example.demo.entity.JobDetail;
import com.example.demo.entity.JobLocation;
import com.example.demo.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobLocationRepository extends JpaRepository<JobLocation, Integer> {
    List<JobLocation> findByJobDetail(JobDetail jobDetail);
    List<JobLocation> findByLocation(Location location);
}