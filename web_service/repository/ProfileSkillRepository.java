package com.example.demo.repository;

import com.example.demo.entity.Profile;
import com.example.demo.entity.ProfileSkill;
import com.example.demo.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileSkillRepository extends JpaRepository<ProfileSkill, Integer> {
    List<ProfileSkill> findByProfile(Profile profile);
    List<ProfileSkill> findBySkill(Skill skill);
}