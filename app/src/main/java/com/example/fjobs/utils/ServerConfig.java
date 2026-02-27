package com.example.fjobs.utils;

import android.content.Context;
import android.content.res.Resources;

public class ServerConfig {
    // Địa chỉ máy chủ - được thiết lập từ tài nguyên
    private static String SERVER_IP = "192.168.1.8"; // Giá trị mặc định, sẽ được thay thế từ tài nguyên

    public static String getBaseUrl() {
        return String.format("http://%s:8080", SERVER_IP);
    }

    public static String getApiBaseUrl() {
        return String.format("http://%s:8080/api", SERVER_IP);
    }

    public static String getWebSocketUrl() {
        return String.format("ws://%s:8080/websocket", SERVER_IP);
    }

    public static String getServerIp() {
        return SERVER_IP;
    }

    public static void setServerIp(String ip) {
        SERVER_IP = ip;
    }

    // Phương thức để lấy IP từ resources (có thể được gọi từ Application class)
    public static void initializeFromResources(Context context) {
        try {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("server_ip", "string", context.getPackageName());
            if (resourceId != 0) {
                String ipFromResource = resources.getString(resourceId);
                if (ipFromResource != null && !ipFromResource.isEmpty()) {
                    SERVER_IP = ipFromResource;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}