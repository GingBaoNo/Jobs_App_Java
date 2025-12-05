package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByTaiKhoan(String taiKhoan);
    boolean existsByTaiKhoan(String taiKhoan);
    java.util.List<User> findByTaiKhoanContainingOrTenHienThiContainingOrLienHeContaining(String taiKhoan, String tenHienThi, String lienHe);
    java.util.List<User> findByRole(com.example.demo.entity.Role role);
}