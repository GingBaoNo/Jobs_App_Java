package com.example.demo.repository;

import com.example.demo.entity.Company;
import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {
    Optional<Company> findByUser(User user);
    List<Company> findByDaXacThuc(Boolean daXacThuc);
    Page<Company> findByDaXacThuc(Boolean daXacThuc, Pageable pageable);
    boolean existsByTenCongTy(String tenCongTy);
}