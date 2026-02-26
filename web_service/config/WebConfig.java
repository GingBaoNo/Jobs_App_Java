package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cấu hình để phục vụ các file ảnh từ thư mục upload
        registry.addResourceHandler("/uploads/companies/**")
                .addResourceLocations("file:./uploads/companies/");

        // Cấu hình để phục vụ các CV từ thư mục upload
        registry.addResourceHandler("/uploads/cvs/**")
                .addResourceLocations("file:./uploads/cvs/");

        // Cấu hình để phục vụ các ảnh đại diện từ thư mục upload
        registry.addResourceHandler("/uploads/avatars/**")
                .addResourceLocations("file:./uploads/avatars/");

        // Cấu hình để phục vụ các ảnh đại diện từ thư mục upload (tương thích với ứng dụng Android)
        registry.addResourceHandler("/uploads/profiles/**")
                .addResourceLocations("file:./uploads/avatars/");

        // Nếu bạn lưu file ở thư mục ngoài dự án, có thể sử dụng đường dẫn tuyệt đối
        // Ví dụ: registry.addResourceHandler("/uploads/companies/**")
        //            .addResourceLocations("file:C:/uploads/companies/");
        //            registry.addResourceHandler("/uploads/cvs/**")
        //            .addResourceLocations("file:C:/uploads/cvs/");
        //            registry.addResourceHandler("/uploads/avatars/**")
        //            .addResourceLocations("file:C:/uploads/avatars/");
    }
}