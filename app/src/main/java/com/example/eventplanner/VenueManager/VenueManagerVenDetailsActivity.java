package com.example.eventplanner.VenueManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.VenueManager.MenuItem.AddMenuActivity;
import com.example.eventplanner.adapters.FeedbackAdapter;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.config.AppConfig;
import com.example.eventplanner.databinding.ActivityVenueManagerVenDetailsBinding;
import com.example.eventplanner.models.Feedback;
import com.example.eventplanner.models.Venue;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
            Glide.with(this).load(imageUrl).placeholder(R.drawable.event_image_1).into(binding.venueDetailsImage);

            if (selectedVenue.getStatus() == 1) {
                binding.approveStatus.setVisibility(View.VISIBLE);
            } else if (selectedVenue.getStatus() == 2) {
                binding.rejectStatus.setVisibility(View.VISIBLE);
            } else {
                binding.pendingStatus.setVisibility(View.VISIBLE);
            }
        }

        binding.viewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VenueManagerVenDetailsActivity.this, AddMenuActivity.class);
                intent.putExtra("selectedVenue", selectedVenue);
                startActivity(intent);
            }
        });


        binding.venueEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VenueManagerVenDetailsActivity.this, EditVenueActivity.class);
                intent.putExtra("selectedVenue", selectedVenue);
                startActivity(intent);
            }
        });

        binding.venueDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle delete button click
                deleteVenue(selectedVenue.getId());
            }
        });

        assert selectedVenue != null;
        ArrayList<Feedback> feedbacksList = new ArrayList<>();
        if (selectedVenue.getVenueFeedbacks() != null) {
            feedbacksList = (ArrayList<Feedback>) selectedVenue.getVenueFeedbacks();
        }
        FeedbackAdapter feedbackAdapter = new FeedbackAdapter(feedbacksList);
        binding.reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.reviewsRecyclerView.setAdapter(feedbackAdapter);

    }

    private void deleteVenue(int venueId) {

        // Show a confirmation dialog to the user
        new AlertDialog.Builder(this).setTitle("Delete Venue").setMessage("Are you sure you want to delete this venue?").setPositiveButton("Confirm", (dialog, which) -> {
            // User confirmed deletion, proceed with API call
            ApiService apiService = ApiClient.getClient().create(ApiService.class);

            apiService.deleteVenue(String.valueOf(venueId)).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Log.d("inaamilyas", "onResponse: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(VenueManagerVenDetailsActivity.this, "Venue deleted successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        startActivity(new Intent(VenueManagerVenDetailsActivity.this, DashboardVenueManagerActivity.class));
                        finish();
                    } else {
                        Toast.makeText(VenueManagerVenDetailsActivity.this, "Failed to delete venue", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(VenueManagerVenDetailsActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                }
            });
        }).setNegativeButton("Cancel", (dialog, which) -> {
            // User canceled the deletion
            dialog.dismiss();
        }).show();
    }


}