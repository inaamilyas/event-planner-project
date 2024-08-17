package com.example.eventplanner.VenueManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.eventplanner.VenueManager.adapter.MangerVenueAdapter;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponseArray;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.databinding.ActivityDashboardVenueManagerBinding;
import com.example.eventplanner.models.Venue;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardVenueManagerActivity extends AppCompatActivity {

    private ActivityDashboardVenueManagerBinding binding;
    private ArrayList<Venue> venuesList = new ArrayList<>();
    private MangerVenueAdapter venuesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardVenueManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Initialize RecyclerView and Adapter
        binding.venuesRecyclerHome.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        venuesAdapter = new MangerVenueAdapter(venuesList);
        binding.venuesRecyclerHome.setAdapter(venuesAdapter);

        // Fetch data from API
        fetchVenues(apiService);

//        // Floating Action Button click listener to add a new venue
        binding.addVenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardVenueManagerActivity.this, AddVenueActivity.class));
            }
        });

        // Set up the refresh listener
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Call a method to refresh the content
                fetchVenues(apiService);
            }
        });
    }


    private void fetchVenues(ApiService apiService) {
        Call<ApiResponseArray<Venue>> call = apiService.getVenues();
        call.enqueue(new Callback<ApiResponseArray<Venue>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ApiResponseArray<Venue>> call, Response<ApiResponseArray<Venue>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // Get the list of Venue from the ApiResponseArray
                    List<Venue> venueList = response.body().getData();

                    if (venueList != null && !venueList.isEmpty()) {

                        // Update the list and notify adapter
                        venuesList.clear(); // Clear the current list
                        venuesList.addAll(venueList); // Add all fetched venues to the list
                        venuesAdapter.notifyDataSetChanged(); // Notify adapter about data change
                    } else {
                        Toast.makeText(DashboardVenueManagerActivity.this, "No venues found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DashboardVenueManagerActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseArray<Venue>> call, Throwable t) {
                Toast.makeText(DashboardVenueManagerActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
        // After the refresh is complete, hide the loading indicator
        binding.swipeRefreshLayout.setRefreshing(false);
    }


}
