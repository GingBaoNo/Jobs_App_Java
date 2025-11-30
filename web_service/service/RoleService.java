package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    
    @Autowired
    private RoleRepository roleRepository;
    
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    
    public Optional<Role> getRoleById(Integer id) {
        return roleRepository.findById(id);
    }
    
    public Optional<Role> getRoleByTenVaiTro(String tenVaiTro) {
        return roleRepository.findByTenVaiTro(tenVaiTro);
    }
    
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }
    
    public Role updateRole(Role role) {
        return roleRepository.save(role);
    }
    
    public void deleteRole(Integer id) {
        roleRepository.deleteById(id);
    }
    
    public Role getOrCreateRole(String roleName) {
        Optional<Role> existingRole = roleRepository.findByTenVaiTro(roleName);
        if (existingRole.isPresent()) {
            return existingRole.get();
        } else {
            Role newRole = new Role(roleName);
            return roleRepository.save(newRole);
        }
    }
}