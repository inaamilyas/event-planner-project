package com.example.eventplanner;

import static com.example.eventplanner.fragments.HomeFragment.eventList;
import static com.example.eventplanner.fragments.HomeFragment.homeEventsAdapter;
import static com.example.eventplanner.fragments.HomeFragment.user;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.eventplanner.adapters.ManuVenueDetailsAdapter;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.config.AppConfig;
import com.example.eventplanner.databinding.ActivityEventDetailsBinding;
import com.example.eventplanner.models.Event;
import com.example.eventplanner.models.MenuItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsActivity extends AppCompatActivity {
    private ActivityEventDetailsBinding binding;
    Event event;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        String position = String.valueOf(intent.getIntExtra("position", -1));
        event = eventList.get(Integer.parseInt(position));

        binding.eventEditButton.setOnClickListener(v -> {
            Intent editEventIntent = new Intent(EventDetailsActivity.this, EditEventActivity.class);
            editEventIntent.putExtra("position", position);
            startActivity(editEventIntent);
        });

        // Set the event name
        binding.eventName.setText(event.getName() != null ? event.getName() : "Event Name Not Available");

// Set the event location if available, otherwise show a default message
        if (event.getVenue() != null && event.getVenue().getAddress() != null) {
            binding.eventLocation.setText(event.getVenue().getAddress());
        } else {
            binding.eventLocation.setText("No Address...");
        }

// Set the event date and time
        String eventTime = (event.getDate() != null ? event.getDate() : "Date Not Available") + " " + (event.getTime() != null ? event.getTime() : "Time Not Available");
        binding.eventTime.setText(eventTime);

// Set the event description
        binding.eventAbout.setText(event.getAbout() != null ? event.getAbout() : "No Description Available");

// Set the organizer name
        binding.organizerName.setText(user != null && user.getName() != null ? user.getName() : "Organizer Name Not Available");

// Load the event image using Glide, with a placeholder in case the image URL is not available
        String imageUrl = event.getImage() != null ? (AppConfig.SERVER_URL + event.getImage()).trim() : null;
        Glide.with(this).load(imageUrl).placeholder(R.drawable.enent_image) // Replace with your placeholder image resource
                .error(R.drawable.enent_image) // Display this image if loading fails
                .into(binding.eventImage);

        // Load the event image using Glide, with a placeholder in case the image URL is not available
        String profileImageUrl = event.getImage() != null ? (AppConfig.SERVER_URL + user.getProfilePic()).trim() : null;
        Glide.with(this).load(imageUrl).placeholder(R.drawable.enent_image) // Replace with your placeholder image resource
                .error(R.drawable.enent_image) // Display this image if loading fails
                .into(binding.eventImage);

        binding.shareEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareEventDetails();
            }
        });

        binding.arrowBackEventDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EventDetailsActivity.this, MainActivity.class));
                finish();
            }
        });

        binding.eventDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEvent(Integer.parseInt(position));
            }
        });


        if( event.getVenue() != null && !event.getVenue().getFoodMenuItems().isEmpty()){
            ManuVenueDetailsAdapter adapter = new ManuVenueDetailsAdapter((ArrayList<MenuItem>) event.getVenue().getFoodMenuItems());
            binding.menuRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            binding.menuRecyclerView.setAdapter(adapter);
        } else{
            binding.menuRecyclerView.setVisibility(View.GONE);
            binding.noMenuItem.setVisibility(View.GONE);
        }

    }

    private void shareEventDetails() {
        String eventDetails = "Event Name: " + (event.getName() != null ? event.getName() : "N/A") + "\nLocation: " + (event.getVenue() != null && event.getVenue().getAddress() != null ? event.getVenue().getAddress() : "No Address...") + "\nTime: " + (event.getDate() != null ? event.getDate() : "N/A") + " " + (event.getTime() != null ? event.getTime() : "N/A") + "\nAbout: " + (event.getAbout() != null ? event.getAbout() : "No Description Available") + "\nOrganizer: " + (user != null && user.getName() != null ? user.getName() : "Organizer Name Not Available");

        String imageUrl = event.getImage() != null ? (AppConfig.SERVER_URL + event.getImage()).trim() : null;

        if (imageUrl != null) {
            Glide.with(this).asBitmap().load(imageUrl).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    try {
                        // Save bitmap to cache directory
                        Uri imageUri = saveImageToCache(resource);

                        if (imageUri != null) {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("image/*");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, eventDetails);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(Intent.createChooser(shareIntent, "Share Event via"));
                        } else {
                            showToast("Failed to share the image.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast("Error sharing the image.");
                    }
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                    // Handle when the image load is cleared
                }
            });
        } else {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, eventDetails);
            startActivity(Intent.createChooser(shareIntent, "Share Event via"));
        }
    }

    // Method to save bitmap to cache and get URI
    private Uri saveImageToCache(Bitmap bitmap) {
        File cachePath = new File(getCacheDir(), "images");
        cachePath.mkdirs();
        File imageFile = new File(cachePath, "shared_image.png");

        try (FileOutputStream stream = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return FileProvider.getUriForFile(this, getPackageName() + ".provider", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper method to show toast messages
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void deleteEvent(int position) {
        // Get the event and its ID based on the clicked position
        Event event = eventList.get(position);
        int eventId = event.getId();

        // Show a confirmation dialog to the user
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    // User confirmed deletion, proceed with API call
                    ApiService apiService = ApiClient.getClient().create(ApiService.class);

                    apiService.deleteEvent(String.valueOf(eventId)).enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            Log.d("inaamilyas", "onResponse: " + response.code());
                            if (response.isSuccessful() && response.body() != null) {
                                // Remove the event from the list and notify the adapter
                                eventList.remove(position);
                                homeEventsAdapter.notifyItemRemoved(position);
                                Toast.makeText(EventDetailsActivity.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                finish();
                            } else {
                                Toast.makeText(EventDetailsActivity.this, "Failed to delete event", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {
                            Toast.makeText(EventDetailsActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // User canceled the deletion
                    dialog.dismiss();
                })
                .show();
    }


    // Method to get image URI from bitmap
    private Uri getImageUri(Bitmap bitmap) {
        // Save bitmap to cache directory
        File cachePath = new File(getCacheDir(), "images");
        cachePath.mkdirs(); // Create directory if not exists
        File file = new File(cachePath, "shared_image.png");

        try (FileOutputStream stream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // Compress and write to file
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get the URI of the saved file
        return FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
    }
}