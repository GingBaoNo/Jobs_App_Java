package com.example.fjobs.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fjobs.R;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.models.LoginRequest;
import com.example.fjobs.models.User;
import com.example.fjobs.utils.Constants;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupClickListeners();

        // Check if user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        if (sharedPreferences.getString(Constants.KEY_TOKEN, null) != null) {
            // User already logged in, redirect to main activity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            performLogin(username, password);
        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void performLogin(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<ApiResponse> call = apiService.login(loginRequest);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        // Trích xuất token từ phản hồi API
                        Object dataObj = apiResponse.getData();
                        String token = null;

                        if (dataObj instanceof java.util.Map) {
                            java.util.Map<String, Object> dataMap = (java.util.Map<String, Object>) dataObj;
                            Object tokenObj = dataMap.get("token");
                            if (tokenObj != null) {
                                token = tokenObj.toString();
                            }
                        }

                        if (token != null) {
                            // Lưu thông tin người dùng vào SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putString(Constants.KEY_TOKEN, token);
                            editor.putString(Constants.KEY_USERNAME, username);

                            // Nếu có thông tin user trong phản hồi, có thể lưu thêm thông tin
                            if (dataObj instanceof java.util.Map) {
                                java.util.Map<String, Object> dataMap = (java.util.Map<String, Object>) dataObj;
                                Object userObj = dataMap.get("user");
                                if (userObj instanceof java.util.Map) {
                                    java.util.Map<String, Object> userMap = (java.util.Map<String, Object>) userObj;

                                    // Lưu thông tin người dùng nếu cần
                                    Object userIdObj = userMap.get("maNguoiDung");
                                    if (userIdObj != null) {
                                        int userId = 0;
                                        if (userIdObj instanceof Integer) {
                                            userId = (Integer) userIdObj;
                                        } else if (userIdObj instanceof Double) {
                                            userId = ((Double) userIdObj).intValue();
                                        } else {
                                            userId = Integer.parseInt(userIdObj.toString());
                                        }
                                        editor.putInt(Constants.KEY_USER_ID, userId);
                                    }

                                    Object userRoleObj = userMap.get("role");
                                    if (userRoleObj != null && userRoleObj instanceof java.util.Map) {
                                        java.util.Map<String, Object> roleMap = (java.util.Map<String, Object>) userRoleObj;
                                        Object roleName = roleMap.get("tenVaiTro");
                                        if (roleName != null) {
                                            editor.putString(Constants.KEY_USER_ROLE, roleName.toString());
                                        }
                                    } else if (userRoleObj != null) {
                                        // Trường hợp fallback nếu userRoleObj là chuỗi trực tiếp
                                        editor.putString(Constants.KEY_USER_ROLE, userRoleObj.toString());
                                    }

                                    // Thử lấy email từ trường lienHe hoặc email trong phản hồi
                                    Object emailObj = userMap.get("lienHe"); // Assuming lienHe contains email
                                    if (emailObj != null) {
                                        editor.putString(Constants.KEY_EMAIL, emailObj.toString());
                                    } else {
                                        // Nếu không có, sử dụng username như email
                                        editor.putString(Constants.KEY_EMAIL, username);
                                    }
                                }
                            }

                            editor.apply();

                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                            // Kiểm tra nếu người dùng đến từ JobDetailActivity
                            boolean fromJobDetail = getIntent().getBooleanExtra("from_job_detail", false);

                            if (fromJobDetail) {
                                // Quay lại màn hình trước đó (JobDetailActivity sẽ tự cập nhật)
                                finish();
                            } else {
                                // Chuyển đến MainActivity
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Lấy token thất bại", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Đăng nhập thất bại";
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}