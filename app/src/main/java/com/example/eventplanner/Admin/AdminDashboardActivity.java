package com.example.eventplanner.Admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private AdminVenueAdapter venueAdapter;

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
        venueAdapter = new AdminVenueAdapter(venuesList);
        binding.venuesRecyclerHome.setAdapter(venueAdapter);


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

    private void fetchVenues(ApiService apiService) {

        // Call the API to fetch venues for the manager
        Call<ApiResponseArray<Venue>> call = apiService.getDataForAdmin();
        call.enqueue(new Callback<ApiResponseArray<Venue>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ApiResponseArray<Venue>> call, Response<ApiResponseArray<Venue>> response) {
                if (response.isSuccessful()) {
                    Log.d("inaamilysa", "onResponse: Response is successful");

                    if (response.body() != null) {
                        Log.d("inaamilysa", "onResponse: Response body is not null");

                        // Get the list of Venue from the ApiResponseArray
                        List<Venue> venueList = response.body().getData();
                        Log.d("inaamilysa", "onResponse: Venue list fetched: " + venueList);

                        if (venueList != null && !venueList.isEmpty()) {
                            Log.d("inaamilysa", "onResponse: Venue list size = " + venueList.size());

                            // Update the list and notify adapter
                            venuesList.clear(); // Clear the current list
                            venuesList.addAll(venueList); // Add all fetched venues to the list
                            Log.d("inaamilysa", "onResponse: Venues list updated in adapter, size = " + venuesList.size());
                            venueAdapter.notifyDataSetChanged(); // Notify adapter about data change

                        } else {
                            Log.d("inaamilysa", "onResponse: Venue list is empty or null");
                            binding.noEvents.setVisibility(View.VISIBLE);
                            Toast.makeText(AdminDashboardActivity.this, "No venues found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("inaamilysa", "onResponse: Response body is null");
                        Toast.makeText(AdminDashboardActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("inaamilysa", "onResponse: Response is unsuccessful. Error code = " + response.code());
                    Toast.makeText(AdminDashboardActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseArray<Venue>> call, Throwable t) {
                Log.d("inaamilysa", "onFailure: API call failed. Error message = " + t.getMessage());
                Toast.makeText(AdminDashboardActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
        // After the refresh is complete, hide the loading indicator
        binding.swipeRefreshLayout.setRefreshing(false);
    }

}