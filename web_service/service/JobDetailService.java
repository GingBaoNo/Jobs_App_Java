package com.example.demo.service;

import com.example.demo.entity.Company;
import com.example.demo.entity.JobDetail;
import com.example.demo.entity.WorkField;
import com.example.demo.repository.JobDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class JobDetailService {
    
    @Autowired
    private JobDetailRepository jobDetailRepository;
    
    public List<JobDetail> getAllJobs() {
        return jobDetailRepository.findAll();
    }
    
    public List<JobDetail> getJobsByCompany(Company company) {
        return jobDetailRepository.findByCompany(company);
    }
    
    public List<JobDetail> getActiveJobs() {
        return jobDetailRepository.findByTrangThaiDuyetAndTrangThaiTinTuyen("Đã duyệt", "Mở");
    }
    
    public List<JobDetail> getActiveJobsWithValidDate() {
        return jobDetailRepository.findByNgayKetThucTuyenDungAfterAndTrangThaiDuyetAndTrangThaiTinTuyen(
            LocalDate.now(), "Đã duyệt", "Mở");
    }
    
    public List<JobDetail> getJobsByTrangThaiDuyet(String trangThaiDuyet) {
        return jobDetailRepository.findByTrangThaiDuyet(trangThaiDuyet);
    }
    
    public List<JobDetail> getJobsByWorkField(WorkField workField) {
        return jobDetailRepository.findByWorkField(workField);
    }
    
    public List<JobDetail> getJobsByKeyword(String keyword) {
        return jobDetailRepository.findByTieuDeContainingOrChiTietContaining(keyword);
    }
    
    public JobDetail getJobById(Integer id) {
        return jobDetailRepository.findById(id).orElse(null);
    }
    
    public JobDetail saveJob(JobDetail jobDetail) {
        return jobDetailRepository.save(jobDetail);
    }
    
    public JobDetail updateJob(JobDetail jobDetail) {
        return jobDetailRepository.save(jobDetail);
    }
    
    public void deleteJob(Integer id) {
        jobDetailRepository.deleteById(id);
    }
    
    public List<JobDetail> getFeaturedJobs() {
        // Lấy các công việc nổi bật: những công việc được duyệt, còn hiệu lực và có nhiều lượt xem
        return jobDetailRepository.findTop10ByTrangThaiDuyetAndTrangThaiTinTuyenOrderByLuotXemDesc("Đã duyệt", "Mở");
    }

    public void incrementViewCount(JobDetail jobDetail) {
        jobDetail.setLuotXem(jobDetail.getLuotXem() + 1);
        jobDetailRepository.save(jobDetail);
    }
}