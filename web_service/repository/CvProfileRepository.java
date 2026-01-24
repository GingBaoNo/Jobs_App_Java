package com.example.demo.repository;

import com.example.demo.entity.CvProfile;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CvProfileRepository extends JpaRepository<CvProfile, Integer> {
    List<CvProfile> findByNguoiTimViec(User nguoiTimViec);
    
    List<CvProfile> findByNguoiTimViecAndCongKhai(User nguoiTimViec, Boolean congKhai);
    
    CvProfile findByNguoiTimViecAndLaMacDinh(User nguoiTimViec, Boolean laMacDinh);
    
    boolean existsByNguoiTimViecAndTenHoSo(User nguoiTimViec, String tenHoSo);
}