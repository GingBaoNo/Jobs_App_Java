package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class JobController {
    
    @Autowired
    private JobDetailService jobDetailService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CompanyService companyService;
    
    @Autowired
    private WorkFieldService workFieldService;
    
    @Autowired
    private WorkTypeService workTypeService;
    
    @Autowired
    private LocationService locationService;
    
    // Trang danh sách công việc của nhà tuyển dụng
    @GetMapping("/employer/jobs")
    public String employerJobs(Authentication authentication, Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        
        Company company = companyService.getCompanyByUser(user).orElse(null);
        if (company == null) {
            model.addAttribute("errorMessage", "Bạn cần đăng ký công ty trước khi tạo tin tuyển dụng.");
            return "employer/jobs";
        }
        
        List<JobDetail> jobs = jobDetailService.getJobsByCompany(company);
        model.addAttribute("jobs", jobs);
        model.addAttribute("title", "Quản lý tin tuyển dụng");
        
        return "employer/jobs";
    }
    
    // Trang tạo công việc mới
    @GetMapping("/employer/jobs/create")
    public String createJobForm(Authentication authentication, Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        
        Company company = companyService.getCompanyByUser(user).orElse(null);
        if (company == null) {
            model.addAttribute("errorMessage", "Bạn cần đăng ký công ty trước khi tạo tin tuyển dụng.");
            return "redirect:/employer/jobs";
        }
        
        model.addAttribute("job", new JobDetail());
        model.addAttribute("workFields", workFieldService.getAllWorkFields());
        model.addAttribute("workTypes", workTypeService.getAllWorkTypes());
        model.addAttribute("locations", locationService.getProvinces());
        model.addAttribute("title", "Tạo tin tuyển dụng mới");
        
        return "employer/job-form";
    }
    
    // Xử lý tạo công việc mới
    @PostMapping("/employer/jobs/create")
    public String createJob(@ModelAttribute JobDetail job, Authentication authentication, Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        
        Company company = companyService.getCompanyByUser(user).orElse(null);
        if (company == null) {
            model.addAttribute("errorMessage", "Bạn cần đăng ký công ty trước khi tạo tin tuyển dụng.");
            return "redirect:/employer/jobs";
        }
        
        try {
            // Gán công ty cho công việc
            job.setCompany(company);
            
            // Đặt trạng thái mặc định
            job.setTrangThaiDuyet("Chờ duyệt");
            job.setTrangThaiTinTuyen("Mở");

            // Đặt ngày đăng là ngày hiện tại nếu chưa có
            if (job.getNgayDang() == null) {
                job.setNgayDang(java.time.LocalDateTime.now());
            }

            // Đặt ngày hết hạn nếu chưa có
            if (job.getNgayKetThucTuyenDung() == null) {
                job.setNgayKetThucTuyenDung(LocalDate.now().plusDays(30));
            }

            // Đặt giá trị mặc định cho các trường mới nếu chưa có
            if (job.getYeuCauCongViec() == null) {
                job.setYeuCauCongViec("");
            }
            if (job.getQuyenLoi() == null) {
                job.setQuyenLoi("");
            }
            
            JobDetail savedJob = jobDetailService.saveJob(job);
            model.addAttribute("successMessage", "Tạo tin tuyển dụng thành công! Vui lòng chờ quản trị viên duyệt.");
            return "redirect:/employer/jobs";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tạo tin tuyển dụng: " + e.getMessage());
            model.addAttribute("workFields", workFieldService.getAllWorkFields());
            model.addAttribute("workTypes", workTypeService.getAllWorkTypes());
            model.addAttribute("locations", locationService.getProvinces());
            model.addAttribute("job", job);
            return "employer/job-form";
        }
    }
    
    // Trang chỉnh sửa công việc
    @GetMapping("/employer/jobs/{id}/edit")
    public String editJobForm(@PathVariable Integer id, Authentication authentication, Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        
        JobDetail job = jobDetailService.getJobById(id);
        if (job == null) {
            model.addAttribute("errorMessage", "Không tìm thấy tin tuyển dụng.");
            return "redirect:/employer/jobs";
        }
        
        // Kiểm tra xem công việc này có thuộc về công ty của người dùng không
        Company company = companyService.getCompanyByUser(user).orElse(null);
        if (company == null || !job.getCompany().getMaCongTy().equals(company.getMaCongTy())) {
            model.addAttribute("errorMessage", "Bạn không có quyền truy cập tin tuyển dụng này.");
            return "redirect:/employer/jobs";
        }
        
        model.addAttribute("job", job);
        model.addAttribute("workFields", workFieldService.getAllWorkFields());
        model.addAttribute("workTypes", workTypeService.getAllWorkTypes());
        model.addAttribute("locations", locationService.getProvinces());
        model.addAttribute("title", "Chỉnh sửa tin tuyển dụng");
        
        return "employer/job-form";
    }
    
    // Xử lý cập nhật công việc
    @PostMapping("/employer/jobs/{id}/update")
    public String updateJob(@PathVariable Integer id, @ModelAttribute JobDetail job, Authentication authentication, Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        
        JobDetail existingJob = jobDetailService.getJobById(id);
        if (existingJob == null) {
            model.addAttribute("errorMessage", "Không tìm thấy tin tuyển dụng.");
            return "redirect:/employer/jobs";
        }
        
        // Kiểm tra xem công việc này có thuộc về công ty của người dùng không
        Company company = companyService.getCompanyByUser(user).orElse(null);
        if (company == null || !existingJob.getCompany().getMaCongTy().equals(company.getMaCongTy())) {
            model.addAttribute("errorMessage", "Bạn không có quyền cập nhật tin tuyển dụng này.");
            return "redirect:/employer/jobs";
        }
        
        try {
            // Cập nhật các trường
            existingJob.setTieuDe(job.getTieuDe());
            existingJob.setWorkField(job.getWorkField());
            existingJob.setWorkType(job.getWorkType());
            existingJob.setLuong(job.getLuong());
            existingJob.setLoaiLuong(job.getLoaiLuong());
            existingJob.setGioBatDau(job.getGioBatDau());
            existingJob.setGioKetThuc(job.getGioKetThuc());
            existingJob.setCoTheThuongLuongGio(job.getCoTheThuongLuongGio());
            existingJob.setGioiTinhYeuCau(job.getGioiTinhYeuCau());
            existingJob.setSoLuongTuyen(job.getSoLuongTuyen());
            existingJob.setNgayLamViec(job.getNgayLamViec());
            existingJob.setThoiHanLamViec(job.getThoiHanLamViec());
            existingJob.setCoTheThuongLuongNgay(job.getCoTheThuongLuongNgay());
            existingJob.setChiTiet(job.getChiTiet());
            existingJob.setYeuCauCongViec(job.getYeuCauCongViec());
            existingJob.setQuyenLoi(job.getQuyenLoi());
            existingJob.setNgayKetThucTuyenDung(job.getNgayKetThucTuyenDung());
            
            // Chỉ cập nhật trạng thái nếu công việc chưa được duyệt
            if ("Chờ duyệt".equals(existingJob.getTrangThaiDuyet())) {
                existingJob.setTrangThaiDuyet("Chờ duyệt");
            }
            
            JobDetail updatedJob = jobDetailService.updateJob(existingJob);
            model.addAttribute("successMessage", "Cập nhật tin tuyển dụng thành công! Vui lòng chờ quản trị viên duyệt lại.");
            return "redirect:/employer/jobs";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi cập nhật tin tuyển dụng: " + e.getMessage());
            model.addAttribute("workFields", workFieldService.getAllWorkFields());
            model.addAttribute("workTypes", workTypeService.getAllWorkTypes());
            model.addAttribute("locations", locationService.getProvinces());
            model.addAttribute("job", job);
            return "employer/job-form";
        }
    }
    
    // Xóa công việc
    @PostMapping("/employer/jobs/{id}/delete")
    public String deleteJob(@PathVariable Integer id, Authentication authentication, Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        
        JobDetail job = jobDetailService.getJobById(id);
        if (job == null) {
            model.addAttribute("errorMessage", "Không tìm thấy tin tuyển dụng.");
            return "redirect:/employer/jobs";
        }
        
        // Kiểm tra xem công việc này có thuộc về công ty của người dùng không
        Company company = companyService.getCompanyByUser(user).orElse(null);
        if (company == null || !job.getCompany().getMaCongTy().equals(company.getMaCongTy())) {
            model.addAttribute("errorMessage", "Bạn không có quyền xóa tin tuyển dụng này.");
            return "redirect:/employer/jobs";
        }
        
        try {
            jobDetailService.deleteJob(id);
            model.addAttribute("successMessage", "Xóa tin tuyển dụng thành công.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi xóa tin tuyển dụng: " + e.getMessage());
        }
        
        return "redirect:/employer/jobs";
    }
    
    // Trang danh sách công việc công khai
    @GetMapping("/jobs")
    public String listJobs(Model model) {
        try {
            // Lấy tất cả các công việc đã được duyệt và còn hiệu lực (chưa hết hạn)
            List<JobDetail> activeJobs = jobDetailService.getActiveJobsWithValidDate();
            
            // Lấy tất cả công việc đã duyệt
            List<JobDetail> allApprovedJobs = jobDetailService.getJobsByTrangThaiDuyet("Đã duyệt");
            
            List<JobDetail> jobsToShow;
            
            if (!activeJobs.isEmpty()) {
                // Nếu có công việc active, dùng danh sách kết hợp
                java.util.Set<Integer> activeJobIds = new java.util.HashSet<>();
                for (JobDetail job : activeJobs) {
                    activeJobIds.add(job.getMaCongViec());
                }
                
                jobsToShow = new java.util.ArrayList<>();
                jobsToShow.addAll(activeJobs);
                
                // Thêm các job đã duyệt khác mà không trùng với active jobs
                for (JobDetail job : allApprovedJobs) {
                    if (!activeJobIds.contains(job.getMaCongViec())) {
                        jobsToShow.add(job);
                    }
                }
            } else if (!allApprovedJobs.isEmpty()) {
                // Nếu không có công việc active nhưng có công việc đã duyệt, dùng danh sách công việc đã duyệt
                jobsToShow = allApprovedJobs;
            } else {
                // Nếu không có công việc đã duyệt, trả về danh sách trống
                jobsToShow = new java.util.ArrayList<>();
            }
            
            model.addAttribute("jobs", jobsToShow);
            model.addAttribute("title", "Danh sách việc làm");
            
            // Thêm dữ liệu cho bộ lọc
            model.addAttribute("workFields", workFieldService.getAllWorkFields());
            model.addAttribute("workTypes", workTypeService.getAllWorkTypes());
            model.addAttribute("locations", locationService.getProvinces());
            
        } catch (Exception e) {
            // Trong trường hợp có lỗi, vẫn trả về trang nhưng với danh sách trống
            model.addAttribute("jobs", new java.util.ArrayList<>());
            model.addAttribute("title", "Danh sách việc làm");
            
            // Thêm dữ liệu cho bộ lọc để tránh lỗi
            model.addAttribute("workFields", workFieldService.getAllWorkFields());
            model.addAttribute("workTypes", workTypeService.getAllWorkTypes());
            model.addAttribute("locations", locationService.getProvinces());
        }
        
        return "public/jobs";
    }
    
    // Trang chi tiết công việc
    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable Integer id, Model model) {
        JobDetail job = jobDetailService.getJobById(id);
        if (job == null || !"Đã duyệt".equals(job.getTrangThaiDuyet())) {
            // Chỉ hiển thị công việc đã được duyệt
            model.addAttribute("errorMessage", "Công việc không tồn tại hoặc chưa được duyệt.");
            return "public/jobs";
        }
        
        // Tăng số lượt xem
        jobDetailService.incrementViewCount(job);
        
        model.addAttribute("job", job);
        model.addAttribute("title", job.getTieuDe());
        return "public/job-detail";
    }
}