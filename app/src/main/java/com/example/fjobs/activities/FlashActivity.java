package com.example.fjobs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fjobs.R;

public class FlashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);

        // Hide action bar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Apply animations to splash elements
        animateSplashElements();

        // Use handler to delay navigation to main activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(FlashActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close splash activity
                // Add fade in/out transition
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, SPLASH_DURATION);
    }

    private void animateSplashElements() {
        ImageView logo = findViewById(R.id.iv_logo);
        TextView appName = findViewById(R.id.tv_app_name);
        TextView slogan = findViewById(R.id.tv_slogan);
        TextView description = findViewById(R.id.tv_description);

        // Logo scale animation
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        logo.startAnimation(scaleAnimation);

        // App name fade in animation with delay
        appName.setAlpha(0f);
        appName.animate().alpha(1f).setDuration(1000).setStartDelay(500);

        // Slogan fade in animation with delay
        slogan.setAlpha(0f);
        slogan.animate().alpha(1f).setDuration(1000).setStartDelay(1000);

        // Description fade in animation with delay
        description.setAlpha(0f);
        description.animate().alpha(1f).setDuration(1000).setStartDelay(1500);
    }
}