package com.example.demo.controller.api;

import com.example.demo.entity.User;
import com.example.demo.service.OtpService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class ApiOtpController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserService userService;

    // Gửi OTP
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        
        Map<String, Object> response = new HashMap<>();
        
        Optional<User> userOpt = userService.getUserByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            otpService.sendOtp(email, user.getTenHienThi());
            
            response.put("success", true);
            response.put("message", "Mã xác nhận đã được gửi đến email của bạn");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Email không tồn tại trong hệ thống");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Xác nhận OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        
        Map<String, Object> response = new HashMap<>();
        
        boolean isValid = otpService.verifyOtp(email, otp);
        if (isValid) {
            response.put("success", true);
            response.put("message", "Xác nhận OTP thành công");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Mã xác nhận không đúng hoặc đã hết hạn");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Đặt lại mật khẩu
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");
        String confirmNewPassword = request.get("confirmNewPassword");
        
        Map<String, Object> response = new HashMap<>();
        
        if (!newPassword.equals(confirmNewPassword)) {
            response.put("success", false);
            response.put("message", "Mật khẩu xác nhận không khớp");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<User> userOpt = userService.getUserByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Mã hóa mật khẩu mới
            user.setMatKhau(userService.encodePassword(newPassword));
            userService.updateUser(user);
            
            response.put("success", true);
            response.put("message", "Mật khẩu đã được thay đổi thành công");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Email không tồn tại trong hệ thống");
            return ResponseEntity.badRequest().body(response);
        }
    }
}