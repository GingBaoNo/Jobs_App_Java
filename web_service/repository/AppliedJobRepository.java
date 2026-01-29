package com.example.demo.repository;

import com.example.demo.entity.AppliedJob;
import com.example.demo.entity.JobDetail;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppliedJobRepository extends JpaRepository<AppliedJob, Integer> {
    List<AppliedJob> findByEmployee(User employee);
    List<AppliedJob> findByJobDetail(JobDetail jobDetail);
    Optional<AppliedJob> findByEmployeeAndJobDetail(User employee, JobDetail jobDetail);

    // Methods for employer dashboard
    @Query("SELECT aj FROM AppliedJob aj " +
           "JOIN FETCH aj.employee e " +
           "JOIN FETCH aj.jobDetail jd " +
           "JOIN FETCH jd.company c " +
           "JOIN FETCH c.user " +
           "LEFT JOIN FETCH aj.cvProfile " +
           "WHERE aj.jobDetail.company.user.maNguoiDung = :employerId")
    List<AppliedJob> findByJobDetailCompanyUserMaNguoiDung(@Param("employerId") Integer employerId);

    @Query("SELECT aj FROM AppliedJob aj " +
           "JOIN FETCH aj.employee e " +
           "JOIN FETCH aj.jobDetail jd " +
           "JOIN FETCH jd.company c " +
           "JOIN FETCH c.user " +
           "LEFT JOIN FETCH aj.cvProfile " +
           "WHERE aj.jobDetail.company.user.maNguoiDung = :employerId AND aj.jobDetail = :jobDetail")
    List<AppliedJob> findByJobDetailCompanyUserMaNguoiDungAndJobDetail(@Param("employerId") Integer employerId, @Param("jobDetail") JobDetail jobDetail);

    @Query("SELECT aj FROM AppliedJob aj " +
           "JOIN FETCH aj.employee e " +
           "JOIN FETCH aj.jobDetail jd " +
           "JOIN FETCH jd.company c " +
           "JOIN FETCH c.user " +
           "LEFT JOIN FETCH aj.cvProfile " +
           "WHERE aj.maUngTuyen = :id")
    Optional<AppliedJob> findByIdWithDetails(@Param("id") Integer id);
}