package com.example.eventplanner.VenueManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventplanner.VenueManager.adapter.BookedVenueAdapter;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.databinding.ActivityOrderBinding;
import com.example.eventplanner.models.BookedVenue;
import com.example.eventplanner.models.VenueManager;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {

    private ActivityOrderBinding binding;
    private ArrayList<BookedVenue> bookedVenuesList = new ArrayList<>();
    private BookedVenueAdapter bookedVenueAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Set a custom title for the toolbar
        setTitle("");

        // Initialize the RecyclerView
        binding.bookedVenueRecyclerView.setLayoutManager(new LinearLayoutManager(OrderActivity.this, LinearLayoutManager.VERTICAL, false));
        bookedVenueAdapter = new BookedVenueAdapter(bookedVenuesList);
        binding.bookedVenueRecyclerView.setAdapter(bookedVenueAdapter);

        // Load the initial data
        loadBookedVenues();

        // Handle swipe-to-refresh action
        // Refresh the list
        binding.swipeRefreshLayout.setOnRefreshListener(this::refreshBookedVenues);

    }


    @SuppressLint("NotifyDataSetChanged")
    private void loadBookedVenues() {
        VenueManager venueManager = VenueManager.getFromPreferences(this);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<ArrayList<BookedVenue>>> call = apiService.getManagerBookings(venueManager.getId());

        call.enqueue(new Callback<ApiResponse<ArrayList<BookedVenue>>>() {
            @Override
            public void onResponse(Call<ApiResponse<ArrayList<BookedVenue>>> call, Response<ApiResponse<ArrayList<BookedVenue>>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OrderActivity.this, "Orders retrieved successfully", Toast.LENGTH_SHORT).show();
                    ApiResponse<ArrayList<BookedVenue>> apiResponse = response.body();

                    if (apiResponse != null && apiResponse.getCode() == 200) {
                        // Clear the list and add the new data
                        bookedVenuesList.clear();
                        if (apiResponse.getData() != null) {
                            bookedVenuesList.addAll(apiResponse.getData());
                        }
                        // Notify the adapter of the data change
                        bookedVenueAdapter.notifyDataSetChanged();
                    } else {
                        // Show the error message from the API response
                        assert apiResponse != null;
                        Toast.makeText(OrderActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        // Parse the error body
                        Gson gson = new Gson();
                        ApiResponse<?> apiErrorResponse = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        if (apiErrorResponse != null) {
                            Toast.makeText(OrderActivity.this, apiErrorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(OrderActivity.this, "Something went wrong while getting data", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ArrayList<BookedVenue>>> call, Throwable t) {
                Toast.makeText(OrderActivity.this, "Failed to load booked venues", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @SuppressLint("NotifyDataSetChanged")
    private void refreshBookedVenues() {
        // Simulate a data refresh (you can call an API here if needed)
        bookedVenuesList.clear();
        loadBookedVenues();

        // Notify the adapter and stop the refreshing animation
        bookedVenueAdapter.notifyDataSetChanged();
        binding.swipeRefreshLayout.setRefreshing(false);
    }

}