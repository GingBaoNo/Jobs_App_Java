package com.example.demo.controller;

import com.example.demo.entity.AppliedJob;
import com.example.demo.entity.Company;
import com.example.demo.entity.JobDetail;
import com.example.demo.entity.Message;
import com.example.demo.entity.Profile;
import com.example.demo.entity.User;
import com.example.demo.service.AppliedJobService;
import com.example.demo.service.CompanyService;
import com.example.demo.service.JobDetailService;
import com.example.demo.service.MessageService;
import com.example.demo.service.ProfileService;
import com.example.demo.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class EmployerController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    @Autowired
    private AppliedJobService appliedJobService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private JobDetailService jobDetailService;

    @Autowired
    private MessageService messageService;

    // Trang dashboard của nhà tuyển dụng
    @GetMapping("/employer/dashboard")
    public String employerDashboard(Authentication authentication, Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("title", "Bảng điều khiển Nhà Tuyển Dụng");

            // Lấy thông tin công ty nếu có
            Company company = companyService.getCompanyByUser(user).orElse(null);
            model.addAttribute("company", company);

            // Thêm dữ liệu thống kê cho dashboard nếu có công ty
            if (company != null) {
                // Lấy số lượng tin tuyển dụng
                List<JobDetail> jobs = jobDetailService.getJobsByCompany(company);
                model.addAttribute("totalJobs", jobs.size());

                // Lấy số lượng ứng viên
                int totalApplicants = 0;
                for (JobDetail job : jobs) {
                    List<AppliedJob> appliedJobs = appliedJobService.getAppliedJobsByJobDetail(job);
                    totalApplicants += appliedJobs.size();
                }
                model.addAttribute("totalApplicants", totalApplicants);

                // Lấy số lượng tin nhắn chưa đọc
                List<Message> unreadMessages = messageService.getUnreadMessagesByReceiver(user);
                model.addAttribute("totalMessages", unreadMessages.size());

                // Lấy tổng số lượt xem hồ sơ (tạm thời lấy từ tổng số lần xem tất cả các công việc)
                int totalViews = jobs.stream()
                    .mapToInt(job -> job.getLuotXem() != null ? job.getLuotXem() : 0)
                    .sum();
                model.addAttribute("totalViews", totalViews);

                // Thống kê tuyển dụng
                long activeJobs = jobs.stream()
                    .filter(job -> "Mở".equals(job.getTrangThaiTinTuyen()) && "Đã duyệt".equals(job.getTrangThaiDuyet()))
                    .count();
                long closedJobs = jobs.stream()
                    .filter(job -> "Đã đóng".equals(job.getTrangThaiTinTuyen()))
                    .count();
                long pausedJobs = jobs.stream()
                    .filter(job -> "Tạm dừng".equals(job.getTrangThaiTinTuyen()))
                    .count();

                model.addAttribute("activeJobs", activeJobs);
                model.addAttribute("closedJobs", closedJobs);
                model.addAttribute("pausedJobs", pausedJobs);

                // Lấy tin tuyển dụng gần đây (lấy 3 tin mới nhất) cùng với số lượng ứng viên
                List<JobDetail> recentJobs = jobs.stream()
                    .sorted((j1, j2) -> j2.getNgayDang().compareTo(j1.getNgayDang()))
                    .limit(3)
                    .toList();

                // Tạo map để lưu số lượng ứng viên cho từng công việc
                Map<Integer, Integer> jobApplicantCount = new HashMap<>();
                for (JobDetail job : recentJobs) {
                    List<AppliedJob> appliedJobs = appliedJobService.getAppliedJobsByJobDetail(job);
                    jobApplicantCount.put(job.getMaCongViec(), appliedJobs.size());
                }
                model.addAttribute("recentJobs", recentJobs);
                model.addAttribute("jobApplicantCount", jobApplicantCount);
            }
        }
        return "employer/dashboard";
    }

    // Trang xem hồ sơ công ty
    @GetMapping("/employer/company")
    public String viewCompanyProfile(Authentication authentication, Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Company> companyOpt = companyService.getCompanyByUser(user);
        if (companyOpt.isEmpty()) {
            // Nếu chưa có công ty, chuyển hướng đến trang chỉnh sửa để đăng ký
            return "redirect:/employer/company/edit";
        } else {
            model.addAttribute("company", companyOpt.get());
        }

        model.addAttribute("title", "Hồ sơ công ty");
        return "employer/company-profile";
    }

    // Trang cập nhật thông tin công ty hoặc đăng ký nếu chưa có
    @GetMapping("/employer/company/edit")
    public String editCompany(Authentication authentication, Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Company> companyOpt = companyService.getCompanyByUser(user);
        if (companyOpt.isEmpty()) {
            // Nếu chưa có công ty, chuẩn bị form đăng ký
            model.addAttribute("company", new Company()); // Một công ty trống cho form
            model.addAttribute("isRegistration", true); // Biến để template biết đang trong chế độ đăng ký
            model.addAttribute("title", "Đăng ký công ty");
        } else {
            model.addAttribute("company", companyOpt.get());
            model.addAttribute("isRegistration", false); // Đang cập nhật
            model.addAttribute("title", "Cập nhật thông tin công ty");
        }

        return "employer/company"; // Trang cập nhật hoặc đăng ký
    }

    // Xử lý đăng ký công ty
    @PostMapping("/employer/company/register")
    public String registerCompany(Authentication authentication,
                                 @ModelAttribute Company company,
                                 @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
                                 Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            // Kiểm tra xem công ty đã tồn tại chưa
            if (companyService.existsByTenCongTy(company.getTenCongTy())) {
                model.addAttribute("errorMessage", "Tên công ty đã tồn tại");
                model.addAttribute("company", company);
                model.addAttribute("isRegistration", true);
                return "employer/company";
            }

            // Tạo công ty mới
            Company newCompany = companyService.registerCompany(
                user,
                company.getTenCongTy(),
                company.getTenNguoiDaiDien(),
                company.getMaSoThue(),
                company.getDiaChi(),
                company.getLienHeCty()
            );

            // Nếu có logo được tải lên, cập nhật logo cho công ty mới
            if (logoFile != null && !logoFile.isEmpty()) {
                 try {
                    // Phương thức updateCompanyLogo đã xử lý việc lưu file và cập nhật hinhAnhCty
                    companyService.updateCompanyLogo(newCompany.getMaCongTy(), logoFile);
                    // load lại company sau khi update logo để có URL mới, nếu cần để hiển thị ngay
                    // newCompany = companyService.getCompanyById(newCompany.getMaCongTy()).orElse(newCompany); // Tùy chọn
                } catch (Exception e) {
                    // Nếu upload logo thất bại, ghi log và tiếp tục. Công ty vẫn được tạo.
                    // Có thể thêm thông báo lỗi riêng cho logo nếu muốn.
                    e.printStackTrace();
                    model.addAttribute("errorMessage", "Đăng ký công ty thành công, nhưng có lỗi khi tải lên logo: " + e.getMessage());
                    // Không return, tiếp tục với successMessage chung.
                }
            }

            model.addAttribute("successMessage", "Đăng ký công ty thành công!");
            model.addAttribute("company", newCompany);
            model.addAttribute("isRegistration", false);

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi đăng ký công ty: " + e.getMessage());
            model.addAttribute("company", company);
            model.addAttribute("isRegistration", true);
            return "employer/company";
        }

        return "employer/company";
    }

    // Xử lý upload logo công ty
    @PostMapping("/employer/company/update-logo")
    public String updateCompanyLogo(Authentication authentication,
                                   @RequestParam("logoFile") MultipartFile logoFile,
                                   @RequestParam("companyId") Integer companyId,
                                   Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Company existingCompany = companyService.getCompanyById(companyId).orElse(null);
        if (existingCompany == null || !existingCompany.getUser().getMaNguoiDung().equals(user.getMaNguoiDung())) {
            // Không tìm thấy công ty hoặc công ty không thuộc về người dùng này
            model.addAttribute("errorMessage", "Không thể cập nhật logo cho công ty này.");
            return "employer/company"; // Trở lại trang quản lý công ty
        }

        try {
            companyService.updateCompanyLogo(companyId, logoFile);
            model.addAttribute("successMessage", "Cập nhật logo thành công!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi cập nhật logo: " + e.getMessage());
            e.printStackTrace();
        }

        // Load lại thông tin công ty để hiển thị trên view
        Company updatedCompany = companyService.getCompanyById(companyId).orElse(null);
        model.addAttribute("company", updatedCompany);
        model.addAttribute("isRegistration", false);
        return "employer/company";
    }

    // Xử lý cập nhật thông tin công ty (bao gồm cả logo nếu có)
    @PostMapping("/employer/company/update")
    public String updateCompany(Authentication authentication,
                                @RequestParam("maCongTy") Integer companyId, // Lấy ID từ form
                                @RequestParam("tenCongTy") String tenCongTy,
                                @RequestParam("tenNguoiDaiDien") String tenNguoiDaiDien,
                                @RequestParam("diaChi") String diaChi,
                                @RequestParam("maSoThue") String maSoThue,
                                @RequestParam("lienHeCty") String lienHeCty,
                                @RequestParam(value = "logoFile", required = false) MultipartFile logoFile, // Thêm logo file, có thể không có
                                Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        // Lấy công ty hiện tại của người dùng
        Company existingCompany = companyService.getCompanyByUser(user).orElse(null);
        if (existingCompany == null || !existingCompany.getMaCongTy().equals(companyId)) {
            // Không tìm thấy công ty hoặc công ty không thuộc về người dùng này hoặc ID không khớp
            model.addAttribute("errorMessage", "Không thể cập nhật thông tin công ty này.");
            return "employer/company"; // Trở lại trang cập nhật công ty
        }

        // Cập nhật logo nếu có file mới
        if (logoFile != null && !logoFile.isEmpty()) {
             try {
                // Phương thức updateCompanyLogo đã xử lý việc lưu file và cập nhật hinhAnhCty
                companyService.updateCompanyLogo(companyId, logoFile);
                // load lại company sau khi update logo để có URL mới
                existingCompany = companyService.getCompanyById(companyId).orElse(existingCompany);
                model.addAttribute("successMessage", "Cập nhật thông tin và logo công ty thành công!");
            } catch (Exception e) {
                model.addAttribute("errorMessage", "Lỗi khi cập nhật logo: " + e.getMessage());
                e.printStackTrace();
                // Không return, vẫn cố cập nhật thông tin text nếu logo lỗi
            }
        }

        // Cập nhật các trường thông tin khác
        existingCompany.setTenCongTy(tenCongTy);
        existingCompany.setTenNguoiDaiDien(tenNguoiDaiDien);
        existingCompany.setDiaChi(diaChi);
        existingCompany.setMaSoThue(maSoThue);
        existingCompany.setLienHeCty(lienHeCty);

        // Lưu lại thông tin chung vào DB
        try {
            companyService.updateCompany(existingCompany);
            // Nếu chưa có success message từ logo, gán message cho update text
            if (!model.containsAttribute("successMessage")) {
                model.addAttribute("successMessage", "Cập nhật thông tin công ty thành công!");
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi cập nhật thông tin công ty: " + e.getMessage());
            e.printStackTrace();
        }

        // Load lại thông tin công ty để hiển thị trên view
        model.addAttribute("company", existingCompany);
        model.addAttribute("isRegistration", false); // Gán lại để đảm bảo template render đúng nếu có lỗi
        return "employer/company";
    }

    // Trang quản lý ứng viên
    @GetMapping("/employer/applicants")
    public String applicantManagement(@RequestParam(value = "search", required = false) String search, Authentication authentication, Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Company company = companyService.getCompanyByUser(user).orElse(null);
        if (company == null) {
            model.addAttribute("errorMessage", "Bạn chưa đăng ký công ty nào.");
            return "redirect:/employer/company";
        }

        // Lấy tất cả công việc của công ty
        List<JobDetail> jobs = jobDetailService.getJobsByCompany(company);

        // Lấy tất cả ứng viên cho các công việc của công ty
        List<AppliedJob> allAppliedJobs = new ArrayList<>();
        for (JobDetail job : jobs) {
            List<AppliedJob> appliedJobs = appliedJobService.getAppliedJobsByJobDetail(job);
            allAppliedJobs.addAll(appliedJobs);
        }

        // Nếu có tìm kiếm, lọc theo tên ứng viên hoặc tiêu đề công việc
        if (search != null && !search.trim().isEmpty()) {
            List<AppliedJob> filteredAppliedJobs = new ArrayList<>();
            for (AppliedJob appliedJob : allAppliedJobs) {
                String applicantName = appliedJob.getEmployee().getTenHienThi();
                String jobTitle = appliedJob.getJobDetail().getTieuDe();
                if (applicantName != null && applicantName.toLowerCase().contains(search.toLowerCase())) {
                    filteredAppliedJobs.add(appliedJob);
                } else if (jobTitle != null && jobTitle.toLowerCase().contains(search.toLowerCase())) {
                    filteredAppliedJobs.add(appliedJob);
                }
            }
            allAppliedJobs = filteredAppliedJobs;
        }

        model.addAttribute("appliedJobs", allAppliedJobs);
        model.addAttribute("user", user);
        model.addAttribute("company", company);
        model.addAttribute("title", "Quản lý Ứng viên");
        model.addAttribute("searchQuery", search != null ? search : "");

        return "employer/applicants";
    }

    // Trang chi tiết ứng viên
    @GetMapping("/employer/applicants/{id}/cv")
    public String applicantDetail(@PathVariable Integer id, Authentication authentication, Model model) {
        return applicantDetailAndView(authentication, model, id);
    }

    @PostMapping("/employer/applicants/{id}/cv")
    public String updateApplicantStatus(@PathVariable Integer id,
                                   @RequestParam String status,
                                   @RequestParam(required = false) Byte rating,
                                   Authentication authentication, Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Company company = companyService.getCompanyByUser(user).orElse(null);
        if (company == null) {
            model.addAttribute("errorMessage", "Bạn chưa đăng ký công ty nào.");
            return "redirect:/employer/company";
        }

        // Lấy thông tin ứng tuyển với chi tiết
        Optional<AppliedJob> appliedJobOpt = appliedJobService.getAppliedJobByIdWithDetails(id);
        if (!appliedJobOpt.isPresent()) {
            model.addAttribute("errorMessage", "Không tìm thấy hồ sơ ứng viên.");
            return applicantDetailAndView(authentication, model, id);
        }

        AppliedJob appliedJob = appliedJobOpt.get();

        // Kiểm tra quyền: chỉ nhà tuyển dụng của công ty đăng công việc mới có thể cập nhật
        if (!appliedJob.getJobDetail().getCompany().getUser().getMaNguoiDung().equals(user.getMaNguoiDung())) {
            model.addAttribute("errorMessage", "Bạn không có quyền cập nhật hồ sơ này.");
            return applicantDetailAndView(authentication, model, id);
        }

        // Cập nhật trạng thái
        appliedJob.setTrangThaiUngTuyen(status);
        if (rating != null) {
            appliedJob.setDanhGiaNtd(rating);
        }

        appliedJobService.updateAppliedJob(appliedJob);

        // Redirect để tránh resubmission
        return "redirect:/employer/applicants/" + id + "/cv";
    }

    private String applicantDetailAndView(Authentication authentication, Model model, Integer id) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Company company = companyService.getCompanyByUser(user).orElse(null);
        if (company == null) {
            model.addAttribute("errorMessage", "Bạn chưa đăng ký công ty nào.");
            return "redirect:/employer/company";
        }

        // Lấy thông tin ứng tuyển với chi tiết
        Optional<AppliedJob> appliedJobOpt = appliedJobService.getAppliedJobByIdWithDetails(id);
        if (!appliedJobOpt.isPresent()) {
            model.addAttribute("errorMessage", "Không tìm thấy hồ sơ ứng viên.");
            model.addAttribute("user", user);
            model.addAttribute("company", company);
            model.addAttribute("title", "Chi tiết Ứng viên");
            return "employer/applicant-detail";
        }

        AppliedJob appliedJob = appliedJobOpt.get();

        // Kiểm tra quyền: chỉ nhà tuyển dụng của công ty đăng công việc mới có thể xem
        if (!appliedJob.getJobDetail().getCompany().getUser().getMaNguoiDung().equals(user.getMaNguoiDung())) {
            model.addAttribute("errorMessage", "Bạn không có quyền xem hồ sơ này.");
            model.addAttribute("user", user);
            model.addAttribute("company", company);
            model.addAttribute("title", "Chi tiết Ứng viên");
            return "employer/applicant-detail";
        }

        // Lấy hồ sơ người tìm việc
        Optional<Profile> profileOpt = profileService.getProfileByUser(appliedJob.getEmployee());

        model.addAttribute("appliedJob", appliedJob);
        model.addAttribute("profile", profileOpt.orElse(null));
        model.addAttribute("user", user);
        model.addAttribute("company", company);
        model.addAttribute("title", "Chi tiết Ứng viên");
        return "employer/applicant-detail";
    }

    // Trang danh sách ứng viên cho công việc cụ thể
    @GetMapping("/employer/jobs/{jobId}/applicants")
    public String jobApplicants(@PathVariable Integer jobId, Authentication authentication, Model model) {
        User user = userService.getUserByTaiKhoan(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Company company = companyService.getCompanyByUser(user).orElse(null);
        if (company == null) {
            model.addAttribute("errorMessage", "Bạn chưa đăng ký công ty nào.");
            return "redirect:/employer/company";
        }

        model.addAttribute("user", user);
        model.addAttribute("company", company);
        model.addAttribute("jobId", jobId);
        model.addAttribute("title", "Ứng viên cho công việc");
        return "employer/job-applicants";
    }
}