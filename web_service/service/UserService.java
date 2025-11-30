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
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
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
        
        return saveUser(user);
    }
}