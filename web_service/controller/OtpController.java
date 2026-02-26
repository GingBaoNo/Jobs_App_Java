package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.OtpService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class OtpController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserService userService;

    // Trang yêu cầu gửi OTP
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    // Gửi OTP
    @PostMapping("/forgot-password")
    public String sendOtp(@RequestParam String email, Model model) {
        Optional<User> userOpt = userService.getUserByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            otpService.sendOtp(email, user.getTenHienThi());
            // Thay vì chuyển trực tiếp sang verify-otp, ta redirect để đảm bảo URL đúng
            return "redirect:/verify-otp?email=" + email;
        } else {
            model.addAttribute("error", "Email không tồn tại trong hệ thống.");
            return "auth/forgot-password";
        }
    }

    // Trang xác nhận OTP
    @GetMapping("/verify-otp")
    public String showVerifyOtpForm(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "auth/verify-otp";
    }

    // Xác nhận OTP
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email, @RequestParam String otp, Model model) {
        boolean isValid = otpService.verifyOtp(email, otp);
        if (isValid) {
            // OTP hợp lệ, chuyển đến trang đặt lại mật khẩu
            model.addAttribute("email", email);
            return "auth/reset-password";
        } else {
            model.addAttribute("error", "Mã xác nhận không đúng hoặc đã hết hạn. Vui lòng thử lại.");
            model.addAttribute("email", email);
            return "auth/verify-otp";
        }
    }

    // Đặt lại mật khẩu
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email,
                               @RequestParam String newPassword,
                               @RequestParam String confirmNewPassword,
                               Model model) {
        if (!newPassword.equals(confirmNewPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp.");
            model.addAttribute("email", email);
            return "auth/reset-password";
        }

        Optional<User> userOpt = userService.getUserByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Mã hóa mật khẩu mới
            user.setMatKhau(userService.encodePassword(newPassword));
            userService.updateUser(user);
            model.addAttribute("message", "Mật khẩu đã được thay đổi thành công.");
            return "auth/login";
        } else {
            model.addAttribute("error", "Email không tồn tại trong hệ thống.");
            return "auth/forgot-password";
        }
    }

    // Trang hướng dẫn sử dụng chức năng quên mật khẩu
    @GetMapping("/password-reset-guide")
    public String showPasswordResetGuide() {
        return "auth/password-reset-guide";
    }
}