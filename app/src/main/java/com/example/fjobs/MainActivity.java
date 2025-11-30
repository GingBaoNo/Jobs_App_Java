package com.example.fjobs;

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
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.fragments.CompaniesFragment;
import com.example.fjobs.fragments.HomeFragment;
import com.example.fjobs.fragments.JobsFragment;
import com.example.fjobs.fragments.SavedJobsFragment;
import com.example.fjobs.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        // Check if user is logged in
        if (sharedPreferences.getString(Constants.KEY_TOKEN, null) == null) {
            // User not logged in, redirect to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupNavigation();
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
            } else if (itemId == R.id.nav_saved_jobs_bottom) {
                selectedFragment = new SavedJobsFragment();
            } else if (itemId == R.id.nav_profile_bottom) {
                // Mở UserProfileFragment cho tab hồ sơ
                loadFragment(new UserProfileFragment());
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
        } else if (id == R.id.nav_profile) {
            // Mở UserProfileFragment
            loadFragment(new UserProfileFragment());
        } else if (id == R.id.nav_saved_jobs) {
            // Mở SavedJobsFragment trong MainActivity
            loadFragment(new SavedJobsFragment());
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