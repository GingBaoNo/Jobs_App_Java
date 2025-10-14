package com.example.fjobs;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://hip-guests-exist.loca.lt/"; // ✅ Thay thế URL của bạn
    private static RetrofitClient instance;
    private StatusApi statusApi;

    private RetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create()) // Dùng cho response String
                .addConverterFactory(GsonConverterFactory.create())    // Dùng cho response JSON
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
