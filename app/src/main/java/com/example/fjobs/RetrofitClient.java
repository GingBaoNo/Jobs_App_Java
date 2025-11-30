package com.example.fjobs;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import com.example.fjobs.api.SafeResponseConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RetrofitClient {

    private static final String BASE_URL = "http://192.168.1.8:8080/"; // ✅ Thay thế URL của bạn
    private static RetrofitClient instance;
    private StatusApi statusApi;

    private RetrofitClient() {
        Gson gson = new GsonBuilder()
                .setLenient()  // Cho phép JSON có định dạng không nghiêm ngặt
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create()) // Dùng cho response String
                .addConverterFactory(new SafeResponseConverterFactory(GsonConverterFactory.create(gson)))    // Dùng cho response JSON
                .build();

        statusApi = retrofit.create(StatusApi.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public StatusApi getStatusApi() {
        return statusApi;
    }
}
