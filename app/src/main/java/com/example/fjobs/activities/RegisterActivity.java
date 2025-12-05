package com.example.fjobs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fjobs.R;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.models.User;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText etUsername, etDisplayName, etContact, etPassword;
    private RadioGroup rgUserRole;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etDisplayName = findViewById(R.id.et_display_name);
        etContact = findViewById(R.id.et_contact);
        etPassword = findViewById(R.id.et_password);
        rgUserRole = findViewById(R.id.rg_user_role);
        btnRegister = findViewById(R.id.btn_register);
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String displayName = etDisplayName.getText().toString().trim();
            String contact = etContact.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || displayName.isEmpty() || contact.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Xác định vai trò người dùng
            int selectedRoleId = 2; // Mặc định là NV (người tìm việc)
            int selectedRadioButtonId = rgUserRole.getCheckedRadioButtonId();
            if (selectedRadioButtonId == R.id.rb_employer) {
                selectedRoleId = 3; // NTD (nhà tuyển dụng)
            }

            performRegister(username, displayName, contact, password, selectedRoleId);
        });
    }

    private void performRegister(String username, String displayName, String contact, String password, int roleId) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        // Tạo user object để gửi đăng ký
        // Chú ý: API Spring Boot có thể yêu cầu DTO riêng biệt cho đăng ký
        // ở đây tôi giả định API có thể sử dụng model User trực tiếp
        User user = new User(username, displayName, contact, roleId);

        Call<ApiResponse> call = apiService.register(user);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        // Chuyển về màn hình đăng nhập, giữ lại extra nếu có
                        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                        loginIntent.putExtras(getIntent()); // Sao chép các extra từ RegisterActivity
                        startActivity(loginIntent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}