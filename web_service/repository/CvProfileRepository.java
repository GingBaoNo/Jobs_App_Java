package com.example.demo.repository;

import com.example.demo.entity.CvProfile;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CvProfileRepository extends JpaRepository<CvProfile, Integer> {
    List<CvProfile> findByNguoiTimViec(User nguoiTimViec);

    List<CvProfile> findByNguoiTimViecAndCongKhai(User nguoiTimViec, Boolean congKhai);

    CvProfile findByNguoiTimViecAndLaMacDinh(User nguoiTimViec, Boolean laMacDinh);

    boolean existsByNguoiTimViecAndTenHoSo(User nguoiTimViec, String tenHoSo);

    @Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM CvProfile c WHERE c.nguoiTimViec.maNguoiDung = :maNguoiTimViec")
    void deleteByMaNguoiTimViec(@org.springframework.data.repository.query.Param("maNguoiTimViec") Integer maNguoiTimViec);
}