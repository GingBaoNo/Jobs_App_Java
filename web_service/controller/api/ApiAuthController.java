package com.example.demo.controller.api;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.service.UserService;
import com.example.demo.utils.ApiResponseUtil;
import com.example.demo.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        } catch (Exception e) {
            return ApiResponseUtil.error("Invalid credentials");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        // Get user details to include in response
        User user = userService.getUserByTaiKhoan(loginRequest.getUsername()).orElse(null);
        if (user == null) {
            return ApiResponseUtil.error("User not found");
        }

        // Create response with JWT and user info
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("user", user);

        return ApiResponseUtil.success("Login successful", response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            // Find or create default role
            Role role = null;
            if (registerRequest.getRole() != null && !registerRequest.getRole().isEmpty()) {
                Optional<Role> existingRole = roleRepository.findByTenVaiTro(registerRequest.getRole());
                if (existingRole.isPresent()) {
                    role = existingRole.get();
                } else {
                    // If role doesn't exist, use default role (NV - Employee)
                    role = roleRepository.findByTenVaiTro("NV").orElse(null);
                }
            } else {
                // Default to NV role if none specified
                role = roleRepository.findByTenVaiTro("NV").orElse(null);
            }
            
            if (role == null) {
                return ApiResponseUtil.error("Default role not found. Please initialize roles in the database.");
            }
            
            User user = userService.registerUser(
                registerRequest.getUsername(),
                registerRequest.getPassword(),
                registerRequest.getDisplayName(),
                registerRequest.getContact(),
                role
            );
            
            // Create success response with token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getTaiKhoan());
            final String jwt = jwtUtil.generateToken(userDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("user", user);
            
            return ApiResponseUtil.success("Registration successful", response);
        } catch (Exception e) {
            return ApiResponseUtil.error(e.getMessage());
        }
    }
    
    // Request classes
    public static class LoginRequest {
        private String username;
        private String password;

        // Getters and setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
    
    public static class RegisterRequest {
        private String username;
        private String password;
        private String displayName;
        private String contact;
        private String role; // ADMIN, NTD, NV

        // Getters and setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}