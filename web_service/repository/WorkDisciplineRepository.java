package com.example.demo.repository;

import com.example.demo.entity.WorkDiscipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkDisciplineRepository extends JpaRepository<WorkDiscipline, Integer> {
    List<WorkDiscipline> findByWorkField_MaLinhVuc(Integer workFieldId);

    @Query("SELECT wd FROM WorkDiscipline wd WHERE wd.tenNganh LIKE CONCAT('%', :keyword, '%')")
    List<WorkDiscipline> findByTenNganhContaining(@Param("keyword") String keyword);
}