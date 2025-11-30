package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_dia_diem")
    private Integer maDiaDiem;

    @ManyToOne
    @JoinColumn(name = "ma_cha")
    private Location parentLocation;

    @OneToMany(mappedBy = "parentLocation")
    private List<Location> childLocations = new ArrayList<>();

    @Column(name = "ten_dia_diem", nullable = false)
    private String tenDiaDiem;

    @Column(name = "cap_do", nullable = false)
    private String capDo; // "Tỉnh/Thành phố" or "Quận/Huyện"

    // Constructors
    public Location() {}

    public Location(String tenDiaDiem, String capDo) {
        this.tenDiaDiem = tenDiaDiem;
        this.capDo = capDo;
    }

    // Getters and Setters
    public Integer getMaDiaDiem() {
        return maDiaDiem;
    }

    public void setMaDiaDiem(Integer maDiaDiem) {
        this.maDiaDiem = maDiaDiem;
    }

    public Location getParentLocation() {
        return parentLocation;
    }

    public void setParentLocation(Location parentLocation) {
        this.parentLocation = parentLocation;
    }

    public List<Location> getChildLocations() {
        return childLocations;
    }

    public void setChildLocations(List<Location> childLocations) {
        this.childLocations = childLocations;
    }

    public String getTenDiaDiem() {
        return tenDiaDiem;
    }

    public void setTenDiaDiem(String tenDiaDiem) {
        this.tenDiaDiem = tenDiaDiem;
    }

    public String getCapDo() {
        return capDo;
    }

    public void setCapDo(String capDo) {
        this.capDo = capDo;
    }
}