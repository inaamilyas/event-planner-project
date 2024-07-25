package com.example.eventplanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    // Splash screen timer
    private static final int SPLASH_TIME_OUT = 3000; // 5 seconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // Check if the user has launched the app before
                SharedPreferences preferences = getSharedPreferences("EventPlannerPrefs", MODE_PRIVATE);
                boolean isFirstTime = preferences.getBoolean("isFirstTime", true);

                Intent i;
                if (isFirstTime) {
                    i = new Intent(SplashScreenActivity.this, WelcomeActivity.class);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isFirstTime", false);
                    editor.apply();
                } else {
                    i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                }
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}