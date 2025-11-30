package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
public class ConnectionTestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/test-connection")
    public String testConnection() {
        try (Connection conn = dataSource.getConnection()) {
            return "Kết nối thành công!";
        } catch (SQLException e) {
            return "Kết nối không thành công: " + e.getMessage();
        }
    }
}