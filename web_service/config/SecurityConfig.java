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
import org.springframework.web.filter.CharacterEncodingFilter;

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
            .authorizeHttpRequests(authz -> authz
                // Public web pages and resources
                .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/images/**", "/webjars/**", "/uploads/**", "/uploads/cvs/**", "/uploads/avatars/**", "/uploads/profiles/**").permitAll()
                // Public access to job/company pages
                .requestMatchers("/jobs/**", "/companies/**", "/job/**", "/company/**").permitAll()
                // Public API endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/v1/job-details/**").permitAll()
                .requestMatchers("/api/v1/companies/**").permitAll()
                .requestMatchers("/api/v1/work-fields").permitAll()
                .requestMatchers("/api/v1/work-fields/**").permitAll()
                .requestMatchers("/api/v1/work-disciplines").permitAll()
                .requestMatchers("/api/v1/work-disciplines/**").permitAll()
                .requestMatchers("/api/v1/job-positions").permitAll()
                .requestMatchers("/api/v1/job-positions/**").permitAll()
                .requestMatchers("/api/v1/experience-levels").permitAll()
                .requestMatchers("/api/v1/experience-levels/**").permitAll()
                .requestMatchers("/api/v1/work-types").permitAll()
                .requestMatchers("/api/v1/work-types/**").permitAll()
                // All other API endpoints - need authentication via JWT
                .requestMatchers("/api/v1/**").authenticated()
                // Web pages with role-based access
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/employer/**").hasRole("NTD")
                .requestMatchers("/employee/**").hasRole("NV")
                .anyRequest().authenticated()
            )
            // Configure form login for web pages only, and specify redirect behavior for non-web requests
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("taiKhoan")
                .passwordParameter("matKhau")
                .defaultSuccessUrl("/")
                .failureUrl("/login?error=true")
                .permitAll()
                // Only apply form login redirect to web requests, not API requests
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .csrf(AbstractHttpConfigurer::disable)
            // Add JWT filter before the authentication filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}