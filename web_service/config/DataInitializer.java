package com.example.demo.config;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Tạo các vai trò nếu chưa tồn tại
        Role adminRole = roleService.getOrCreateRole("ADMIN");
        Role ntdRole = roleService.getOrCreateRole("NTD");
        Role nvRole = roleService.getOrCreateRole("NV");
        
        // Tạo tài khoản admin mặc định nếu chưa tồn tại
        if (!userService.existsByTaiKhoan("admin")) {
            // Không mã hóa mật khẩu trong quá trình phát triển
            User adminUser = new User("admin", "admin123", "Quản trị viên", "admin@company.com");
            adminUser.setRole(adminRole);
            userService.saveUser(adminUser);
            System.out.println("Tài khoản admin mặc định đã được tạo: admin / admin123");
        }
    }
}