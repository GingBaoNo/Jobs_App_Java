package com.example.fjobs.api;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

public class ApiClient {
    private static Retrofit retrofit;
    private static Context context;
    // Cập nhật URL để sử dụng địa chỉ máy chủ thực tế
    // Nếu dùng Android Emulator: sử dụng 10.0.2.2 thay cho localhost
    // Nếu dùng thiết bị thật: sử dụng IP địa phương của máy chủ (ví dụ: 192.168.1.x)
    // private static final String BASE_URL = "http://10.0.2.2:8080/api/"; // Dùng cho Android Emulator
    private static final String BASE_URL = "http://192.168.1.8:8080/api/"; // Dùng cho thiết bị thật

    public static Retrofit getRetrofitInstance(Context ctx) {
        if (retrofit == null) {
            context = ctx;

            Gson gson = new GsonBuilder()
                    .setLenient()  // Cho phép JSON có định dạng không nghiêm ngặt
                    .create();

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(false)  // Tắt retry hoàn toàn
                    .followRedirects(false)  // Tắt redirect để tránh vòng lặp
                    .followSslRedirects(false);  // Tắt SSL redirect

            if (context != null) {
                httpClient.addInterceptor(new AuthInterceptor(context));
            }

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(new SafeResponseConverterFactory(GsonConverterFactory.create(gson)))
                    .build();
        }
        return retrofit;
    }

    // Thêm phương thức không tham số để giữ tính tương thích
    public static Retrofit getRetrofitInstance() {
        // Trả về retrofit hiện có nếu đã được khởi tạo, nếu không thì không thể tạo được
        if (retrofit != null) {
            return retrofit;
        }
        throw new IllegalStateException("ApiClient chưa được khởi tạo với Context. Vui lòng gọi initialize(Context) trước.");
    }

    // Phương thức khởi tạo có context
    public static void initialize(Context ctx) {
        getRetrofitInstance(ctx);
    }

    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
    }
}