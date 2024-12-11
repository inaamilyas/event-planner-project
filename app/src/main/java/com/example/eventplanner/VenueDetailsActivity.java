package com.example.eventplanner;

import static com.example.eventplanner.fragments.HomeFragment.eventList;
import static com.example.eventplanner.fragments.HomeFragment.user;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.eventplanner.VenueManager.adapter.GalleryAdapter;
import com.example.eventplanner.adapters.FeedbackAdapter;
import com.example.eventplanner.adapters.ManuVenueDetailsAdapter;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.config.AppConfig;
import com.example.eventplanner.databinding.ActivityVenueDetailsBinding;
import com.example.eventplanner.fragments.BookVenueFragment;
import com.example.eventplanner.models.Feedback;
import com.example.eventplanner.models.MenuItem;
import com.example.eventplanner.models.Venue;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VenueDetailsActivity extends FragmentActivity implements OnMapReadyCallback {

    private ActivityVenueDetailsBinding binding;
    private GoogleMap mMap;
    private Venue selectedVenue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVenueDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        setSupportActionBar(binding.myToolbar);

        selectedVenue = (Venue) getIntent().getSerializableExtra("selectedVenue");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.map_container, mapFragment).commit();
        mapFragment.getMapAsync(this);

        binding.venueName.setText(selectedVenue.getName());
        binding.venueAbout.setText(selectedVenue.getAbout());
        binding.venueAddress.setText(selectedVenue.getAddress());
        binding.venueContact.setText(selectedVenue.getPhone());
        binding.venueOwnName.setText(selectedVenue.getOwner().getName());
        binding.venueOwnerPhone.setText(selectedVenue.getOwner().getPhone());
        String imageUrl = AppConfig.SERVER_URL + selectedVenue.getPicture();
        Glide.with(this).load(imageUrl).placeholder(R.drawable.event_image_1).into(binding.venueImage);

        // Set up the RecyclerView with horizontal LinearLayoutManager
        binding.galleryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        GalleryAdapter galleryAdapter = new GalleryAdapter(selectedVenue.getGallery());
        binding.galleryRecyclerView.setAdapter(galleryAdapter);

        binding.gotoBookVenueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookVenueFragment bookVenueFragment = BookVenueFragment.newInstance(selectedVenue, String.valueOf(eventList.get(0).getId()));
                FragmentManager fragmentManager = getSupportFragmentManager();
                bookVenueFragment.show(fragmentManager, "BookVenueBottomSheet");
            }
        });

        binding.arrowBackEventDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ManuVenueDetailsAdapter adapter = new ManuVenueDetailsAdapter((ArrayList<MenuItem>) selectedVenue.getFoodMenuItems());
        binding.menuRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.menuRecyclerView.setAdapter(adapter);
        ArrayList<Feedback> feedbacksList = new ArrayList<>();
        if (selectedVenue != null && selectedVenue.getVenueFeedbacks() != null) {
            feedbacksList = (ArrayList<Feedback>) selectedVenue.getVenueFeedbacks();
        }
        FeedbackAdapter feedbackAdapter = new FeedbackAdapter(feedbacksList);
        binding.reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.reviewsRecyclerView.setAdapter(feedbackAdapter);

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                Call<ApiResponse<Venue>> call = apiService.getVenueById(selectedVenue.getId());
                call.enqueue(new Callback<ApiResponse<Venue>>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(Call<ApiResponse<Venue>> call, Response<ApiResponse<Venue>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.code() == 200) {

                                // Get the list of Venue from the ApiResponseArray
                                Venue venue = response.body().getData();
                                if (venue != null) {
                                    selectedVenue = venue;
                                }

                            } else {
                                Toast.makeText(VenueDetailsActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(VenueDetailsActivity.this, "Check Internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Venue>> call, Throwable t) {
                        Toast.makeText(VenueDetailsActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });

                // Stop the refreshing animation
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

        ArrayList<Feedback> finalFeedbacksList = feedbacksList;
        binding.saveFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback = binding.editTextSuggestion.getText().toString();
                int userId = user.getId();
                int venueId = selectedVenue.getId();

                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("user_id", userId);
                requestBody.put("feedback", feedback);
                requestBody.put("venue_id", venueId);
                Call<ApiResponse<String>> call = apiService.saveFeedback(requestBody);

                call.enqueue(new Callback<ApiResponse<String>>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                        binding.saveFeedbackButton.setEnabled(false);
                        binding.saveFeedbackButton.setText("Loading...");

                        if (response.isSuccessful()) {
                            binding.editTextSuggestion.setText("");
                            finalFeedbacksList.add(new Feedback(feedback, user.getProfilePic(), user.getName()));
                            feedbackAdapter.notifyDataSetChanged();
                            binding.saveFeedbackButton.setEnabled(false);
                            binding.saveFeedbackButton.setText("Submitted");
                        } else {
                            try {
                                // Parse the error body to get the API response
                                Gson gson = new Gson();
                                ApiResponse<?> apiErrorResponse = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                                if (apiErrorResponse != null) {
                                    Toast.makeText(VenueDetailsActivity.this, apiErrorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                    binding.saveFeedbackButton.setEnabled(true);
                                    binding.saveFeedbackButton.setText("Submit Again");
                                }
                            } catch (Exception e) {
                                Toast.makeText(VenueDetailsActivity.this, "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
                                binding.saveFeedbackButton.setEnabled(true);
                                binding.saveFeedbackButton.setText("Submit Again");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                        // Handle failure (e.g., no internet connection)
                        Toast.makeText(VenueDetailsActivity.this, "Failed to connect. Please check your internet connection.", Toast.LENGTH_SHORT).show();
                        binding.saveFeedbackButton.setEnabled(true);
                        binding.saveFeedbackButton.setText("Submit Again");
                    }
                });


            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker and move the camera
        LatLng location = new LatLng(selectedVenue.getLatitude(), selectedVenue.getLongitude());
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.addMarker(new MarkerOptions().position(location).title(selectedVenue.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));

    }
}