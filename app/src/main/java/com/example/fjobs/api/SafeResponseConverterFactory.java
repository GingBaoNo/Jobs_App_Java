package com.example.fjobs.api;

import com.example.fjobs.models.ApiResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class SafeResponseConverterFactory extends Converter.Factory {

    private final Converter.Factory delegate;

    public SafeResponseConverterFactory(Converter.Factory delegate) {
        this.delegate = delegate;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        // Chỉ áp dụng xử lý đặc biệt cho ApiResponse
        if (type == ApiResponse.class) {
            return new SafeResponseConverter<>(delegate.responseBodyConverter(type, annotations, retrofit));
        }
        // Đối với các loại khác, sử dụng converter gốc
        return delegate.responseBodyConverter(type, annotations, retrofit);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        // Delegate việc chuyển đổi request body cho converter gốc (GsonConverterFactory)
        return delegate.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
    }
}