package com.example.fjobs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fjobs.R;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.ApiResponse;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnSendOtp;
    private TextView txtBackToLogin;
    private TextView txtInfoMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        edtEmail = findViewById(R.id.edt_email);
        btnSendOtp = findViewById(R.id.btn_send_otp);
        txtBackToLogin = findViewById(R.id.txt_back_to_login);
        txtInfoMessage = findViewById(R.id.txt_info_message);
    }

    private void setupClickListeners() {
        btnSendOtp.setOnClickListener(v -> sendOtp());

        txtBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void sendOtp() {
        String email = edtEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Vui lòng nhập email");
            edtEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Vui lòng nhập email hợp lệ");
            edtEmail.requestFocus();
            return;
        }

        // Show loading state
        btnSendOtp.setEnabled(false);
        btnSendOtp.setText("Đang gửi...");

        // Prepare request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);

        // Call API
        ApiService apiService = ApiClient.getApiService();
        Call<ApiResponse> call = apiService.sendOtp(requestBody);
        
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                btnSendOtp.setEnabled(true);
                btnSendOtp.setText("Gửi mã xác nhận");

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        // Show success message
                        Toast.makeText(ForgotPasswordActivity.this, 
                            "Mã xác nhận đã được gửi đến email của bạn", 
                            Toast.LENGTH_LONG).show();
                        
                        // Show info message
                        txtInfoMessage.setVisibility(View.VISIBLE);
                        
                        // Navigate to OTP verification screen
                        Intent intent = new Intent(ForgotPasswordActivity.this, OtpVerificationActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        // Show error from API
                        String errorMessage = apiResponse.getMessage();
                        if (errorMessage == null || errorMessage.isEmpty()) {
                            errorMessage = "Gửi mã xác nhận thất bại";
                        }
                        showError(errorMessage);
                    }
                } else {
                    showError("Gửi mã xác nhận thất bại");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                btnSendOtp.setEnabled(true);
                btnSendOtp.setText("Gửi mã xác nhận");
                
                showError("Lỗi kết nối mạng: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}