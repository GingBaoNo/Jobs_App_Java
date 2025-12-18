package com.example.demo.service;

import com.example.demo.entity.JobPosition;
import com.example.demo.repository.JobPositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobPositionService {

    @Autowired
    private JobPositionRepository jobPositionRepository;

    public List<JobPosition> getAllJobPositions() {
        return jobPositionRepository.findAll();
    }

    public Optional<JobPosition> getJobPositionById(Integer id) {
        return jobPositionRepository.findById(id);
    }

    public JobPosition saveJobPosition(JobPosition jobPosition) {
        return jobPositionRepository.save(jobPosition);
    }

    public JobPosition updateJobPosition(JobPosition jobPosition) {
        return jobPositionRepository.save(jobPosition);
    }

    public void deleteJobPosition(Integer id) {
        jobPositionRepository.deleteById(id);
    }

    public List<JobPosition> getJobPositionsByWorkDisciplineId(Integer workDisciplineId) {
        return jobPositionRepository.findByWorkDiscipline_MaNganh(workDisciplineId);
    }

    public List<JobPosition> getJobPositionsBySearch(String keyword) {
        return jobPositionRepository.findByTenViTriContaining(keyword);
    }
}