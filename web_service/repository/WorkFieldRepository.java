package com.example.demo.repository;

import com.example.demo.entity.WorkField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkFieldRepository extends JpaRepository<WorkField, Integer> {
    java.util.List<WorkField> findByTenLinhVucContainingIgnoreCase(String tenLinhVuc);

    // Phương thức tìm kiếm các lĩnh vực theo số lượng công việc - Native Query không phân trang
    @org.springframework.data.jpa.repository.Query(
        value = "SELECT w.ma_linh_vuc, COUNT(j.ma_cong_viec) AS job_count FROM work_fields w INNER JOIN jobdetails j ON w.ma_linh_vuc = j.ma_linh_vuc WHERE j.trang_thai_duyet = N'Đã duyệt' AND j.trang_thai_tin_tuyen = N'Mở' GROUP BY w.ma_linh_vuc ORDER BY COUNT(j.ma_cong_viec) DESC",
        nativeQuery = true
    )
    java.util.List<Object[]> findAllWorkFieldsByJobCountNative();

    // Phương thức tìm kiếm theo danh sách ID
    java.util.List<WorkField> findByMaLinhVucIn(java.util.List<Integer> ids);
}