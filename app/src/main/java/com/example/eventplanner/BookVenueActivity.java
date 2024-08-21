package com.example.eventplanner;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventplanner.databinding.ActivityBookVenueBinding;

public class BookVenueActivity extends AppCompatActivity {
    private ActivityBookVenueBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookVenueBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}