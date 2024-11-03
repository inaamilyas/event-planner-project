package com.example.eventplanner.Admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.eventplanner.Admin.adapter.AdminVenueAdapter;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponseArray;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.databinding.ActivityAdminDashboardBinding;
import com.example.eventplanner.models.Venue;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends AppCompatActivity {
    private ActivityAdminDashboardBinding binding;
    private ArrayList<Venue> venuesList = new ArrayList<>();
    private ArrayList<Venue> venueRequestList = new ArrayList<>();
    private ArrayList<Venue> allVenueList = new ArrayList<>();
    private AdminVenueAdapter venueAdapter;
    private AdminVenueAdapter allVenuesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set a custom title for the toolbar
        setTitle("");
        setSupportActionBar(binding.toolBar);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Fetch data from API
        fetchVenues(apiService);

        // Initialize RecyclerView and Adapter
        binding.venuesRecyclerHome.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        venueAdapter = new AdminVenueAdapter(venueRequestList);
        binding.venuesRecyclerHome.setAdapter(venueAdapter);

        // Setup RecyclerView for "All Venues"
        binding.otherVenuesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        allVenuesAdapter = new AdminVenueAdapter(allVenueList); // Assuming you have this list
        binding.otherVenuesRecycler.setAdapter(allVenuesAdapter);

        // Handle menu clicks
        binding.venueRequestTab.setOnClickListener(v -> showVenueRequest());
        binding.allVenuesTab.setOnClickListener(v -> showAllVenues());

        // Default to showing "Venue Request"
        showVenueRequest();

        binding.adminLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, AdminLoginActivity.class));
                Toast.makeText(AdminDashboardActivity.this, "Logging out", Toast.LENGTH_SHORT).show();
                finish();
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

    private void showVenueRequest() {
        binding.allRequestsContainer.setVisibility(View.VISIBLE);
        binding.allVenuesContainer.setVisibility(View.GONE);
        binding.venueRequestTab.setBackgroundColor(Color.parseColor("#eaeaea"));
        binding.allVenuesTab.setBackgroundColor(Color.parseColor("#ffffff"));
    }

    private void showAllVenues() {
        binding.allRequestsContainer.setVisibility(View.GONE);
        binding.allVenuesContainer.setVisibility(View.VISIBLE);
        binding.venueRequestTab.setBackgroundColor(Color.parseColor("#ffffff"));
        binding.allVenuesTab.setBackgroundColor(Color.parseColor("#eaeaea"));
    }


    private void fetchVenues(ApiService apiService) {

        // Call the API to fetch venues for the manager
        Call<ApiResponseArray<Venue>> call = apiService.getDataForAdmin();
        call.enqueue(new Callback<ApiResponseArray<Venue>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ApiResponseArray<Venue>> call, Response<ApiResponseArray<Venue>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        // Get the list of Venue from the ApiResponseArray
                        List<Venue> venueList = response.body().getData();

                        if (venueList != null && !venueList.isEmpty()) {

                            // Update the venuesList and notify the adapter
                            venuesList.clear(); // Clear the current venuesList
                            venuesList.addAll(venueList); // Add all fetched venues to the venuesList

                            // Clear the request and all lists to avoid duplicates
                            venueRequestList.clear();
                            allVenueList.clear();

                            // Loop through the venueList (or you can use venuesList)
                            for (int i = 0; i < venueList.size(); i++) {
                                // Check the status of the venue and categorize
                                if (venueList.get(i).getStatus() != 1) {
                                    venueRequestList.add(venueList.get(i));
                                } else {
                                    allVenueList.add(venueList.get(i));
                                }
                            }

                            if (allVenueList.isEmpty()) {
                                binding.noVenueRequest.setVisibility(View.VISIBLE);
                                binding.noVenueRequest.setText("No venues found");
                            }

                            if (venueRequestList.isEmpty()) {
                                binding.noVenueRequest.setVisibility(View.VISIBLE);
                                binding.noVenueRequest.setText("No venues Requests found");
                            }

                            // Notify the adapter about the data change
                            venueAdapter.notifyDataSetChanged();
                            allVenuesAdapter.notifyDataSetChanged();
                        } else {
//                            binding.noVenueRequest.setVisibility(View.VISIBLE);
                            Toast.makeText(AdminDashboardActivity.this, "No venues found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AdminDashboardActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminDashboardActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseArray<Venue>> call, Throwable t) {
                Toast.makeText(AdminDashboardActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
        // After the refresh is complete, hide the loading indicator
        binding.swipeRefreshLayout.setRefreshing(false);
    }

}