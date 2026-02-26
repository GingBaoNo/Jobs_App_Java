package com.example.demo.service;

import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserService userService;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByTaiKhoan(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        // Lấy tên vai trò và đảm bảo định dạng đúng cho Spring Security
        String roleName = user.getRole() != null ? user.getRole().getTenVaiTro() : "NV"; // Mặc định là NV nếu không có vai trò
        // Đảm bảo vai trò bắt đầu bằng "ROLE_" như Spring Security yêu cầu
        String authority = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
        
        // Tạo UserDetails từ thông tin người dùng
        // Password được giữ nguyên như trong DB, PasswordEncoder sẽ xử lý phần còn lại
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getTaiKhoan())
                .password(user.getMatKhau()) // Mật khẩu từ DB (đã được mã hóa hoặc chưa)
                .authorities(authority) // Sử dụng vai trò từ cơ sở dữ liệu
                .accountExpired(!user.getTrangThaiHoatDong())
                .credentialsExpired(false)
                .accountLocked(false)
                .disabled(!user.getTrangThaiHoatDong())
                .build();
    }
}