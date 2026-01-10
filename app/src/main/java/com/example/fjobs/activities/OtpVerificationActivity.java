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

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText edtOtp;
    private Button btnVerifyOtp;
    private TextView txtEmailDisplay;
    private TextView txtSubtitle;
    private TextView txtResendOtp;
    private TextView txtBackToLogin;
    
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        // Get email from intent
        email = getIntent().getStringExtra("email");
        
        initViews();
        setupClickListeners();
        updateEmailDisplay();
    }

    private void initViews() {
        edtOtp = findViewById(R.id.edt_otp);
        btnVerifyOtp = findViewById(R.id.btn_verify_otp);
        txtEmailDisplay = findViewById(R.id.txt_email_display);
        txtSubtitle = findViewById(R.id.txt_subtitle);
        txtResendOtp = findViewById(R.id.txt_resend_otp);
        txtBackToLogin = findViewById(R.id.txt_back_to_login);
    }

    private void setupClickListeners() {
        btnVerifyOtp.setOnClickListener(v -> verifyOtp());

        txtResendOtp.setOnClickListener(v -> resendOtp());

        txtBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(OtpVerificationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void updateEmailDisplay() {
        if (email != null && !email.isEmpty()) {
            txtEmailDisplay.setText("Email: " + email);
        }
    }

    private void verifyOtp() {
        String otp = edtOtp.getText().toString().trim();

        if (TextUtils.isEmpty(otp)) {
            edtOtp.setError("Vui lòng nhập mã xác nhận");
            edtOtp.requestFocus();
            return;
        }

        if (otp.length() != 6) {
            edtOtp.setError("Mã xác nhận phải có 6 chữ số");
            edtOtp.requestFocus();
            return;
        }

        // Show loading state
        btnVerifyOtp.setEnabled(false);
        btnVerifyOtp.setText("Đang xác nhận...");

        // Prepare request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("otp", otp);

        // Call API
        ApiService apiService = ApiClient.getApiService();
        Call<ApiResponse> call = apiService.verifyOtp(requestBody);
        
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                btnVerifyOtp.setEnabled(true);
                btnVerifyOtp.setText("Xác nhận mã");

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        // Navigate to reset password screen
                        Intent intent = new Intent(OtpVerificationActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        // Show error from API
                        String errorMessage = apiResponse.getMessage();
                        if (errorMessage == null || errorMessage.isEmpty()) {
                            errorMessage = "Mã xác nhận không đúng hoặc đã hết hạn";
                        }
                        showError(errorMessage);
                    }
                } else {
                    showError("Xác nhận mã thất bại");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                btnVerifyOtp.setEnabled(true);
                btnVerifyOtp.setText("Xác nhận mã");
                
                showError("Lỗi kết nối mạng: " + t.getMessage());
            }
        });
    }

    private void resendOtp() {
        // Show loading state
        txtResendOtp.setEnabled(false);
        txtResendOtp.setText("Đang gửi...");

        // Prepare request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);

        // Call API to send OTP again
        ApiService apiService = ApiClient.getApiService();
        Call<ApiResponse> call = apiService.sendOtp(requestBody);
        
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                txtResendOtp.setEnabled(true);
                txtResendOtp.setText("Gửi lại mã xác nhận");

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(OtpVerificationActivity.this, 
                            "Mã xác nhận mới đã được gửi đến email của bạn", 
                            Toast.LENGTH_LONG).show();
                    } else {
                        String errorMessage = apiResponse.getMessage();
                        if (errorMessage == null || errorMessage.isEmpty()) {
                            errorMessage = "Gửi lại mã xác nhận thất bại";
                        }
                        showError(errorMessage);
                    }
                } else {
                    showError("Gửi lại mã xác nhận thất bại");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                txtResendOtp.setEnabled(true);
                txtResendOtp.setText("Gửi lại mã xác nhận");
                
                showError("Lỗi kết nối mạng: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}