package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Định nghĩa handler cho các file upload
        registry.addResourceHandler("/uploads/cvs/**")
                .addResourceLocations("file:uploads/cvs/")
                .setCachePeriod(3600); // Cache trong 1 giờ

        registry.addResourceHandler("/uploads/avatars/**")
                .addResourceLocations("file:uploads/avatars/")
                .setCachePeriod(3600); // Cache trong 1 giờ

        // Cấu hình để phục vụ các ảnh đại diện từ thư mục upload (tương thích với ứng dụng Android)
        registry.addResourceHandler("/uploads/profiles/**")
                .addResourceLocations("file:uploads/avatars/")
                .setCachePeriod(3600); // Cache trong 1 giờ

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/")
                .setCachePeriod(3600);
    }
}