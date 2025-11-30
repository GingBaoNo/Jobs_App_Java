package com.example.demo.service;

import com.example.demo.entity.JobDetail;
import com.example.demo.entity.SavedJob;
import com.example.demo.entity.User;
import com.example.demo.repository.SavedJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SavedJobService {
    
    @Autowired
    private SavedJobRepository savedJobRepository;
    
    public List<SavedJob> getAllSavedJobs() {
        return savedJobRepository.findAll();
    }
    
    public List<SavedJob> getSavedJobsByUser(User user) {
        return savedJobRepository.findByUser(user);
    }
    
    public List<SavedJob> getSavedJobsByJobDetail(JobDetail jobDetail) {
        return savedJobRepository.findByJobDetail(jobDetail);
    }
    
    public Optional<SavedJob> getSavedJobById(Integer id) {
        return savedJobRepository.findById(id);
    }
    
    public Optional<SavedJob> getSavedJobByUserAndJobDetail(User user, JobDetail jobDetail) {
        return savedJobRepository.findByUserAndJobDetail(user, jobDetail);
    }
    
    public SavedJob saveSavedJob(SavedJob savedJob) {
        return savedJobRepository.save(savedJob);
    }
    
    public SavedJob updateSavedJob(SavedJob savedJob) {
        return savedJobRepository.save(savedJob);
    }
    
    public void deleteSavedJob(Integer id) {
        savedJobRepository.deleteById(id);
    }
    
    public SavedJob saveJob(User user, JobDetail jobDetail) {
        Optional<SavedJob> existingSaved = savedJobRepository.findByUserAndJobDetail(user, jobDetail);
        if (existingSaved.isPresent()) {
            throw new RuntimeException("Bạn đã lưu công việc này rồi");
        }
        
        SavedJob savedJob = new SavedJob(user, jobDetail);
        return saveSavedJob(savedJob);
    }
    
    public void removeSavedJob(User user, JobDetail jobDetail) {
        Optional<SavedJob> existingSaved = savedJobRepository.findByUserAndJobDetail(user, jobDetail);
        existingSaved.ifPresent(savedJobRepository::delete);
    }
}