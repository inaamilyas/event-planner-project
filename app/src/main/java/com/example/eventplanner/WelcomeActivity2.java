package com.example.eventplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventplanner.databinding.ActivityWelcome2Binding;
import com.example.eventplanner.databinding.ActivityWelcomeBinding;

public class WelcomeActivity2 extends AppCompatActivity {

    private ActivityWelcome2Binding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcome2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.tvSkip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity2.this, LoginActivity.class));
                finish();
            }
        });

        binding.tvNext2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity2.this, WelcomeActivity3.class));
                finish();
            }
        });
    }
}