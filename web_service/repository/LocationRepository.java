package com.example.demo.repository;

import com.example.demo.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    List<Location> findByParentLocationIsNull(); // Find provinces/cities
    List<Location> findByParentLocationMaDiaDiem(Integer parentMaDiaDiem); // Find districts under a province
}