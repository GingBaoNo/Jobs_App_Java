package com.example.demo.service;

import com.example.demo.entity.WorkField;
import com.example.demo.repository.WorkFieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkFieldService {
    
    @Autowired
    private WorkFieldRepository workFieldRepository;
    
    public List<WorkField> getAllWorkFields() {
        return workFieldRepository.findAll();
    }
    
    public Optional<WorkField> getWorkFieldById(Integer id) {
        return workFieldRepository.findById(id);
    }
    
    public WorkField saveWorkField(WorkField workField) {
        return workFieldRepository.save(workField);
    }
    
    public WorkField updateWorkField(WorkField workField) {
        return workFieldRepository.save(workField);
    }
    
    public void deleteWorkField(Integer id) {
        workFieldRepository.deleteById(id);
    }
}