package com.example.demo.controller.api;

import com.example.demo.entity.AppliedJob;
import com.example.demo.entity.Company;
import com.example.demo.entity.JobDetail;
import com.example.demo.entity.Profile;
import com.example.demo.entity.User;
import com.example.demo.service.AppliedJobService;
import com.example.demo.service.JobDetailService;
import com.example.demo.service.ProfileService;
import com.example.demo.service.UserService;
import com.example.demo.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/applied-jobs")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiAppliedJobController {

    @Autowired
    private AppliedJobService appliedJobService;

    @Autowired
    private UserService userService;

    @Autowired
    private JobDetailService jobDetailService;

    @Autowired
    private ProfileService profileService;

    // Ứng tuyển vào công việc
    @PostMapping
    public ResponseEntity<?> applyForJob(@RequestBody AppliedJobRequest request) {
        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponseUtil.error("User not authenticated");
        }

        String username = authentication.getName();
        Optional<User> user = userService.getUserByTaiKhoan(username);
        if (!user.isPresent()) {
            return ApiResponseUtil.error("User not found");
        }

        JobDetail jobDetail = jobDetailService.getJobById(request.getJobDetailId());
        if (jobDetail == null) {
            return ApiResponseUtil.error("Job detail not found with id: " + request.getJobDetailId());
        }

        try {
            AppliedJob appliedJob = appliedJobService.applyForJob(user.get(), jobDetail);
            return ApiResponseUtil.created(appliedJob);
        } catch (RuntimeException e) {
            return ApiResponseUtil.error(e.getMessage());
        }
    }

    // Lấy các công việc đã ứng tuyển của người dùng hiện tại
    // @GetMapping("/my-applications")
    // public ResponseEntity<?> getMyApplications() {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
    //         return ApiResponseUtil.error("User not authenticated");
    //     }

    //     String username = authentication.getName();
    //     Optional<User> user = userService.getUserByTaiKhoan(username);
    //     if (!user.isPresent()) {
    //         return ApiResponseUtil.error("User not found");
    //     }

    //     List<AppliedJob> appliedJobs = appliedJobService.getAppliedJobsByEmployee(user.get());
    //     return ApiResponseUtil.success("My applications retrieved successfully", appliedJobs);
    // }

    // Hủy ứng tuyển
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelApplication(@PathVariable Integer id) {
        Optional<AppliedJob> appliedJobOpt = appliedJobService.getAppliedJobById(id);
        if (!appliedJobOpt.isPresent()) {
            return ApiResponseUtil.error("Applied job not found with id: " + id);
        }

        AppliedJob appliedJob = appliedJobOpt.get();

        // Kiểm tra quyền: chỉ người đã ứng tuyển mới có thể hủy
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponseUtil.error("User not authenticated");
        }

        String username = authentication.getName();
        Optional<User> user = userService.getUserByTaiKhoan(username);
        if (!user.isPresent()) {
            return ApiResponseUtil.error("User not found");
        }

        if (!appliedJob.getEmployee().getMaNguoiDung().equals(user.get().getMaNguoiDung())) {
            return ApiResponseUtil.error("You don't have permission to cancel this application");
        }

        appliedJobService.deleteAppliedJob(id);
        return ApiResponseUtil.noContent();
    }

    // Ứng tuyển vào công việc với CV
    @PostMapping("/apply-with-cv")
    public ResponseEntity<?> applyForJobWithCv(
            @RequestParam Integer jobDetailId,
            @RequestParam(required = false) String coverLetter,
            @RequestParam("cvFile") org.springframework.web.multipart.MultipartFile cvFile) {

        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponseUtil.error("User not authenticated");
        }

        String username = authentication.getName();
        Optional<User> user = userService.getUserByTaiKhoan(username);
        if (!user.isPresent()) {
            return ApiResponseUtil.error("User not found");
        }

        JobDetail jobDetail = jobDetailService.getJobById(jobDetailId);
        if (jobDetail == null) {
            return ApiResponseUtil.error("Job detail not found with id: " + jobDetailId);
        }

        // Xử lý upload CV file
        String cvUrl = null;
        try {
            String uploadDir = "uploads/cvs/";
            String fileName = cvFile.getOriginalFilename();
            if (fileName != null) {
                String savedFileName = com.example.demo.utils.FileUploadUtil.saveFile(uploadDir, fileName, cvFile);
                cvUrl = "/uploads/cvs/" + savedFileName;
            }
        } catch (Exception e) {
            return ApiResponseUtil.error("Error uploading CV file: " + e.getMessage());
        }

        try {
            AppliedJob appliedJob = appliedJobService.applyForJobWithCv(user.get(), jobDetail, cvUrl);
            return ApiResponseUtil.created(appliedJob);
        } catch (RuntimeException e) {
            return ApiResponseUtil.error(e.getMessage());
        }
    }

    // Cập nhật trạng thái ứng tuyển (dành cho nhà tuyển dụng)
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateApplicationStatus(@PathVariable Integer id, @RequestBody StatusUpdateRequest request) {
        Optional<AppliedJob> appliedJobOpt = appliedJobService.getAppliedJobById(id);
        if (!appliedJobOpt.isPresent()) {
            return ApiResponseUtil.error("Applied job not found with id: " + id);
        }

        AppliedJob appliedJob = appliedJobOpt.get();

        // Kiểm tra quyền: chỉ nhà tuyển dụng của công ty đăng công việc mới có thể cập nhật trạng thái
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponseUtil.error("User not authenticated");
        }

        String username = authentication.getName();
        Optional<User> user = userService.getUserByTaiKhoan(username);
        if (!user.isPresent()) {
            return ApiResponseUtil.error("User not found");
        }

        // Kiểm tra xem người dùng có phải là nhà tuyển dụng của công ty đăng công việc không
        if (!appliedJob.getJobDetail().getCompany().getUser().getMaNguoiDung().equals(user.get().getMaNguoiDung())) {
            return ApiResponseUtil.error("You don't have permission to update this application status");
        }

        appliedJob.setTrangThaiUngTuyen(request.getStatus());
        if (request.getRating() != null) {
            appliedJob.setDanhGiaNtd(request.getRating());
        }

        AppliedJob updatedAppliedJob = appliedJobService.updateAppliedJob(appliedJob);

        // Trả về thông tin đầy đủ tương tự như getApplicantCv
        try {
            Map<String, Object> response = new java.util.HashMap<>();

            // Xử lý fallback CV: nếu không có trong applied_job, lấy từ profile
            String cvUrl = updatedAppliedJob.getUrlCvUngTuyen();
            if (cvUrl == null || cvUrl.isEmpty()) {
                // Lấy từ profile nếu có
                Optional<Profile> profileOpt = profileService.getProfileByUser(updatedAppliedJob.getEmployee());
                if (profileOpt.isPresent() && profileOpt.get().getUrlCv() != null) {
                    cvUrl = profileOpt.get().getUrlCv();
                }
            }

            response.put("cvUrl", cvUrl);
            response.put("trangThaiUngTuyen", updatedAppliedJob.getTrangThaiUngTuyen());
            response.put("danhGiaNtd", updatedAppliedJob.getDanhGiaNtd());
            response.put("ngayUngTuyen", updatedAppliedJob.getNgayUngTuyen());

            // Thêm thông tin chi tiết của người ứng tuyển
            Map<String, Object> employeeInfo = new java.util.HashMap<>();
            if (updatedAppliedJob.getEmployee() != null) {
                employeeInfo.put("maNguoiDung", updatedAppliedJob.getEmployee().getMaNguoiDung());
                employeeInfo.put("tenHienThi", updatedAppliedJob.getEmployee().getTenHienThi());
                employeeInfo.put("taiKhoan", updatedAppliedJob.getEmployee().getTaiKhoan());
                employeeInfo.put("lienHe", updatedAppliedJob.getEmployee().getLienHe());
            }
            response.put("employee", employeeInfo);

            // Thêm thông tin chi tiết của công việc
            Map<String, Object> jobDetailInfo = new java.util.HashMap<>();
            if (updatedAppliedJob.getJobDetail() != null) {
                jobDetailInfo.put("maCongViec", updatedAppliedJob.getJobDetail().getMaCongViec());
                jobDetailInfo.put("tieuDe", updatedAppliedJob.getJobDetail().getTieuDe());
                jobDetailInfo.put("chiTiet", updatedAppliedJob.getJobDetail().getChiTiet());
                jobDetailInfo.put("yeuCauCongViec", updatedAppliedJob.getJobDetail().getYeuCauCongViec());
                jobDetailInfo.put("quyenLoi", updatedAppliedJob.getJobDetail().getQuyenLoi());
                jobDetailInfo.put("luong", updatedAppliedJob.getJobDetail().getLuong());
                jobDetailInfo.put("loaiLuong", updatedAppliedJob.getJobDetail().getLoaiLuong());
                jobDetailInfo.put("trangThaiDuyet", updatedAppliedJob.getJobDetail().getTrangThaiDuyet());
                jobDetailInfo.put("trangThaiTinTuyen", updatedAppliedJob.getJobDetail().getTrangThaiTinTuyen());
            }
            response.put("jobDetail", jobDetailInfo);

            return ApiResponseUtil.success("Application status updated successfully", response);
        } catch (Exception e) {
            return ApiResponseUtil.error("Error updating application status: " + e.getMessage());
        }
    }

    // Lấy danh sách công việc đã ứng tuyển (dành cho người xin việc)
    @GetMapping("/my-applications")
    public ResponseEntity<?> getMyApplications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponseUtil.error("User not authenticated");
        }

        String username = authentication.getName();
        Optional<User> user = userService.getUserByTaiKhoan(username);
        if (!user.isPresent()) {
            return ApiResponseUtil.error("User not found");
        }

        try {
            List<AppliedJob> appliedJobs = appliedJobService.getAppliedJobsByEmployee(user.get());
            // Tạo response với thông tin chi tiết hơn
            List<Map<String, Object>> result = appliedJobs.stream().map(job -> {
                Map<String, Object> jobInfo = new java.util.HashMap<>();
                jobInfo.put("maUngTuyen", job.getMaUngTuyen());
                jobInfo.put("trangThaiUngTuyen", job.getTrangThaiUngTuyen());
                jobInfo.put("danhGiaNtd", job.getDanhGiaNtd());
                jobInfo.put("ngayUngTuyen", job.getNgayUngTuyen());
                jobInfo.put("urlCvUngTuyen", job.getUrlCvUngTuyen());

                // Thêm thông tin công việc
                JobDetail jobDetail = job.getJobDetail();
                jobInfo.put("jobDetail", Map.of(
                    "maCongViec", jobDetail.getMaCongViec(),
                    "tieuDe", jobDetail.getTieuDe(),
                    "chiTiet", jobDetail.getChiTiet(),
                    "yeuCauCongViec", jobDetail.getYeuCauCongViec(),
                    "quyenLoi", jobDetail.getQuyenLoi(),
                    "luong", jobDetail.getLuong(),
                    "loaiLuong", jobDetail.getLoaiLuong(),
                    "trangThaiDuyet", jobDetail.getTrangThaiDuyet(),
                    "trangThaiTinTuyen", jobDetail.getTrangThaiTinTuyen()
                ));

                // Thêm thông tin công ty
                Company company = jobDetail.getCompany();
                jobInfo.put("company", Map.of(
                    "maCongTy", company.getMaCongTy(),
                    "tenCongTy", company.getTenCongTy(),
                    "diaChi", company.getDiaChi(),
                    "lienHeCty", company.getLienHeCty()
                ));

                return jobInfo;
            }).toList();

            return ApiResponseUtil.success("My applications retrieved successfully", result);
        } catch (Exception e) {
            return ApiResponseUtil.error("Error retrieving applications: " + e.getMessage());
        }
    }

    // Lấy danh sách ứng viên cho công việc cụ thể (dành cho nhà tuyển dụng)
    @GetMapping("/for-job/{jobId}")
    public ResponseEntity<?> getApplicantsForJob(@PathVariable Integer jobId) {
        JobDetail jobDetail = jobDetailService.getJobById(jobId);
        if (jobDetail == null) {
            return ApiResponseUtil.error("Job detail not found with id: " + jobId);
        }

        // Kiểm tra quyền: chỉ nhà tuyển dụng của công ty đăng công việc mới có thể xem ứng viên
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponseUtil.error("User not authenticated");
        }

        String username = authentication.getName();
        Optional<User> user = userService.getUserByTaiKhoan(username);
        if (!user.isPresent()) {
            return ApiResponseUtil.error("User not found");
        }

        if (!jobDetail.getCompany().getUser().getMaNguoiDung().equals(user.get().getMaNguoiDung())) {
            return ApiResponseUtil.error("You don't have permission to view applicants for this job");
        }

        try {
            List<AppliedJob> appliedJobs = appliedJobService.getAppliedJobsByJobDetail(jobDetail);
            return ApiResponseUtil.success("Applicants retrieved successfully", appliedJobs);
        } catch (Exception e) {
            return ApiResponseUtil.error("Error retrieving applicants: " + e.getMessage());
        }
    }

    // Lấy danh sách công việc đã ứng tuyển với trạng thái cụ thể (Android API)
    @GetMapping("/my-applications/status/{status}")
    public ResponseEntity<?> getMyApplicationsByStatus(@PathVariable String status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponseUtil.error("User not authenticated");
        }

        String username = authentication.getName();
        Optional<User> user = userService.getUserByTaiKhoan(username);
        if (!user.isPresent()) {
            return ApiResponseUtil.error("User not found");
        }

        List<AppliedJob> appliedJobs = appliedJobService.getAppliedJobsByEmployee(user.get()).stream()
            .filter(job -> job.getTrangThaiUngTuyen().equals(status))
            .toList();

        return ApiResponseUtil.success("My applications with status " + status + " retrieved successfully", appliedJobs);
    }

    // Lấy danh sách công việc đã ứng tuyển (Android API - cho cả trạng thái)
    @GetMapping("/my-applications-all")
    public ResponseEntity<?> getMyApplicationsAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponseUtil.error("User not authenticated");
        }

        String username = authentication.getName();
        Optional<User> user = userService.getUserByTaiKhoan(username);
        if (!user.isPresent()) {
            return ApiResponseUtil.error("User not found");
        }

        List<AppliedJob> appliedJobs = appliedJobService.getAppliedJobsByEmployee(user.get());
        return ApiResponseUtil.success("All my applications retrieved successfully", appliedJobs);
    }

    // Lấy danh sách công việc đã được duyệt (Android API)
    @GetMapping("/my-applications-approved")
    public ResponseEntity<?> getMyApprovedApplications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponseUtil.error("User not authenticated");
        }

        String username = authentication.getName();
        Optional<User> user = userService.getUserByTaiKhoan(username);
        if (!user.isPresent()) {
            return ApiResponseUtil.error("User not found");
        }

        List<AppliedJob> appliedJobs = appliedJobService.getAppliedJobsByEmployee(user.get()).stream()
            .filter(job -> "Tuyển dụng".equals(job.getTrangThaiUngTuyen()))
            .toList();

        return ApiResponseUtil.success("My approved applications retrieved successfully", appliedJobs);
    }

    // Lấy danh sách ứng tuyển cho nhà tuyển dụng
    @GetMapping("/for-employer")
    public ResponseEntity<?> getApplicationsForEmployer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponseUtil.error("User not authenticated");
        }

        String username = authentication.getName();
        Optional<User> user = userService.getUserByTaiKhoan(username);
        if (!user.isPresent()) {
            return ApiResponseUtil.error("User not found");
        }

        try {
            List<AppliedJob> appliedJobs = appliedJobService.getAppliedJobsByEmployer(user.get().getMaNguoiDung());
            return ApiResponseUtil.success("Applications for employer retrieved successfully", appliedJobs);
        } catch (Exception e) {
            return ApiResponseUtil.error("Error retrieving applications: " + e.getMessage());
        }
    }

    // Lấy thông tin CV của ứng viên (dành cho nhà tuyển dụng)
    @GetMapping("/{id}/cv")
    public ResponseEntity<?> getApplicantCv(@PathVariable Integer id) {
        Optional<AppliedJob> appliedJobOpt = appliedJobService.getAppliedJobByIdWithDetails(id);
        if (!appliedJobOpt.isPresent()) {
            return ApiResponseUtil.error("Applied job not found with id: " + id);
        }

        AppliedJob appliedJob = appliedJobOpt.get();

        // Kiểm tra quyền: chỉ nhà tuyển dụng của công ty đăng công việc mới có thể xem CV
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ApiResponseUtil.error("User not authenticated");
        }

        String username = authentication.getName();
        Optional<User> user = userService.getUserByTaiKhoan(username);
        if (!user.isPresent()) {
            return ApiResponseUtil.error("User not found");
        }

        if (!appliedJob.getJobDetail().getCompany().getUser().getMaNguoiDung().equals(user.get().getMaNguoiDung())) {
            return ApiResponseUtil.error("You don't have permission to view this CV");
        }

        try {
            Map<String, Object> response = new java.util.HashMap<>();

            // Xử lý fallback CV: nếu không có trong applied_job, lấy từ profile
            String cvUrl = appliedJob.getUrlCvUngTuyen();
            if (cvUrl == null || cvUrl.isEmpty()) {
                // Lấy từ profile nếu có
                Optional<Profile> profileOpt = profileService.getProfileByUser(appliedJob.getEmployee());
                if (profileOpt.isPresent() && profileOpt.get().getUrlCv() != null) {
                    cvUrl = profileOpt.get().getUrlCv();
                }
            }

            response.put("cvUrl", cvUrl);
            response.put("trangThaiUngTuyen", appliedJob.getTrangThaiUngTuyen());
            response.put("danhGiaNtd", appliedJob.getDanhGiaNtd());
            response.put("ngayUngTuyen", appliedJob.getNgayUngTuyen());

            // Thêm thông tin chi tiết của người ứng tuyển
            Map<String, Object> employeeInfo = new java.util.HashMap<>();
            if (appliedJob.getEmployee() != null) {
                employeeInfo.put("maNguoiDung", appliedJob.getEmployee().getMaNguoiDung());
                employeeInfo.put("tenHienThi", appliedJob.getEmployee().getTenHienThi());
                employeeInfo.put("taiKhoan", appliedJob.getEmployee().getTaiKhoan());
                employeeInfo.put("lienHe", appliedJob.getEmployee().getLienHe());
            }
            response.put("employee", employeeInfo);

            // Thêm thông tin chi tiết của công việc
            Map<String, Object> jobDetailInfo = new java.util.HashMap<>();
            if (appliedJob.getJobDetail() != null) {
                jobDetailInfo.put("maCongViec", appliedJob.getJobDetail().getMaCongViec());
                jobDetailInfo.put("tieuDe", appliedJob.getJobDetail().getTieuDe());
                jobDetailInfo.put("chiTiet", appliedJob.getJobDetail().getChiTiet());
                jobDetailInfo.put("yeuCauCongViec", appliedJob.getJobDetail().getYeuCauCongViec());
                jobDetailInfo.put("quyenLoi", appliedJob.getJobDetail().getQuyenLoi());
                jobDetailInfo.put("luong", appliedJob.getJobDetail().getLuong());
                jobDetailInfo.put("loaiLuong", appliedJob.getJobDetail().getLoaiLuong());
                jobDetailInfo.put("trangThaiDuyet", appliedJob.getJobDetail().getTrangThaiDuyet());
                jobDetailInfo.put("trangThaiTinTuyen", appliedJob.getJobDetail().getTrangThaiTinTuyen());
            }
            response.put("jobDetail", jobDetailInfo);

            return ApiResponseUtil.success("CV information retrieved successfully", response);
        } catch (Exception e) {
            return ApiResponseUtil.error("Error retrieving CV information: " + e.getMessage());
        }
    }

    // Request classes
    public static class AppliedJobRequest {
        private Integer jobDetailId;

        public Integer getJobDetailId() {
            return jobDetailId;
        }

        public void setJobDetailId(Integer jobDetailId) {
            this.jobDetailId = jobDetailId;
        }
    }

    public static class StatusUpdateRequest {
        private String status;
        private Byte rating;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Byte getRating() {
            return rating;
        }

        public void setRating(Byte rating) {
            this.rating = rating;
        }
    }
}