package com.example.demo.service;

import com.example.demo.entity.ExperienceLevel;
import com.example.demo.repository.ExperienceLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExperienceLevelService {

    @Autowired
    private ExperienceLevelRepository experienceLevelRepository;

    public List<ExperienceLevel> getAllExperienceLevels() {
        return experienceLevelRepository.findAll();
    }

    public Optional<ExperienceLevel> getExperienceLevelById(Integer id) {
        return experienceLevelRepository.findById(id);
    }

    public ExperienceLevel saveExperienceLevel(ExperienceLevel experienceLevel) {
        return experienceLevelRepository.save(experienceLevel);
    }

    public ExperienceLevel updateExperienceLevel(ExperienceLevel experienceLevel) {
        return experienceLevelRepository.save(experienceLevel);
    }

    public void deleteExperienceLevel(Integer id) {
        experienceLevelRepository.deleteById(id);
    }

    public List<ExperienceLevel> getExperienceLevelsBySearch(String keyword) {
        return experienceLevelRepository.findByTenCapDoContaining(keyword);
    }
}