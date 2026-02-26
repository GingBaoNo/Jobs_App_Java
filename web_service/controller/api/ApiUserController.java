package com.example.demo.controller.api;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiUserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ApiResponseUtil.success("Users retrieved successfully", users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id)
            .map(user -> ApiResponseUtil.success("User retrieved successfully", user))
            .orElse(ApiResponseUtil.error("User not found with id: " + id));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        User savedUser = userService.saveUser(user);
        return ApiResponseUtil.created(savedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody User user) {
        if (!userService.getUserById(id).isPresent()) {
            return ApiResponseUtil.error("User not found with id: " + id);
        }
        user.setMaNguoiDung(id);
        User updatedUser = userService.updateUser(user);
        return ApiResponseUtil.success("User updated successfully", updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        if (!userService.getUserById(id).isPresent()) {
            return ApiResponseUtil.error("User not found with id: " + id);
        }
        userService.deleteUser(id);
        return ApiResponseUtil.noContent();
    }
}