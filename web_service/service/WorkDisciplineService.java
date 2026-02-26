package com.example.demo.service;

import com.example.demo.entity.WorkDiscipline;
import com.example.demo.repository.WorkDisciplineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkDisciplineService {

    @Autowired
    private WorkDisciplineRepository workDisciplineRepository;

    public List<WorkDiscipline> getAllWorkDisciplines() {
        return workDisciplineRepository.findAll();
    }

    public WorkDiscipline getWorkDisciplineById(Integer id) {
        return workDisciplineRepository.findById(id).orElse(null);
    }

    public WorkDiscipline saveWorkDiscipline(WorkDiscipline workDiscipline) {
        return workDisciplineRepository.save(workDiscipline);
    }

    public WorkDiscipline updateWorkDiscipline(WorkDiscipline workDiscipline) {
        return workDisciplineRepository.save(workDiscipline);
    }

    public void deleteWorkDiscipline(Integer id) {
        workDisciplineRepository.deleteById(id);
    }

    public List<WorkDiscipline> getWorkDisciplinesByWorkFieldId(Integer workFieldId) {
        return workDisciplineRepository.findByWorkField_MaLinhVuc(workFieldId);
    }

    public List<WorkDiscipline> getWorkDisciplinesBySearch(String keyword) {
        return workDisciplineRepository.findByTenNganhContaining(keyword);
    }
}