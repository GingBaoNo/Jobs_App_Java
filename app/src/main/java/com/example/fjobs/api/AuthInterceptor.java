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

        Request.Builder requestBuilder = original.newBuilder();
        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + token);
        }

        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}