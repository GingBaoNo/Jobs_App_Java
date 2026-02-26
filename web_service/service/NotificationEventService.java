package com.example.demo.service;

import com.example.demo.entity.AppliedJob;
import com.example.demo.entity.Company;
import com.example.demo.entity.JobDetail;
import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service để xử lý các sự kiện gửi email trong hệ thống
 */
@Service
public class NotificationEventService {

    @Autowired
    private EmailService emailService;

    /**
     * Gửi email thông báo khi có ứng viên mới ứng tuyển vào công việc
     */
    public void notifyEmployerOfNewApplication(AppliedJob appliedJob) {
        User employer = appliedJob.getJobDetail().getCompany().getUser(); // Lấy user là nhà tuyển dụng
        String employerEmail = employer.getEmail();
        String employerName = employer.getTenHienThi();
        String jobTitle = appliedJob.getJobDetail().getTieuDe();
        String applicantName = appliedJob.getEmployee().getTenHienThi();

        emailService.sendNewApplicationNotification(employerEmail, employerName, jobTitle, applicantName);
    }

    /**
     * Gửi email thông báo trạng thái ứng tuyển cho ứng viên
     */
    public void notifyApplicantOfApplicationStatus(AppliedJob appliedJob) {
        String applicantEmail = appliedJob.getEmployee().getEmail();
        String applicantName = appliedJob.getEmployee().getTenHienThi();
        String jobTitle = appliedJob.getJobDetail().getTieuDe();
        String status = appliedJob.getTrangThaiUngTuyen();
        // Không có trường ghiChuNtd trong entity AppliedJob, nên sử dụng một giá trị mặc định
        String messageContent = ""; // hoặc có thể truyền thêm thông tin nếu cần

        emailService.sendApplicationStatusUpdate(applicantEmail, applicantName, jobTitle, status, messageContent);
    }

    /**
     * Gửi email thông báo duyệt công ty
     */
    public void notifyCompanyApproval(Company company) {
        String representativeEmail = company.getUser().getEmail();
        String companyName = company.getTenCongTy();

        emailService.sendCompanyApprovalNotification(representativeEmail, companyName);
    }

    /**
     * Gửi email thông báo từ chối công ty
     */
    public void notifyCompanyRejection(Company company, String reason) {
        String representativeEmail = company.getUser().getEmail();
        String companyName = company.getTenCongTy();

        emailService.sendCompanyRejectionNotification(representativeEmail, companyName, reason);
    }

    /**
     * Gửi email thông báo duyệt tin tuyển dụng
     */
    public void notifyJobApproval(JobDetail jobDetail) {
        String employerEmail = jobDetail.getCompany().getUser().getEmail();
        String jobTitle = jobDetail.getTieuDe();

        emailService.sendJobApprovalNotification(employerEmail, jobTitle);
    }

    /**
     * Gửi email thông báo từ chối tin tuyển dụng
     */
    public void notifyJobRejection(JobDetail jobDetail, String reason) {
        String employerEmail = jobDetail.getCompany().getUser().getEmail();
        String jobTitle = jobDetail.getTieuDe();

        emailService.sendJobRejectionNotification(employerEmail, jobTitle, reason);
    }

    /**
     * Gửi email cảnh báo hết hạn tin tuyển dụng
     */
    public void notifyJobExpirationWarning(JobDetail jobDetail) {
        String employerEmail = jobDetail.getCompany().getUser().getEmail();
        String jobTitle = jobDetail.getTieuDe();
        String expirationDate = jobDetail.getNgayKetThucTuyenDung().toString();

        emailService.sendJobExpirationWarning(employerEmail, jobTitle, expirationDate);
    }

    /**
     * Gửi email thông báo cho admin về công ty mới chờ duyệt
     */
    public void notifyAdminOfNewCompanyForApproval(Company company, String adminEmail) {
        String companyName = company.getTenCongTy();
        String representativeName = company.getTenNguoiDaiDien();

        emailService.sendNewCompanyForApprovalNotification(adminEmail, companyName, representativeName);
    }

    /**
     * Gửi email thông báo cho admin về tin tuyển dụng mới chờ duyệt
     */
    public void notifyAdminOfNewJobForApproval(JobDetail jobDetail, String adminEmail) {
        String jobTitle = jobDetail.getTieuDe();
        String companyName = jobDetail.getCompany().getTenCongTy();

        emailService.sendNewJobForApprovalNotification(adminEmail, jobTitle, companyName);
    }

    /**
     * Gửi email xác nhận mã OTP
     */
    public void notifyOtpVerification(String to, String username, String otpCode) {
        emailService.sendOtpVerificationEmail(to, username, otpCode);
    }
}