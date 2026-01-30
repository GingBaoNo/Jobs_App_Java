package com.example.fjobs.api;

import retrofit2.Call;
import retrofit2.http.GET;

public interface StatusApi {
    // Phương thức kiểm tra trạng thái server
    @GET("/api/status/health")
    Call<String> checkServerStatus();
}