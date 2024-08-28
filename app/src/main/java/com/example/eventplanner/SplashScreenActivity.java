package com.example.eventplanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplanner.config.AppConfig;

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
                SharedPreferences preferences = getSharedPreferences(AppConfig.SHARED_PREF_NAME, MODE_PRIVATE);
                boolean isFirstTime = preferences.getBoolean("isFirstTime", true);
                String user = preferences.getString("user", null);
                Intent i;

                if (user != null && !user.isEmpty()) {
//                    Fetch information




//                    Go to Home
                    i = new Intent(SplashScreenActivity.this, MainActivity.class);
                } else if (isFirstTime) {
                    i = new Intent(SplashScreenActivity.this, WelcomeActivity.class);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isFirstTime", false);
                    editor.apply();
                } else if (user == null || user.isEmpty()) {
                    i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                } else {
                    i = new Intent(SplashScreenActivity.this, WelcomeActivity.class);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isFirstTime", false);
                    editor.apply();
                }
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}