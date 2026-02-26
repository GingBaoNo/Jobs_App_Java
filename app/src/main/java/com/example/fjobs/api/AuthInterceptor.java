package com.example.fjobs.api;

import com.example.fjobs.utils.Constants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class AuthInterceptor implements Interceptor {
    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        // Lấy token từ SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(
            Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Constants.KEY_TOKEN, null);

        // Không thêm header cho các endpoint công khai (public endpoints)
        String url = original.url().toString();
        boolean isPublicEndpoint = url.contains("/auth/login") 
            || url.contains("/auth/register")
            || url.contains("/v1/job-details")
            || url.contains("/v1/companies")
            || url.contains("/v1/work-fields")
            || url.contains("/v1/work-types")
            || url.contains("/v1/job-positions")
            || url.contains("/v1/experience-levels")
            || url.contains("/v1/work-disciplines")
            || url.contains("/v1/home");
        
        if (isPublicEndpoint) {
            return chain.proceed(original);
        }

        Request.Builder requestBuilder = original.newBuilder();

        // Chỉ thêm header nếu có token và request chưa có header Authorization
        if (token != null && original.header("Authorization") == null) {
            requestBuilder.addHeader("Authorization", "Bearer " + token);
        }

        Request request = requestBuilder.build();

        // Gọi chain.proceed một lần duy nhất và trả về kết quả
        Response response = chain.proceed(request);

        // Kiểm tra nếu response là lỗi xác thực (401) hoặc redirect (302)
        if (response.code() == 401 || response.code() == 302) {
            // Token có thể đã hết hạn, xóa token để user đăng nhập lại
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(Constants.KEY_TOKEN);
            editor.apply();

            // Gửi broadcast để các Activity/Fragment biết và xử lý
            Intent intent = new Intent("com.example.fjobs.SESSION_EXPIRED");
            intent.setPackage("com.example.fjobs");
            context.sendBroadcast(intent);

            // Không retry để tránh vòng lặp
        }

        return response;
    }
}