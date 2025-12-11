package com.example.fjobs.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.fjobs.R;
import com.example.fjobs.fragments.UserProfileFragment;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.fragments.AppliedJobsFragment;
import com.example.fjobs.fragments.ChatFragment;
import com.example.fjobs.fragments.CompaniesFragment;
import com.example.fjobs.fragments.HomeFragment;
import com.example.fjobs.fragments.JobsFragment;
import com.example.fjobs.fragments.SavedJobsFragment;
import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ApiClient with context first
        ApiClient.initialize(this);

        // Initialize SharedPreferences first
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);

        initViews();
        setupToolbar();

        // Check if user is logged in
        boolean isLoggedIn = sharedPreferences.getString(Constants.KEY_TOKEN, null) != null;

        // Allow access to app whether logged in or not, but update UI accordingly
        if (!isLoggedIn) {
            // User not logged in, update UI to reflect this state
            setupLoggedOutNavigation();
        } else {
            // Update navigation header with user info
            setupNavigation();
        }

        setupBottomNavigation();
        loadFragment(new HomeFragment());
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            // Mở UserProfileFragment từ toolbar
            loadFragment(new UserProfileFragment());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupNavigation() {
        navigationView.setNavigationItemSelectedListener(this);

        // Update navigation header with user info
        View headerView = navigationView.getHeaderView(0);
        TextView txtName = headerView.findViewById(R.id.txt_name);
        TextView txtEmail = headerView.findViewById(R.id.txt_email);
        txtName.setText(sharedPreferences.getString(Constants.KEY_USERNAME, "Người dùng"));
        txtEmail.setText(sharedPreferences.getString(Constants.KEY_EMAIL, "gingbao@gmail.com"));

        // Load avatar hiện tại của người dùng
        loadNavHeaderAvatar();
    }

    private void setupLoggedOutNavigation() {
        navigationView.setNavigationItemSelectedListener(this);

        // Update navigation header with login prompt
        View headerView = navigationView.getHeaderView(0);
        TextView txtName = headerView.findViewById(R.id.txt_name);
        TextView txtEmail = headerView.findViewById(R.id.txt_email);
        txtName.setText("Vui lòng đăng nhập");
        txtEmail.setText("Nhấn vào đây để đăng nhập");

        // Set default avatar with proper handling for CircleImageView
        de.hdodenhof.circleimageview.CircleImageView imgAvt = headerView.findViewById(R.id.img_avt);
        if (imgAvt != null) {
            // Sử dụng Glide để đảm bảo ảnh được tải an toàn tránh lỗi width/height = 0
            imgAvt.post(() -> {
                com.bumptech.glide.Glide.with(this)
                        .load(R.drawable.avt)  // Ảnh mặc định
                        .placeholder(R.drawable.avt)  // Ảnh placeholder
                        .error(R.drawable.avt)  // Ảnh khi lỗi
                        .into(imgAvt);
            });

            // Set click listener to login
            imgAvt.setOnClickListener(v -> {
                startActivity(new Intent(this, LoginActivity.class));
            });
        }

        txtName.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
    }

    private void loadNavHeaderAvatar() {
        // Khởi tạo ApiService và tải thông tin hồ sơ để lấy avatar
        retrofit2.Retrofit retrofit = ApiClient.getRetrofitInstance();
        if (retrofit != null) {
            ApiService apiService = retrofit.create(ApiService.class);
            Call<ApiResponse> call = apiService.getMyProfile();
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, retrofit2.Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse apiResponse = response.body();
                        if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                            if (apiResponse.getData() instanceof java.util.Map) {
                                java.util.Map<String, Object> profileMap = (java.util.Map<String, Object>) apiResponse.getData();

                                Object avatarObj = profileMap.get("urlAnhDaiDien");
                                String avatarUrl = null;
                                if (avatarObj != null) {
                                    avatarUrl = avatarObj.toString();
                                }

                                updateNavHeaderAvatar(avatarUrl);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    // Trường hợp lỗi, vẫn hiển thị ảnh mặc định
                    View headerView = navigationView.getHeaderView(0);
                    de.hdodenhof.circleimageview.CircleImageView imgAvt = headerView.findViewById(R.id.img_avt);
                    if (imgAvt != null) {
                        imgAvt.post(() -> {
                            imgAvt.setImageResource(R.drawable.avt);
                        });
                    }
                }
            });
        }
    }

    // Phương thức cập nhật avatar trong header navigation
    public void updateNavHeaderAvatar(String avatarUrl) {
        View headerView = navigationView.getHeaderView(0);
        de.hdodenhof.circleimageview.CircleImageView imgAvt = headerView.findViewById(R.id.img_avt);

        if (imgAvt != null) {
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                String imageUrl;
                if (avatarUrl.startsWith("/")) {
                    imageUrl = "http://192.168.102.19:8080" + avatarUrl;
                } else {
                    imageUrl = "http://192.168.102.19:8080/" + avatarUrl;
                }

                try {
                    // Đảm bảo rằng imgAvt đã có kích thước hợp lệ trước khi load ảnh
                    imgAvt.post(() -> {
                        if (imgAvt.getWidth() > 0 && imgAvt.getHeight() > 0) {
                            com.bumptech.glide.Glide.with(this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.avt)  // Ảnh mặc định
                                    .error(R.drawable.avt)        // Ảnh khi lỗi
                                    .into(imgAvt);
                        } else {
                            // Nếu chưa có kích thước, đợi thêm một chút
                            imgAvt.post(() -> {
                                com.bumptech.glide.Glide.with(this)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.avt)  // Ảnh mặc định
                                        .error(R.drawable.avt)        // Ảnh khi lỗi
                                        .into(imgAvt);
                            });
                        }
                    });
                } catch (Exception e) {
                    System.out.println("Lỗi load avatar trong header navigation: " + e.getMessage());
                    // Sử dụng Glide để đảm bảo ảnh được tải an toàn tránh lỗi width/height = 0
                    imgAvt.post(() -> {
                        com.bumptech.glide.Glide.with(this)
                                .load(R.drawable.avt)  // Ảnh mặc định
                                .placeholder(R.drawable.avt)  // Ảnh placeholder
                                .error(R.drawable.avt)  // Ảnh khi lỗi
                                .into(imgAvt);
                    });
                }
            } else {
                // Nếu không có avatar, sử dụng ảnh mặc định
                imgAvt.post(() -> {
                    com.bumptech.glide.Glide.with(this)
                            .load(R.drawable.avt)  // Ảnh mặc định
                            .placeholder(R.drawable.avt)  // Ảnh placeholder
                            .error(R.drawable.avt)  // Ảnh khi lỗi
                            .into(imgAvt);
                });
            }
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home_bottom) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_jobs_bottom) {
                selectedFragment = new JobsFragment();
            } else if (itemId == R.id.nav_companies_bottom) {
                selectedFragment = new CompaniesFragment();
            } else if (itemId == R.id.nav_chat_bottom) {
                // Kiểm tra đăng nhập trước khi mở danh sách trò chuyện
                if (isUserLoggedIn()) {
                    selectedFragment = new ChatFragment();
                } else {
                    redirectToLogin();
                    return true;
                }
            } else if (itemId == R.id.nav_profile_bottom) {
                // Mở UserProfileFragment cho tab hồ sơ
                if (isUserLoggedIn()) {
                    loadFragment(new UserProfileFragment());
                } else {
                    redirectToLogin();
                }
                return true;
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        Fragment selectedFragment = null;

        if (id == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (id == R.id.nav_jobs) {
            selectedFragment = new JobsFragment();
        } else if (id == R.id.nav_companies) {
            selectedFragment = new CompaniesFragment();
        } else if (id == R.id.nav_chat) {
            // Kiểm tra đăng nhập trước khi mở danh sách trò chuyện
            if (isUserLoggedIn()) {
                loadFragment(new ChatFragment());
            } else {
                redirectToLogin();
            }
        } else if (id == R.id.nav_profile) {
            // Kiểm tra đăng nhập trước khi mở hồ sơ
            if (isUserLoggedIn()) {
                loadFragment(new UserProfileFragment());
            } else {
                redirectToLogin();
            }
        } else if (id == R.id.nav_applied_jobs) {
            // Kiểm tra đăng nhập trước khi mở danh sách công việc đã ứng tuyển
            if (isUserLoggedIn()) {
                loadFragment(new AppliedJobsFragment());
            } else {
                redirectToLogin();
            }
        } else if (id == R.id.nav_saved_jobs) {
            // Kiểm tra đăng nhập trước khi mở danh sách công việc đã lưu
            if (isUserLoggedIn()) {
                loadFragment(new SavedJobsFragment());
            } else {
                redirectToLogin();
            }
        } else if (id == R.id.nav_logout) {
            logout();
        }

        if (selectedFragment != null) {
            loadFragment(selectedFragment);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    private boolean isUserLoggedIn() {
        return sharedPreferences.getString(Constants.KEY_TOKEN, null) != null;
    }

    private void redirectToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        Toast.makeText(this, "Vui lòng đăng nhập để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        // Clear user data from shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Navigate to login activity
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}