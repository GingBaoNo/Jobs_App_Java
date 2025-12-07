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
    // Cập nhật URL để sử dụng localhost cho Android Emulator
    // Nếu bạn sử dụng thiết bị thật, thay 10.0.2.2 bằng IP địa phương của máy bạn
    private static final String BASE_URL = "http://172.24.134.32:8080/api/";

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
                    .retryOnConnectionFailure(true);
                    // Mặc định OkHttpClient sẽ giới hạn số lần redirect

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
        // Trả về retrofit hiện có nếu đã được khởi tạo, nếu không sẽ cần Context
        if (retrofit != null) {
            return retrofit;
        }
        // Nếu chưa có context, trả về null và các nơi sử dụng cần truyền context
        return null; // Cần truyền context khi chưa được khởi tạo
    }

    // Phương thức khởi tạo có context
    public static void initialize(Context ctx) {
        getRetrofitInstance(ctx);
    }
}