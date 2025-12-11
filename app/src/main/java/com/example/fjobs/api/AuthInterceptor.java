package com.example.fjobs.api;

import com.example.fjobs.utils.Constants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import android.content.Context;
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

        // Không thêm header cho các endpoint xác thực
        String url = original.url().toString();
        if (url.contains("/auth/login") || url.contains("/auth/register") ||
            url.contains("/register") || url.contains("/login")) {
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

        // Kiểm tra nếu response là lỗi xác thực
        if (response.code() == 401) {
            // Token có thể đã hết hạn, có thể xóa token nếu cần
            // Nhưng không retry để tránh vòng lặp
        }

        return response;
    }
}