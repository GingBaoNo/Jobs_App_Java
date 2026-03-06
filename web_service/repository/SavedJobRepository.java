package com.example.demo.repository;

import com.example.demo.entity.JobDetail;
import com.example.demo.entity.SavedJob;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, Integer> {
    List<SavedJob> findByUser(User user);
    List<SavedJob> findByJobDetail(JobDetail jobDetail);
    Optional<SavedJob> findByUserAndJobDetail(User user, JobDetail jobDetail);

    @Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM SavedJob s WHERE s.user.maNguoiDung = :maNguoiDung")
    void deleteByMaNguoiDung(@org.springframework.data.repository.query.Param("maNguoiDung") Integer maNguoiDung);

    /**
     * Kiểm tra user đã lưu job chưa
     */
    boolean existsByUserMaNguoiDungAndJobDetailMaCongViec(Integer maNguoiDung, Integer maCongViec);

    /**
     * Lấy danh sách việc đã lưu theo user ID
     */
    List<SavedJob> findByUserMaNguoiDung(Integer maNguoiDung);

    /**
     * Lấy danh sách việc đã lưu với fetch join (tránh LazyLoadingException)
     */
    @Query("SELECT DISTINCT s FROM SavedJob s LEFT JOIN FETCH s.jobDetail jd LEFT JOIN FETCH jd.company LEFT JOIN FETCH jd.workField LEFT JOIN FETCH jd.workType LEFT JOIN FETCH jd.experienceLevel WHERE s.user.maNguoiDung = :maNguoiDung")
    List<SavedJob> findByUserMaNguoiDungWithDetails(@Param("maNguoiDung") Integer maNguoiDung);
}