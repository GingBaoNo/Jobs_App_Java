package com.example.demo.repository;

import com.example.demo.entity.Company;
import com.example.demo.entity.JobDetail;
import com.example.demo.entity.WorkField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JobDetailRepository extends JpaRepository<JobDetail, Integer> {
    List<JobDetail> findByCompany(Company company);
    List<JobDetail> findByTrangThaiDuyet(String trangThaiDuyet);
    List<JobDetail> findByTrangThaiDuyetAndTrangThaiTinTuyen(String trangThaiDuyet, String trangThaiTinTuyen);
    List<JobDetail> findByNgayKetThucTuyenDungAfterAndTrangThaiDuyetAndTrangThaiTinTuyen(LocalDate ngayHienTai, String trangThaiDuyet, String trangThaiTinTuyen);
    List<JobDetail> findByWorkField(WorkField workField);
    
    List<JobDetail> findTop10ByTrangThaiDuyetAndTrangThaiTinTuyenOrderByLuotXemDesc(String trangThaiDuyet, String trangThaiTinTuyen);

    @Query("SELECT j FROM JobDetail j WHERE j.tieuDe LIKE %:keyword% OR j.chiTiet LIKE %:keyword%")
    List<JobDetail> findByTieuDeContainingOrChiTietContaining(@Param("keyword") String keyword);
}