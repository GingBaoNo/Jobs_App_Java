package com.example.demo.service;

import com.example.demo.entity.WorkField;
import com.example.demo.repository.WorkFieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class WorkFieldService {
    
    @Autowired
    private WorkFieldRepository workFieldRepository;
    
    public List<WorkField> getAllWorkFields() {
        return workFieldRepository.findAll();
    }
    
    public WorkField getWorkFieldById(Integer id) {
        return workFieldRepository.findById(id).orElse(null);
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

    public List<WorkField> getWorkFieldsBySearch(String search) {
        return workFieldRepository.findByTenLinhVucContainingIgnoreCase(search);
    }

    public List<WorkField> getPopularWorkFields(int limit) {
        // Lấy tất cả các lĩnh vực có nhiều công việc nhất (không giới hạn ở repo, dùng Native Query)
        List<Object[]> allPopularFields = workFieldRepository.findAllWorkFieldsByJobCountNative();

        // Lấy danh sách ID của các lĩnh vực phổ biến, giới hạn số lượng theo 'limit'
        List<Integer> fieldIds = new ArrayList<>();
        int count = 0;
        for (Object[] fieldData : allPopularFields) {
            if (count >= limit) {
                break;
            }
            // fieldData[0] là maLinhVuc (Number)
            Number maLinhVucNumber = (Number) fieldData[0];
            if (maLinhVucNumber != null) {
                fieldIds.add(maLinhVucNumber.intValue());
                count++;
            }
        }

        // Lấy thông tin chi tiết của các lĩnh vực dựa trên ID
        if (fieldIds.isEmpty()) {
            return new ArrayList<>(); // Trả về danh sách rỗng nếu không có ID nào
        }
        return workFieldRepository.findByMaLinhVucIn(fieldIds);
    }
}