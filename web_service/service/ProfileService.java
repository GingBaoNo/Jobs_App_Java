package com.example.demo.service;

import com.example.demo.entity.Profile;
import com.example.demo.entity.User;
import com.example.demo.repository.ProfileRepository;
import com.example.demo.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    public Optional<Profile> getProfileById(Integer id) {
        return profileRepository.findById(id);
    }

    public Optional<Profile> getProfileByUser(User user) {
        return profileRepository.findByUser(user);
    }

    public Profile saveProfile(Profile profile) {
        return profileRepository.save(profile);
    }

    public Profile updateProfile(Profile profile) {
        return profileRepository.save(profile);
    }

    public void deleteProfile(Integer id) {
        profileRepository.deleteById(id);
    }

    public Profile createProfileForUser(User user, String hoTen, String gioiTinh) {
        Optional<Profile> existingProfile = profileRepository.findByUser(user);
        if (existingProfile.isPresent()) {
            throw new RuntimeException("Hồ sơ cho người dùng này đã tồn tại");
        }

        Profile profile = new Profile(user, hoTen, gioiTinh);
        return saveProfile(profile);
    }

    public Profile updateAvatar(User user, MultipartFile avatarFile) throws IOException {
        Optional<Profile> profileOpt = profileRepository.findByUser(user);
        if (!profileOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy hồ sơ cho người dùng này");
        }

        Profile profile = profileOpt.get();

        // Xóa avatar cũ nếu tồn tại
        if (profile.getUrlAnhDaiDien() != null && !profile.getUrlAnhDaiDien().isEmpty()) {
            // Trích xuất tên file từ URL để xóa
            String oldFileName = profile.getUrlAnhDaiDien().substring(profile.getUrlAnhDaiDien().lastIndexOf("/") + 1);
            FileUploadUtil.deleteFile("uploads/avatars/", oldFileName);
        }

        // Upload avatar mới
        String uploadDir = "uploads/avatars/";
        String fileName = avatarFile.getOriginalFilename();
        String savedFileName = FileUploadUtil.saveFile(uploadDir, fileName, avatarFile);
        String avatarUrl = "/uploads/avatars/" + savedFileName;

        profile.setUrlAnhDaiDien(avatarUrl);

        return profileRepository.save(profile);
    }

    public Profile deleteAvatar(User user) throws IOException {
        Optional<Profile> profileOpt = profileRepository.findByUser(user);
        if (!profileOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy hồ sơ cho người dùng này");
        }

        Profile profile = profileOpt.get();

        // Xóa avatar cũ nếu tồn tại
        if (profile.getUrlAnhDaiDien() != null && !profile.getUrlAnhDaiDien().isEmpty()) {
            // Trích xuất tên file từ URL để xóa
            String oldFileName = profile.getUrlAnhDaiDien().substring(profile.getUrlAnhDaiDien().lastIndexOf("/") + 1);
            FileUploadUtil.deleteFile("uploads/avatars/", oldFileName);
        }

        // Đặt lại URL avatar về null
        profile.setUrlAnhDaiDien(null);

        return profileRepository.save(profile);
    }

    public Profile updateCv(User user, MultipartFile cvFile) throws IOException {
        Optional<Profile> profileOpt = profileRepository.findByUser(user);
        if (!profileOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy hồ sơ cho người dùng này");
        }

        Profile profile = profileOpt.get();

        // Xóa CV cũ nếu tồn tại
        if (profile.getUrlCv() != null && !profile.getUrlCv().isEmpty()) {
            // Trích xuất tên file từ URL để xóa
            String oldFileName = profile.getUrlCv().substring(profile.getUrlCv().lastIndexOf("/") + 1);
            FileUploadUtil.deleteFile("uploads/cvs/", oldFileName);
        }

        // Upload CV mới
        String uploadDir = "uploads/cvs/";
        String fileName = cvFile.getOriginalFilename();
        String savedFileName = FileUploadUtil.saveFile(uploadDir, fileName, cvFile);
        String cvUrl = "/uploads/cvs/" + savedFileName;

        profile.setUrlCv(cvUrl);

        return profileRepository.save(profile);
    }
}