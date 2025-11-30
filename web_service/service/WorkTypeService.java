package com.example.demo.service;

import com.example.demo.entity.WorkType;
import com.example.demo.repository.WorkTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkTypeService {
    
    @Autowired
    private WorkTypeRepository workTypeRepository;
    
    public List<WorkType> getAllWorkTypes() {
        return workTypeRepository.findAll();
    }
    
    public Optional<WorkType> getWorkTypeById(Integer id) {
        return workTypeRepository.findById(id);
    }
    
    public WorkType saveWorkType(WorkType workType) {
        return workTypeRepository.save(workType);
    }
    
    public WorkType updateWorkType(WorkType workType) {
        return workTypeRepository.save(workType);
    }
    
    public void deleteWorkType(Integer id) {
        workTypeRepository.deleteById(id);
    }
}