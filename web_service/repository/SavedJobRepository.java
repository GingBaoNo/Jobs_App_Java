package com.example.demo.repository;

import com.example.demo.entity.JobDetail;
import com.example.demo.entity.SavedJob;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
}