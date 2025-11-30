package com.example.demo.controller.api;

import com.example.demo.entity.Role;
import com.example.demo.service.RoleService;
import com.example.demo.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiRoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public ResponseEntity<?> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ApiResponseUtil.success("Roles retrieved successfully", roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable Integer id) {
        return roleService.getRoleById(id)
            .map(role -> ApiResponseUtil.success("Role retrieved successfully", role))
            .orElse(ApiResponseUtil.error("Role not found with id: " + id));
    }

    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody Role role) {
        Role savedRole = roleService.saveRole(role);
        return ApiResponseUtil.created(savedRole);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Integer id, @RequestBody Role role) {
        if (!roleService.getRoleById(id).isPresent()) {
            return ApiResponseUtil.error("Role not found with id: " + id);
        }
        role.setMaVaiTro(id);
        Role updatedRole = roleService.updateRole(role);
        return ApiResponseUtil.success("Role updated successfully", updatedRole);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Integer id) {
        if (!roleService.getRoleById(id).isPresent()) {
            return ApiResponseUtil.error("Role not found with id: " + id);
        }
        roleService.deleteRole(id);
        return ApiResponseUtil.noContent();
    }
}