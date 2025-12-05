package com.example.demo.service;

import com.example.demo.entity.AppliedJob;
import com.example.demo.entity.JobDetail;
import com.example.demo.entity.Profile;
import com.example.demo.entity.User;
import com.example.demo.repository.AppliedJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.service.ProfileService;

import java.util.List;
import java.util.Optional;

@Service
public class AppliedJobService {

    @Autowired
    private AppliedJobRepository appliedJobRepository;

    @Autowired
    private ProfileService profileService;
    
    public List<AppliedJob> getAllAppliedJobs() {
        return appliedJobRepository.findAll();
    }
    
    public List<AppliedJob> getAppliedJobsByEmployee(User employee) {
        return appliedJobRepository.findByEmployee(employee);
    }
    
    public List<AppliedJob> getAppliedJobsByJobDetail(JobDetail jobDetail) {
        return appliedJobRepository.findByJobDetail(jobDetail);
    }
    
    public Optional<AppliedJob> getAppliedJobById(Integer id) {
        return appliedJobRepository.findById(id);
    }
    
    public Optional<AppliedJob> getAppliedJobByEmployeeAndJobDetail(User employee, JobDetail jobDetail) {
        return appliedJobRepository.findByEmployeeAndJobDetail(employee, jobDetail);
    }
    
    public AppliedJob saveAppliedJob(AppliedJob appliedJob) {
        return appliedJobRepository.save(appliedJob);
    }
    
    public AppliedJob updateAppliedJob(AppliedJob appliedJob) {
        return appliedJobRepository.save(appliedJob);
    }
    
    public void deleteAppliedJob(Integer id) {
        appliedJobRepository.deleteById(id);
    }
    
    public AppliedJob applyForJob(User employee, JobDetail jobDetail) {
        Optional<AppliedJob> existingApplication = appliedJobRepository.findByEmployeeAndJobDetail(employee, jobDetail);
        if (existingApplication.isPresent()) {
            throw new RuntimeException("Bạn đã ứng tuyển vào công việc này rồi");
        }

        // Lấy hồ sơ người dùng để copy CV nếu có
        Optional<Profile> profileOpt = profileService.getProfileByUser(employee);
        String cvUrl = null;
        if (profileOpt.isPresent() && profileOpt.get().getUrlCv() != null) {
            cvUrl = profileOpt.get().getUrlCv();
        }

        AppliedJob appliedJob = new AppliedJob(employee, jobDetail, cvUrl);
        return saveAppliedJob(appliedJob);
    }

    public AppliedJob applyForJobWithCv(User employee, JobDetail jobDetail, String urlCv) {
        Optional<AppliedJob> existingApplication = appliedJobRepository.findByEmployeeAndJobDetail(employee, jobDetail);
        if (existingApplication.isPresent()) {
            throw new RuntimeException("Bạn đã ứng tuyển vào công việc này rồi");
        }

        AppliedJob appliedJob = new AppliedJob(employee, jobDetail, urlCv);
        return saveAppliedJob(appliedJob);
    }

    public Optional<AppliedJob> getAppliedJobByIdWithDetails(Integer id) {
        return appliedJobRepository.findByIdWithDetails(id);
    }

    public List<AppliedJob> getAppliedJobsByEmployer(Integer employerId) {
        return appliedJobRepository.findByJobDetailCompanyUserMaNguoiDung(employerId);
    }

    public List<AppliedJob> getAppliedJobsByEmployerAndJob(Integer employerId, JobDetail jobDetail) {
        return appliedJobRepository.findByJobDetailCompanyUserMaNguoiDungAndJobDetail(employerId, jobDetail);
    }
}