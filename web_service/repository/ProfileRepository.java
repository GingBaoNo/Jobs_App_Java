package com.example.demo.repository;

import com.example.demo.entity.Profile;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    Optional<Profile> findByUser(User user);

    @Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM Profile p WHERE p.user.maNguoiDung = :maNguoiTimViec")
    void deleteByMaNguoiTimViec(@org.springframework.data.repository.query.Param("maNguoiTimViec") Integer maNguoiTimViec);
}