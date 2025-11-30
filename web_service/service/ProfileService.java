package com.example.demo.service;

import com.example.demo.entity.Profile;
import com.example.demo.entity.User;
import com.example.demo.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}