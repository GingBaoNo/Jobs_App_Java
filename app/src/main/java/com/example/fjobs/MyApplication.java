package com.example.fjobs;

import android.app.Application;
import android.content.res.Resources;

import com.example.fjobs.api.ApiClient;
import com.example.fjobs.utils.ServerConfig;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Khởi tạo ServerConfig với IP từ tài nguyên
        ServerConfig.initializeFromResources(this);

        // Khởi tạo ApiClient với Context
        ApiClient.initialize(this);
    }
}