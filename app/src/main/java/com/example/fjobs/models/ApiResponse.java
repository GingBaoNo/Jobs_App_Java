package com.example.fjobs.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResponse {
    @SerializedName("success")
    private Boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Object data;

    @SerializedName("error")
    private String error;

    // Constructors
    public ApiResponse() {}

    // Getters and Setters
    public Boolean isSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    // Method to maintain compatibility
    public boolean getSuccess() {
        return success != null && success;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}