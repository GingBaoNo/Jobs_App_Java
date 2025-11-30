package com.example.fjobs.api;

import com.example.fjobs.models.ApiResponse;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class SafeResponseConverter<T> implements Converter<ResponseBody, T> {
    private final Converter<ResponseBody, T> delegate;
    private final Gson gson = new Gson();

    public SafeResponseConverter(Converter<ResponseBody, T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String responseBody = value.string();
        
        // Kiểm tra nếu response là chuỗi (không phải JSON object)
        if (responseBody.trim().startsWith("\"") && responseBody.trim().endsWith("\"")) {
            // Đây là một chuỗi, tạo ApiResponse từ chuỗi này
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage(responseBody.replaceAll("\"", "").trim());
            apiResponse.setData(null);
            return (T) apiResponse;
        }
        
        // Nếu bắt đầu bằng { hoặc [, có thể là JSON hợp lệ
        if (responseBody.trim().startsWith("{") || responseBody.trim().startsWith("[")) {
            try {
                // Thử parse với converter gốc
                return delegate.convert(ResponseBody.create(value.contentType(), responseBody));
            } catch (JsonParseException e) {
                // Nếu vẫn lỗi, tạo ApiResponse lỗi
                ApiResponse apiResponse = new ApiResponse();
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Lỗi phân tích dữ liệu: " + responseBody);
                apiResponse.setData(null);
                return (T) apiResponse;
            }
        }
        
        // Nếu không phải JSON, tạo ApiResponse từ chuỗi lỗi
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setSuccess(false);
        apiResponse.setMessage("Dữ liệu không hợp lệ: " + responseBody);
        apiResponse.setData(null);
        return (T) apiResponse;
    }
}