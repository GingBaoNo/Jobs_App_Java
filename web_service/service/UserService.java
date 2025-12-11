package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProfileService profileService;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByTaiKhoan(String taiKhoan) {
        return userRepository.findByTaiKhoan(taiKhoan);
    }
    
    public User saveUser(User user) {
        // Không mã hóa mật khẩu trong quá trình phát triển
        return userRepository.save(user);
    }
    
    public User updateUser(User user) {
        // Không mã hóa mật khẩu trong quá trình phát triển
        return userRepository.save(user);
    }
    
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
    
    public boolean existsByTaiKhoan(String taiKhoan) {
        return userRepository.existsByTaiKhoan(taiKhoan);
    }
    
    public User registerUser(String taiKhoan, String matKhau, String tenHienThi, String lienHe, Role role) {
        if (userRepository.existsByTaiKhoan(taiKhoan)) {
            throw new RuntimeException("Tài khoản đã tồn tại");
        }

        User user = new User(taiKhoan, matKhau, tenHienThi, lienHe);
        user.setRole(role);

        User savedUser = saveUser(user);

        // Tự động tạo hồ sơ mặc định cho người dùng mới nếu là NV (người xin việc)
        if ("NV".equals(role.getTenVaiTro())) {
            try {
                profileService.createProfileForUser(savedUser, tenHienThi, "Nam"); // Mặc định giới tính là Nam, người dùng có thể cập nhật sau
            } catch (Exception e) {
                // Nếu tạo hồ sơ thất bại, log lỗi nhưng không làm hỏng quá trình đăng ký
                System.err.println("Không thể tạo hồ sơ mặc định cho người dùng " + taiKhoan + ": " + e.getMessage());
            }
        }

        return savedUser;
    }

    public List<User> getUsersBySearch(String search) {
        return userRepository.findByTaiKhoanContainingOrTenHienThiContainingOrLienHeContaining(search, search, search);
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }
}