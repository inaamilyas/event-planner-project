package com.example.eventplanner.VenueManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.config.AppConfig;
import com.example.eventplanner.databinding.ActivityVenueManagerVenDetailsBinding;
import com.example.eventplanner.models.Venue;

public class VenueManagerVenDetailsActivity extends AppCompatActivity {

    private ActivityVenueManagerVenDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVenueManagerVenDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        setSupportActionBar(binding.myToolbar);

        // Retrieve the Venue object
        Venue selectedVenue = (Venue) getIntent().getSerializableExtra("selectedVenue");

        // Use the Venue object
        if (selectedVenue != null) {
            binding.venueName.setText(selectedVenue.getName());
            binding.venueAddress.setText(selectedVenue.getAddress());
            binding.venueAbout.setText(selectedVenue.getAbout());

//            Setting image
            String imageUrl = AppConfig.SERVER_URL + selectedVenue.getPicture();
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.event_image_1)
                    .into(binding.venueDetailsImage);
        }


        binding.venueEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VenueManagerVenDetailsActivity.this, EditVenueActivity.class));
            }
        });
    }
}