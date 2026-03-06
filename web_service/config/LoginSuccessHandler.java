package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

/**
 * Xử lý chuyển hướng sau khi đăng nhập thành công dựa trên vai trò người dùng
 * 
 * Quy tắc chuyển hướng:
 * - ADMIN (/admin/**) -> /admin/dashboard
 * - NTD (Nhà tuyển dụng) -> /employer/dashboard
 * - NV (Người xin việc) -> / (trang chủ)
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        String username = authentication.getName();
        User user = userService.getUserByTaiKhoan(username).orElse(null);
        
        String redirectUrl = determineRedirectUrl(user);
        
        // Redirect đến URL đã xác định
        response.sendRedirect(redirectUrl);
    }
    
    /**
     * Xác định URL chuyển hướng dựa trên vai trò người dùng
     * @param user Người dùng đã đăng nhập
     * @return URL chuyển hướng
     */
    private String determineRedirectUrl(User user) {
        if (user == null || user.getRole() == null) {
            return "/";
        }
        
        String roleCode = user.getRole().getTenVaiTro();
        
        // Admin -> chuyển đến trang admin dashboard
        if ("ADMIN".equals(roleCode)) {
            return "/admin/dashboard";
        }
        // Nhà tuyển dụng -> chuyển đến trang employer dashboard
        else if ("NTD".equals(roleCode)) {
            return "/employer/dashboard";
        }
        // Ứng viên (NV) -> chuyển đến trang chủ
        else {
            return "/";
        }
    }
}
