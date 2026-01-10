package com.example.demo.service;

import com.example.demo.entity.OtpCode;
import com.example.demo.repository.OtpCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpCodeRepository otpCodeRepository;

    @Autowired
    private NotificationEventService notificationEventService;

    /**
     * Tạo mã OTP 6 chữ số ngẫu nhiên
     */
    public String generateOtpCode() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Tạo số ngẫu nhiên từ 100000 đến 999999
        return String.valueOf(otp);
    }

    /**
     * Gửi mã OTP đến email
     */
    public void sendOtp(String email, String username) {
        // Xóa các OTP cũ chưa sử dụng của email này
        deleteUnusedOtpsByEmail(email);

        // Tạo mã OTP mới
        String otpCode = generateOtpCode();

        // Lưu OTP vào DB
        OtpCode otp = new OtpCode(email, otpCode);
        otpCodeRepository.save(otp);

        // Gửi email OTP
        notificationEventService.notifyOtpVerification(email, username, otpCode);
    }

    /**
     * Xác thực mã OTP
     */
    public boolean verifyOtp(String email, String otpCode) {
        // Tìm OTP trong DB
        return otpCodeRepository.findByEmailAndOtpCodeAndDaSuDungFalse(email, otpCode)
                .map(otp -> {
                    // Kiểm tra thời hạn
                    if (otp.isStillValid()) {
                        // Đánh dấu OTP đã sử dụng
                        otp.setDaSuDung(true);
                        otpCodeRepository.save(otp);
                        return true;
                    } else {
                        // OTP đã hết hạn
                        otpCodeRepository.delete(otp);
                        return false;
                    }
                })
                .orElse(false);
    }

    /**
     * Xóa các OTP chưa sử dụng của email
     */
    private void deleteUnusedOtpsByEmail(String email) {
        otpCodeRepository.findLatestUnusedOtpsByEmail(email)
                .forEach(otp -> otpCodeRepository.delete(otp));
    }

    /**
     * Xóa các OTP đã hết hạn
     */
    public void deleteExpiredOtps() {
        otpCodeRepository.findAll().stream()
                .filter(otp -> !otp.isStillValid() && !otp.getDaSuDung())
                .forEach(otp -> otpCodeRepository.delete(otp));
    }
}