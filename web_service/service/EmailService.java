package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * Gửi email xác nhận đăng ký tài khoản
     */
    @Async
    public void sendAccountConfirmationEmail(String to, String username, String activationLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("activationLink", activationLink);
            context.setVariable("siteUrl", "http://localhost:8080");

            String htmlContent = templateEngine.process("email/account-confirmation", context);
            helper.setTo(to);
            helper.setSubject("Xác nhận đăng ký tài khoản - Fjobs");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gửi email xác nhận mã OTP
     */
    @Async
    public void sendOtpVerificationEmail(String to, String username, String otpCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("otpCode", otpCode);
            context.setVariable("siteUrl", "http://localhost:8080");

            String htmlContent = templateEngine.process("email/otp-verification", context);
            helper.setTo(to);
            helper.setSubject("Mã xác nhận OTP - Fjobs");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gửi email thông báo có ứng viên mới
     */
    @Async
    public void sendNewApplicationNotification(String to, String employerName, String jobTitle, String applicantName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("employerName", employerName);
            context.setVariable("jobTitle", jobTitle);
            context.setVariable("applicantName", applicantName);
            context.setVariable("siteUrl", "http://localhost:8080");

            String htmlContent = templateEngine.process("email/new-application", context);
            helper.setTo(to);
            helper.setSubject("Thông báo ứng tuyển mới - Fjobs");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gửi email thông báo trạng thái ứng tuyển cho ứng viên
     */
    @Async
    public void sendApplicationStatusUpdate(String to, String applicantName, String jobTitle, String status, String messageContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("applicantName", applicantName);
            context.setVariable("jobTitle", jobTitle);
            context.setVariable("status", status);
            context.setVariable("message", messageContent);
            context.setVariable("siteUrl", "http://localhost:8080");

            String htmlContent = templateEngine.process("email/application-status-update", context);
            helper.setTo(to);
            helper.setSubject("Cập nhật trạng thái ứng tuyển - Fjobs");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gửi email thông báo duyệt công ty
     */
    @Async
    public void sendCompanyApprovalNotification(String to, String companyName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("companyName", companyName);
            context.setVariable("siteUrl", "http://localhost:8080");

            String htmlContent = templateEngine.process("email/company-approved", context);
            helper.setTo(to);
            helper.setSubject("Công ty đã được duyệt - Fjobs");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gửi email thông báo từ chối công ty
     */
    @Async
    public void sendCompanyRejectionNotification(String to, String companyName, String reason) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("companyName", companyName);
            context.setVariable("reason", reason);
            context.setVariable("siteUrl", "http://localhost:8080");

            String htmlContent = templateEngine.process("email/company-rejected", context);
            helper.setTo(to);
            helper.setSubject("Công ty không được duyệt - Fjobs");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gửi email thông báo duyệt tin tuyển dụng
     */
    @Async
    public void sendJobApprovalNotification(String to, String jobTitle) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("jobTitle", jobTitle);
            context.setVariable("siteUrl", "http://localhost:8080");

            String htmlContent = templateEngine.process("email/job-approved", context);
            helper.setTo(to);
            helper.setSubject("Tin tuyển dụng đã được duyệt - Fjobs");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gửi email thông báo từ chối tin tuyển dụng
     */
    @Async
    public void sendJobRejectionNotification(String to, String jobTitle, String reason) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("jobTitle", jobTitle);
            context.setVariable("reason", reason);
            context.setVariable("siteUrl", "http://localhost:8080");

            String htmlContent = templateEngine.process("email/job-rejected", context);
            helper.setTo(to);
            helper.setSubject("Tin tuyển dụng không được duyệt - Fjobs");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gửi email thông báo hết hạn tin tuyển dụng
     */
    @Async
    public void sendJobExpirationWarning(String to, String jobTitle, String expirationDate) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("jobTitle", jobTitle);
            context.setVariable("expirationDate", expirationDate);
            context.setVariable("siteUrl", "http://localhost:8080");

            String htmlContent = templateEngine.process("email/job-expiration-warning", context);
            helper.setTo(to);
            helper.setSubject("Tin tuyển dụng sắp hết hạn - Fjobs");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gửi email thông báo có công ty mới chờ duyệt (cho admin)
     */
    @Async
    public void sendNewCompanyForApprovalNotification(String to, String companyName, String representativeName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("companyName", companyName);
            context.setVariable("representativeName", representativeName);
            context.setVariable("siteUrl", "http://localhost:8080/admin/companies");

            String htmlContent = templateEngine.process("email/new-company-for-approval", context);
            helper.setTo(to);
            helper.setSubject("Công ty mới chờ duyệt - Fjobs Admin");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gửi email thông báo có tin tuyển dụng mới chờ duyệt (cho admin)
     */
    @Async
    public void sendNewJobForApprovalNotification(String to, String jobTitle, String companyName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("jobTitle", jobTitle);
            context.setVariable("companyName", companyName);
            context.setVariable("siteUrl", "http://localhost:8080/admin/jobs");

            String htmlContent = templateEngine.process("email/new-job-for-approval", context);
            helper.setTo(to);
            helper.setSubject("Tin tuyển dụng mới chờ duyệt - Fjobs Admin");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gửi email đơn giản
     */
    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}