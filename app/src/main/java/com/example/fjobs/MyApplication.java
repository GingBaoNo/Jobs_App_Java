package com.example.fjobs;

import android.app.Application;
import android.content.res.Resources;

import com.example.fjobs.utils.ServerConfig;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Khởi tạo ServerConfig với IP từ tài nguyên
        ServerConfig.initializeFromResources(this);
    }
}