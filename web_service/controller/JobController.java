package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

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
    private WorkDisciplineService workDisciplineService;

    @Autowired
    private JobPositionService jobPositionService;

    @Autowired
    private CvProfileService cvProfileService;

    @Autowired
    private ExperienceLevelService experienceLevelService;
    
    // API endpoint to get company coordinates
    @GetMapping("/api/employer/company-coordinates")
    public ResponseEntity<?> getCompanyCoordinates(Authentication authentication) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        Company company = companyService.getCompanyByUser(user).orElse(null);
        if (company == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Company not found"));
        }

        Map<String, Object> coordinates = new HashMap<>();
        coordinates.put("kinhDo", company.getKinhDo());
        coordinates.put("viDo", company.getViDo());
        coordinates.put("diaChi", company.getDiaChi());

        return ResponseEntity.ok(coordinates);
    }

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
        model.addAttribute("title", "Tạo tin tuyển dụng mới");

        return "employer/job-form";
    }
    
    // Xử lý tạo công việc mới
    @PostMapping("/employer/jobs/create")
    public String createJob(@ModelAttribute JobDetail job,
                           @RequestParam(required = false) Integer maNganh,
                           @RequestParam(required = false) Integer maViTri,
                           @RequestParam(required = false) Integer maCapDoKinhNghiem,
                           @RequestParam(required = false) Double kinhDo,
                           @RequestParam(required = false) Double viDo,
                           @RequestParam(required = false) String jobAddress,
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
                WorkDiscipline workDiscipline = workDisciplineService.getWorkDisciplineById(maNganh);
                if (workDiscipline != null) {
                    job.setJobPosition(null); // Clear any existing position that might not match the discipline
                }
            }

            if (maViTri != null) {
                JobPosition jobPosition = jobPositionService.getJobPositionById(maViTri);
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

            // Gán tọa độ nếu được cung cấp
            if (kinhDo != null && viDo != null) {
                job.setKinhDo(java.math.BigDecimal.valueOf(kinhDo));
                job.setViDo(java.math.BigDecimal.valueOf(viDo));
            } else {
                // Nếu không có tọa độ cho công việc cụ thể, sử dụng tọa độ từ công ty
                if (company.getKinhDo() != null && company.getViDo() != null) {
                    job.setKinhDo(company.getKinhDo());
                    job.setViDo(company.getViDo());
                }
            }

            // Cập nhật địa chỉ công việc nếu được cung cấp
            if (jobAddress != null && !jobAddress.trim().isEmpty()) {
                // Có thể lưu vào một trường trong DB nếu cần, hoặc xử lý theo yêu cầu
                // Hiện tại, chúng ta có thể lưu vào một trường tạm thời hoặc ghi chú
                // Tùy vào yêu cầu kinh doanh, có thể cần thêm trường vào DB
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
                           @RequestParam(required = false) Double kinhDo,
                           @RequestParam(required = false) Double viDo,
                           @RequestParam(required = false) String jobAddress,
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
                WorkDiscipline workDiscipline = workDisciplineService.getWorkDisciplineById(maNganh);
                if (workDiscipline != null) {
                    existingJob.setJobPosition(null); // Clear any existing position that might not match the discipline
                } else {
                    existingJob.setJobPosition(null);
                }
            } else {
                existingJob.setJobPosition(null);
            }

            if (maViTri != null) {
                JobPosition jobPosition = jobPositionService.getJobPositionById(maViTri);
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

            // Cập nhật tọa độ nếu được cung cấp
            if (kinhDo != null && viDo != null) {
                existingJob.setKinhDo(java.math.BigDecimal.valueOf(kinhDo));
                existingJob.setViDo(java.math.BigDecimal.valueOf(viDo));
            } else {
                // Nếu không có tọa độ mới, giữ nguyên tọa độ hiện tại hoặc lấy từ công ty
                if (existingJob.getKinhDo() == null && existingJob.getViDo() == null) {
                    // Nếu công việc chưa có tọa độ, lấy từ công ty
                    if (company.getKinhDo() != null && company.getViDo() != null) {
                        existingJob.setKinhDo(company.getKinhDo());
                        existingJob.setViDo(company.getViDo());
                    }
                }
                // Nếu công việc đã có tọa độ, giữ nguyên (không thay đổi)
            }

            // Cập nhật địa chỉ công việc nếu được cung cấp
            if (jobAddress != null && !jobAddress.trim().isEmpty()) {
                // Có thể lưu vào một trường trong DB nếu cần, hoặc xử lý theo yêu cầu
                // Hiện tại, chúng ta có thể lưu vào một trường tạm thời hoặc ghi chú
                // Tùy vào yêu cầu kinh doanh, có thể cần thêm trường vào DB
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
    public String listJobs(
                          @RequestParam(value = "search", required = false) String search,
                          // Nhận cả 'field' và 'fieldId' để tương thích API
                          @RequestParam(value = "field", required = false) Integer field,
                          @RequestParam(value = "fieldId", required = false) Integer fieldIdParam,
                          @RequestParam(value = "discipline", required = false) Integer disciplineId,
                          @RequestParam(value = "position", required = false) Integer positionId,
                          @RequestParam(value = "experience", required = false) Integer experienceId,
                          @RequestParam(value = "type", required = false) Integer typeId,
                          @RequestParam(value = "minSalary", required = false) Integer minSalary,
                          @RequestParam(value = "maxSalary", required = false) Integer maxSalary,
                          Model model) {
        try {
            List<JobDetail> jobsToShow;
            
            // Ưu tiên fieldId từ API, nếu không có thì dùng field từ form
            Integer finalFieldId = (fieldIdParam != null) ? fieldIdParam : field;

            System.out.println("=== DEBUG FILTER ===");
            System.out.println("search: " + search);
            System.out.println("field/fieldId: " + finalFieldId);
            System.out.println("discipline: " + disciplineId);
            System.out.println("position: " + positionId);
            System.out.println("experience: " + experienceId);
            System.out.println("type: " + typeId);
            System.out.println("minSalary: " + minSalary);
            System.out.println("maxSalary: " + maxSalary);

            // Nếu có tìm kiếm hoặc lọc, sử dụng phương thức tìm kiếm chuyên sâu
            if (search != null || finalFieldId != null || disciplineId != null ||
                positionId != null || experienceId != null || typeId != null) {

                System.out.println("Using AdvancedSearch method...");

                // Sử dụng CÙNG method với API (/api/v1/advanced-search/jobs)
                // Dùng searchJobsAdvancedWithPaging nhưng không phân trang (page=0, size=1000)
                org.springframework.data.domain.Pageable pageable = 
                    org.springframework.data.domain.PageRequest.of(0, 1000);
                
                org.springframework.data.domain.Page<JobDetail> jobPage = 
                    jobDetailService.searchJobsAdvancedWithPaging(
                        search,
                        finalFieldId,
                        disciplineId,
                        positionId,
                        experienceId,
                        typeId,
                        null, // minSalary (đã loại bỏ)
                        null, // maxSalary (đã loại bỏ)
                        pageable
                    );
                
                jobsToShow = jobPage.getContent();

                System.out.println("After advanced search: " + jobsToShow.size() + " jobs");
                System.out.println("Found " + jobsToShow.size() + " jobs");
            } else {
                // Nếu không có điều kiện lọc, lấy TẤT CẢ công việc (không filter trạng thái)
                System.out.println("Using getAllJobs (no filter)...");
                jobsToShow = jobDetailService.getAllJobs();
                System.out.println("Total jobs: " + jobsToShow.size());
            }

            System.out.println("=== END DEBUG ===");

            model.addAttribute("jobs", jobsToShow);
            model.addAttribute("title", "Danh sách việc làm");

            // Truyền tham số tìm kiếm để giữ lại trên giao diện
            model.addAttribute("searchQuery", search);
            model.addAttribute("selectedFieldId", finalFieldId);
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
    public String jobDetail(@PathVariable Integer id, Model model, Authentication authentication) {
        JobDetail job = jobDetailService.getJobById(id);
        if (job == null || !"Đã duyệt".equals(job.getTrangThaiDuyet())) {
            // Chỉ hiển thị công việc đã được duyệt
            model.addAttribute("errorMessage", "Công việc không tồn tại hoặc chưa được duyệt.");
            return "public/jobs";
        }

        // Tăng số lượt xem
        jobDetailService.incrementViewCount(job);

        // Lấy các công việc liên quan (cùng lĩnh vực và công ty)
        List<JobDetail> relatedJobs = jobDetailService.getRelatedJobs(job, 6); // Lấy tối đa 6 công việc liên quan

        model.addAttribute("job", job);
        model.addAttribute("relatedJobs", relatedJobs);
        model.addAttribute("title", job.getTieuDe());
        
        // Nếu user đã đăng nhập và là ứng viên, lấy danh sách CV
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            User user = userService.getUserByTaiKhoan(username).orElse(null);
            if (user != null && "NV".equals(user.getRole().getTenVaiTro())) {
                List<CvProfile> cvProfiles = cvProfileService.getAllCvProfilesByUser(user);
                model.addAttribute("cvProfiles", cvProfiles);
            }
        }
        
        return "public/job-detail";
    }

    // Endpoint cập nhật trạng thái tin tuyển dụng cho nhà tuyển dụng
    @PostMapping("/employer/jobs/{id}/toggle-status")
    public String toggleJobStatus(@PathVariable Integer id,
                                  @RequestParam String newStatus,
                                  Authentication authentication,
                                  Model model) {
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
            model.addAttribute("errorMessage", "Bạn không có quyền cập nhật trạng thái tin tuyển dụng này.");
            return "redirect:/employer/jobs";
        }

        // Kiểm tra trạng thái hợp lệ
        if (!"Mở".equals(newStatus) && !"Đã đóng".equals(newStatus) && !"Tạm dừng".equals(newStatus)) {
            model.addAttribute("errorMessage", "Trạng thái không hợp lệ.");
            return "redirect:/employer/jobs";
        }

        try {
            // Cập nhật trạng thái
            jobDetailService.updateJobStatus(id, newStatus);
            model.addAttribute("successMessage", "Cập nhật trạng thái tin tuyển dụng thành công.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }

        return "redirect:/employer/jobs";
    }
}