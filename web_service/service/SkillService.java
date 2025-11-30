package com.example.demo.service;

import com.example.demo.entity.Skill;
import com.example.demo.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SkillService {
    
    @Autowired
    private SkillRepository skillRepository;
    
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }
    
    public Optional<Skill> getSkillById(Integer id) {
        return skillRepository.findById(id);
    }
    
    public Skill saveSkill(Skill skill) {
        return skillRepository.save(skill);
    }
    
    public Skill updateSkill(Skill skill) {
        return skillRepository.save(skill);
    }
    
    public void deleteSkill(Integer id) {
        skillRepository.deleteById(id);
    }
}