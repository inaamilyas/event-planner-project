package com.example.eventplanner.VenueManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplanner.databinding.ActivityVenueManagerVenDetailsBinding;

public class VenueManagerVenDetailsActivity extends AppCompatActivity {

    private ActivityVenueManagerVenDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVenueManagerVenDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        setSupportActionBar(binding.myToolbar);


        binding.venueEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VenueManagerVenDetailsActivity.this, EditVenueActivity.class));
            }
        });
    }
}