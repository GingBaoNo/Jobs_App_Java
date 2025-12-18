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

    @Autowired
    private WorkDisciplineService workDisciplineService;

    @Autowired
    private JobPositionService jobPositionService;

    @Autowired
    private ExperienceLevelService experienceLevelService;
    
    // Trang danh sách công việc của nhà tuyển dụng
    @GetMapping("/employer/jobs")
    public String employerJobs(@RequestParam(value = "search", required = false) String search, Authentication authentication, Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Company company = companyService.getCompanyByUser(user).orElse(null);
        if (company == null) {
            model.addAttribute("errorMessage", "Bạn cần đăng ký công ty trước khi tạo tin tuyển dụng.");
            return "employer/jobs";
        }

        List<JobDetail> jobs;
        if (search != null && !search.trim().isEmpty()) {
            jobs = jobDetailService.getJobsByCompanyAndTitleContaining(company, search);
        } else {
            jobs = jobDetailService.getJobsByCompany(company);
        }

        model.addAttribute("jobs", jobs);
        model.addAttribute("title", "Quản lý tin tuyển dụng");
        model.addAttribute("searchQuery", search != null ? search : "");

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
        model.addAttribute("workDisciplines", workDisciplineService.getAllWorkDisciplines());
        model.addAttribute("jobPositions", jobPositionService.getAllJobPositions());
        model.addAttribute("experienceLevels", experienceLevelService.getAllExperienceLevels());
        model.addAttribute("locations", locationService.getProvinces());
        model.addAttribute("title", "Tạo tin tuyển dụng mới");

        return "employer/job-form";
    }
    
    // Xử lý tạo công việc mới
    @PostMapping("/employer/jobs/create")
    public String createJob(@ModelAttribute JobDetail job,
                           @RequestParam(required = false) Integer maNganh,
                           @RequestParam(required = false) Integer maViTri,
                           @RequestParam(required = false) Integer maCapDoKinhNghiem,
                           Authentication authentication, Model model) {
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

            // Gán các trường phân cấp nếu được cung cấp
            if (maNganh != null) {
                WorkDiscipline workDiscipline = workDisciplineService.getWorkDisciplineById(maNganh).orElse(null);
                if (workDiscipline != null) {
                    job.setJobPosition(null); // Clear any existing position that might not match the discipline
                }
            }

            if (maViTri != null) {
                JobPosition jobPosition = jobPositionService.getJobPositionById(maViTri).orElse(null);
                if (jobPosition != null) {
                    job.setJobPosition(jobPosition);
                }
            }

            if (maCapDoKinhNghiem != null) {
                ExperienceLevel experienceLevel = experienceLevelService.getExperienceLevelById(maCapDoKinhNghiem).orElse(null);
                if (experienceLevel != null) {
                    job.setExperienceLevel(experienceLevel);
                }
            }

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
            model.addAttribute("workDisciplines", workDisciplineService.getAllWorkDisciplines());
            model.addAttribute("jobPositions", jobPositionService.getAllJobPositions());
            model.addAttribute("experienceLevels", experienceLevelService.getAllExperienceLevels());
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
        model.addAttribute("workDisciplines", workDisciplineService.getAllWorkDisciplines());
        model.addAttribute("jobPositions", jobPositionService.getAllJobPositions());
        model.addAttribute("experienceLevels", experienceLevelService.getAllExperienceLevels());
        model.addAttribute("locations", locationService.getProvinces());
        model.addAttribute("title", "Chỉnh sửa tin tuyển dụng");

        return "employer/job-form";
    }
    
    // Xử lý cập nhật công việc
    @PostMapping("/employer/jobs/{id}/update")
    public String updateJob(@PathVariable Integer id,
                           @ModelAttribute JobDetail job,
                           @RequestParam(required = false) Integer maNganh,
                           @RequestParam(required = false) Integer maViTri,
                           @RequestParam(required = false) Integer maCapDoKinhNghiem,
                           Authentication authentication, Model model) {
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

            // Cập nhật các trường phân cấp
            if (maNganh != null) {
                WorkDiscipline workDiscipline = workDisciplineService.getWorkDisciplineById(maNganh).orElse(null);
                if (workDiscipline != null) {
                    existingJob.setJobPosition(null); // Clear any existing position that might not match the discipline
                } else {
                    existingJob.setJobPosition(null);
                }
            } else {
                existingJob.setJobPosition(null);
            }

            if (maViTri != null) {
                JobPosition jobPosition = jobPositionService.getJobPositionById(maViTri).orElse(null);
                if (jobPosition != null) {
                    // Make sure the position belongs to the selected discipline
                    if (maNganh == null || jobPosition.getWorkDiscipline().getMaNganh().equals(maNganh)) {
                        existingJob.setJobPosition(jobPosition);
                    }
                }
            } else {
                existingJob.setJobPosition(null);
            }

            if (maCapDoKinhNghiem != null) {
                ExperienceLevel experienceLevel = experienceLevelService.getExperienceLevelById(maCapDoKinhNghiem).orElse(null);
                if (experienceLevel != null) {
                    existingJob.setExperienceLevel(experienceLevel);
                }
            } else {
                existingJob.setExperienceLevel(null);
            }

            // Bảo toàn giá trị ngày hết hạn tuyển dụng
            // Nếu form gửi giá trị mới, sử dụng giá trị mới
            // Nếu form không gửi giá trị (null), giữ nguyên giá trị cũ
            LocalDate newEndDate = job.getNgayKetThucTuyenDung();
            if (newEndDate != null) {
                existingJob.setNgayKetThucTuyenDung(newEndDate);
            } else {
                // Nếu người dùng không chọn ngày mới, giữ nguyên giá trị cũ từ DB
                // Không thay đổi ngày hết hạn hiện tại
            }

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
            model.addAttribute("workDisciplines", workDisciplineService.getAllWorkDisciplines());
            model.addAttribute("jobPositions", jobPositionService.getAllJobPositions());
            model.addAttribute("experienceLevels", experienceLevelService.getAllExperienceLevels());
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
    public String listJobs(@RequestParam(value = "search", required = false) String search,
                          @RequestParam(value = "field", required = false) Integer fieldId,
                          @RequestParam(value = "discipline", required = false) Integer disciplineId,
                          @RequestParam(value = "position", required = false) Integer positionId,
                          @RequestParam(value = "experience", required = false) Integer experienceId,
                          @RequestParam(value = "type", required = false) Integer typeId,
                          Model model) {
        try {
            List<JobDetail> jobsToShow;

            // Nếu có tìm kiếm hoặc lọc, sử dụng phương thức tìm kiếm chuyên sâu
            if (search != null || fieldId != null || disciplineId != null ||
                positionId != null || experienceId != null || typeId != null) {

                // Lấy tất cả các công việc đã được duyệt và còn hiệu lực
                jobsToShow = jobDetailService.searchJobsByCombinedCriteriaWithNewHierarchy(
                    search,
                    fieldId,
                    disciplineId,
                    positionId,
                    experienceId,
                    typeId
                );
            } else {
                // Nếu không có điều kiện lọc, lấy tất cả công việc đã được duyệt
                List<JobDetail> activeJobs = jobDetailService.getActiveJobsWithValidDate();
                List<JobDetail> allApprovedJobs = jobDetailService.getJobsByTrangThaiDuyet("Đã duyệt");

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
            }

            model.addAttribute("jobs", jobsToShow);
            model.addAttribute("title", "Danh sách việc làm");

            // Truyền tham số tìm kiếm để giữ lại trên giao diện
            model.addAttribute("searchQuery", search);
            model.addAttribute("selectedFieldId", fieldId);
            model.addAttribute("selectedDisciplineId", disciplineId);
            model.addAttribute("selectedPositionId", positionId);
            model.addAttribute("selectedExperienceId", experienceId);
            model.addAttribute("selectedTypeId", typeId);

            // Thêm dữ liệu cho bộ lọc
            model.addAttribute("workFields", workFieldService.getAllWorkFields());
            model.addAttribute("workTypes", workTypeService.getAllWorkTypes());
            model.addAttribute("workDisciplines", workDisciplineService.getAllWorkDisciplines());
            model.addAttribute("jobPositions", jobPositionService.getAllJobPositions());
            model.addAttribute("experienceLevels", experienceLevelService.getAllExperienceLevels());
            model.addAttribute("locations", locationService.getProvinces());

            // Thêm dữ liệu cho sidebar
            // 1. Việc làm theo lĩnh vực (top 5 lĩnh vực có nhiều việc làm nhất)
            java.util.Map<WorkField, Long> jobsByField = jobsToShow.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    JobDetail::getWorkField,
                    java.util.stream.Collectors.counting()
                ));

            java.util.List<java.util.Map.Entry<WorkField, Long>> topFields = jobsByField.entrySet().stream()
                .sorted(java.util.Map.Entry.<WorkField, Long>comparingByValue().reversed())
                .limit(5)
                .collect(java.util.ArrayList::new, java.util.ArrayList::add, java.util.ArrayList::addAll);
            model.addAttribute("topFields", topFields);

            // 2. Việc làm theo ngành (top 5 ngành có nhiều việc làm nhất)
            java.util.Map<WorkDiscipline, Long> jobsByDiscipline = jobsToShow.stream()
                .filter(job -> job.getJobPosition() != null && job.getJobPosition().getWorkDiscipline() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                    job -> job.getJobPosition().getWorkDiscipline(),
                    java.util.stream.Collectors.counting()
                ));

            java.util.List<java.util.Map.Entry<WorkDiscipline, Long>> topDisciplines = jobsByDiscipline.entrySet().stream()
                .sorted(java.util.Map.Entry.<WorkDiscipline, Long>comparingByValue().reversed())
                .limit(5)
                .collect(java.util.ArrayList::new, java.util.ArrayList::add, java.util.ArrayList::addAll);
            model.addAttribute("topDisciplines", topDisciplines);

            // 3. Việc làm theo vị trí (top 5 vị trí có nhiều việc làm nhất)
            java.util.Map<JobPosition, Long> jobsByPosition = jobsToShow.stream()
                .filter(job -> job.getJobPosition() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                    JobDetail::getJobPosition,
                    java.util.stream.Collectors.counting()
                ));

            java.util.List<java.util.Map.Entry<JobPosition, Long>> topPositions = jobsByPosition.entrySet().stream()
                .sorted(java.util.Map.Entry.<JobPosition, Long>comparingByValue().reversed())
                .limit(5)
                .collect(java.util.ArrayList::new, java.util.ArrayList::add, java.util.ArrayList::addAll);
            model.addAttribute("topPositions", topPositions);

            // 4. Việc làm theo địa điểm (top 5 địa điểm có nhiều việc làm nhất)
            // Lấy địa điểm từ công ty (vì công việc không có địa điểm riêng trong model hiện tại)
            java.util.Map<String, Long> jobsByLocation = jobsToShow.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    job -> job.getCompany().getDiaChi(),
                    java.util.stream.Collectors.counting()
                ));

            java.util.List<java.util.Map.Entry<String, Long>> topLocations = jobsByLocation.entrySet().stream()
                .sorted(java.util.Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(java.util.ArrayList::new, java.util.ArrayList::add, java.util.ArrayList::addAll);
            model.addAttribute("topLocations", topLocations);

            // 5. Thống kê
            long totalJobs = jobsToShow.size();
            long totalCompanies = jobsToShow.stream()
                .map(job -> job.getCompany().getMaCongTy())
                .distinct()
                .count();
            long totalApplications = 0; // Bạn có thể thêm logic tính số lượng ứng tuyển

            model.addAttribute("totalJobs", totalJobs);
            model.addAttribute("totalCompanies", totalCompanies);
            model.addAttribute("totalApplications", totalApplications);

            // 6. Công ty nổi bật (các công ty có nhiều việc làm nhất)
            java.util.Map<Company, Long> companiesByJobCount = jobsToShow.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    JobDetail::getCompany,
                    java.util.stream.Collectors.counting()
                ));

            java.util.List<java.util.Map.Entry<Company, Long>> topCompanies = companiesByJobCount.entrySet().stream()
                .sorted(java.util.Map.Entry.<Company, Long>comparingByValue().reversed())
                .limit(3)
                .collect(java.util.ArrayList::new, java.util.ArrayList::add, java.util.ArrayList::addAll);
            model.addAttribute("topCompanies", topCompanies);

        } catch (Exception e) {
            // Trong trường hợp có lỗi, vẫn trả về trang nhưng với danh sách trống
            e.printStackTrace(); // Log lỗi để dễ debug
            model.addAttribute("jobs", new java.util.ArrayList<>());
            model.addAttribute("title", "Danh sách việc làm");

            // Thêm dữ liệu cho bộ lọc để tránh lỗi
            model.addAttribute("workFields", workFieldService.getAllWorkFields());
            model.addAttribute("workTypes", workTypeService.getAllWorkTypes());
            model.addAttribute("workDisciplines", workDisciplineService.getAllWorkDisciplines());
            model.addAttribute("jobPositions", jobPositionService.getAllJobPositions());
            model.addAttribute("experienceLevels", experienceLevelService.getAllExperienceLevels());
            model.addAttribute("locations", locationService.getProvinces());

            // Dữ liệu mặc định tránh lỗi
            model.addAttribute("topFields", new java.util.ArrayList<>());
            model.addAttribute("topDisciplines", new java.util.ArrayList<>());
            model.addAttribute("topPositions", new java.util.ArrayList<>());
            model.addAttribute("topLocations", new java.util.ArrayList<>());
            model.addAttribute("totalJobs", 0);
            model.addAttribute("totalCompanies", 0);
            model.addAttribute("totalApplications", 0);
            model.addAttribute("topCompanies", new java.util.ArrayList<>());
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