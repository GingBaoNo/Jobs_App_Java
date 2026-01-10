package com.example.demo.repository;

import com.example.demo.entity.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, Integer> {
    Optional<OtpCode> findByEmailAndOtpCodeAndDaSuDungFalse(String email, String otpCode);

    @Query("SELECT o FROM OtpCode o WHERE o.email = :email AND o.daSuDung = false ORDER BY o.ngayTao DESC")
    List<OtpCode> findLatestUnusedOtpsByEmail(@Param("email") String email);

    @Query("SELECT o FROM OtpCode o WHERE o.email = :email AND o.otpCode = :otpCode")
    Optional<OtpCode> findByEmailAndOtpCode(@Param("email") String email, @Param("otpCode") String otpCode);
}