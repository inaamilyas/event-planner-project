package com.example.eventplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplanner.databinding.ActivityEventDetailsBinding;

public class EventDetailsActivity extends AppCompatActivity {
    private ActivityEventDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        String eventName = intent.getStringExtra("eventId");
        Toast.makeText(this, "event name " + eventName, Toast.LENGTH_SHORT).show();

        binding.arrowBackEventDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EventDetailsActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}