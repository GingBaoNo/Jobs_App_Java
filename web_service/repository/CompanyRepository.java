package com.example.demo.repository;

import com.example.demo.entity.Company;
import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {
    Optional<Company> findByUser(User user);
    List<Company> findByDaXacThuc(Boolean daXacThuc);
    List<Company> findByTrangThai(String trangThai);
    Page<Company> findByDaXacThuc(Boolean daXacThuc, Pageable pageable);
    Page<Company> findByTrangThai(String trangThai, Pageable pageable);
    boolean existsByTenCongTy(String tenCongTy);

    // Phương thức đếm số lượng công ty đã xác thực
    int countByDaXacThucTrue();

    List<Company> findByUserMaNguoiDung(Integer maNhaTuyenDung);

    @Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM Company c WHERE c.user.maNguoiDung = :maNhaTuyenDung")
    void deleteByMaNhaTuyenDung(@org.springframework.data.repository.query.Param("maNhaTuyenDung") Integer maNhaTuyenDung);
}