package com.example.eventplanner.VenueManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.VenueManager.adapter.MangerVenueAdapter;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponseArray;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.databinding.ActivityDashboardVenueManagerBinding;
import com.example.eventplanner.models.User;
import com.example.eventplanner.models.Venue;
import com.example.eventplanner.models.VenueManager;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardVenueManagerActivity extends AppCompatActivity {

    private ActivityDashboardVenueManagerBinding binding;
    private ArrayList<Venue> venuesList = new ArrayList<>();
    private MangerVenueAdapter venuesAdapter;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardVenueManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set a custom title for the toolbar
        setTitle("");
        setSupportActionBar(binding.toolBar);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_name);
        TextView emailTextView = headerView.findViewById(R.id.email);
        ImageView profileImageView = headerView.findViewById(R.id.profile_image);

        User venueManager = VenueManager.getFromPreferences(this);

        if (venueManager != null) {
            userNameTextView.setText(venueManager.getName());
            emailTextView.setText(venueManager.getEmail());
            Glide.with(this).load(venueManager.getProfilePic()).placeholder(R.drawable.event_image_1).into(profileImageView);
        }


        // Initialize the DrawerLayout and toggle
        toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolBar, R.string.drawer_open, R.string.drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Handle navigation item clicks in the NavigationView
        binding.navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Navigate to the home activity
                startActivity(new Intent(this, DashboardVenueManagerActivity.class)); // Example
            } else if (itemId == R.id.nav_add_venue) {
                startActivity(new Intent(this, AddVenueActivity.class));
            } else if (itemId == R.id.nav_profile) {

            } else if (itemId == R.id.manager_logout) {
                startActivity(new Intent(this, VenueLoginActivity.class));
                VenueManager.clearPreferences(this);
                finish();
            } else {
                // Handle other items
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

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
//                startActivity(new Intent(DashboardVenueManagerActivity.this, AddMenuActivity.class));
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
        int managerId = VenueManager.getFromPreferences(DashboardVenueManagerActivity.this).getId();

        Call<ApiResponseArray<Venue>> call = apiService.getVenues(managerId);
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
                        binding.noEvents.setVisibility(View.VISIBLE);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
