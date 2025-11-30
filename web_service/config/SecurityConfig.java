package com.example.demo.config;

import com.example.demo.config.JwtAuthenticationFilter;
import com.example.demo.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Trả về NoOpPasswordEncoder để không mã hóa mật khẩu trong quá trình phát triển
        return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public UserDetailsService userDetailsService(CustomUserDetailsService customUserDetailsService) {
        return customUserDetailsService;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
            // For web pages (existing functionality) - keep original configuration
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/images/**", "/webjars/**", "/uploads/**").permitAll()
                // Allow public access to job and company pages
                .requestMatchers("/jobs/**", "/companies/**", "/job/**", "/company/**").permitAll()
                // API authentication endpoints
                .requestMatchers("/api/auth/**").permitAll()
                // API job detail endpoints for Android
                .requestMatchers("/api/v1/job-details/**").permitAll()
                // API company endpoints for Android
                .requestMatchers("/api/v1/companies/**").permitAll()
                // API profile endpoints - cần xác thực
                .requestMatchers("/api/v1/profiles/**").authenticated()
                // API applied jobs endpoints - cần xác thực
                .requestMatchers("/api/v1/applied-jobs/**").authenticated()
                // API saved jobs endpoints - cần xác thực
                .requestMatchers("/api/v1/saved-jobs/**").authenticated()
                // Admin endpoints
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Employer endpoints
                .requestMatchers("/employer/**").hasRole("NTD")
                // Employee endpoints
                .requestMatchers("/employee/**").hasRole("NV")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("taiKhoan")
                .passwordParameter("matKhau")
                .defaultSuccessUrl("/")
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for development
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}