package com.example.demo.repository;

import com.example.demo.entity.AppliedJob;
import com.example.demo.entity.JobDetail;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppliedJobRepository extends JpaRepository<AppliedJob, Integer> {
    List<AppliedJob> findByEmployee(User employee);
    List<AppliedJob> findByJobDetail(JobDetail jobDetail);
    Optional<AppliedJob> findByEmployeeAndJobDetail(User employee, JobDetail jobDetail);
}