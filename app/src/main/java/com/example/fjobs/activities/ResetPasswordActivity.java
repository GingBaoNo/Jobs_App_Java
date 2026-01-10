package com.example.fjobs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText edtNewPassword;
    private EditText edtConfirmPassword;
    private Button btnResetPassword;
    private TextView txtEmailDisplay;
    private TextView txtBackToLogin;
    
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Get email from intent
        email = getIntent().getStringExtra("email");
        
        initViews();
        setupClickListeners();
        updateEmailDisplay();
    }

    private void initViews() {
        edtNewPassword = findViewById(R.id.edt_new_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        txtEmailDisplay = findViewById(R.id.txt_email_display);
        txtBackToLogin = findViewById(R.id.txt_back_to_login);
    }

    private void setupClickListeners() {
        btnResetPassword.setOnClickListener(v -> resetPassword());

        txtBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void updateEmailDisplay() {
        if (email != null && !email.isEmpty()) {
            txtEmailDisplay.setText("Email: " + email);
        }
    }

    private void resetPassword() {
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword)) {
            edtNewPassword.setError("Vui lòng nhập mật khẩu mới");
            edtNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            edtNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            edtNewPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            edtConfirmPassword.setError("Vui lòng xác nhận mật khẩu mới");
            edtConfirmPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            edtConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            edtConfirmPassword.requestFocus();
            return;
        }

        // Show loading state
        btnResetPassword.setEnabled(false);
        btnResetPassword.setText("Đang đặt lại...");

        // Prepare request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("newPassword", newPassword);
        requestBody.put("confirmNewPassword", confirmPassword);

        // Call API
        ApiService apiService = ApiClient.getApiService();
        Call<ApiResponse> call = apiService.resetPassword(requestBody);
        
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                btnResetPassword.setEnabled(true);
                btnResetPassword.setText("Đặt lại mật khẩu");

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(ResetPasswordActivity.this, 
                            "Mật khẩu đã được đặt lại thành công", 
                            Toast.LENGTH_LONG).show();
                        
                        // Navigate back to login screen
                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Show error from API
                        String errorMessage = apiResponse.getMessage();
                        if (errorMessage == null || errorMessage.isEmpty()) {
                            errorMessage = "Đặt lại mật khẩu thất bại";
                        }
                        showError(errorMessage);
                    }
                } else {
                    showError("Đặt lại mật khẩu thất bại");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                btnResetPassword.setEnabled(true);
                btnResetPassword.setText("Đặt lại mật khẩu");
                
                showError("Lỗi kết nối mạng: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}