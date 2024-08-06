package com.example.eventplanner.VenueManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventplanner.ForgetPasswordActivity;
import com.example.eventplanner.LoginActivity;
import com.example.eventplanner.MainActivity;
import com.example.eventplanner.R;
import com.example.eventplanner.SignupActivity;
import com.example.eventplanner.databinding.ActivityLoginBinding;
import com.example.eventplanner.databinding.ActivityVenueLoginBinding;

public class VenueLoginActivity extends AppCompatActivity {

    private ActivityVenueLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVenueLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        setSupportActionBar(binding.myToolbar);

        binding.btnVenueLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VenueLoginActivity.this, "Sign in...", Toast.LENGTH_SHORT).show();
                String email = binding.etLoginEmail.getText().toString();
                String password = binding.etLoginPassword.getText().toString();

//                hit api for login. on success login to home screen
                startActivity(new Intent(VenueLoginActivity.this, DashboardVenueManagerActivity.class));
                finish();
            }
        });

        binding.tvGoToVenueSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VenueLoginActivity.this, VenueSignupActivity.class));
                finish();
            }
        });
    }
}