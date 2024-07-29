package com.example.eventplanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventplanner.adapters.EventsAdapter;
import com.example.eventplanner.databinding.ActivityMainBinding;
import com.example.eventplanner.models.Event;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityMainBinding binding;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView profileImageView;
    private TextView usernameTextView;
    private ImageView buttonCloseDrawer;

    private Event[] eventsArr = {
            new Event("Event 1", "Date 1", "Address 1", "https://media.istockphoto.com/id/974238866/photo/audience-listens-to-the-lecturer-at-the-conference.jpg?s=612x612&w=0&k=20&c=p_BQCJWRQQtZYnQlOtZMzTjeB_csic8OofTCAKLwT0M="),
            // Add more events if needed
    };

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Initialize RecyclerViews
        binding.eventsRecyclerHome.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        EventsAdapter eventsAdapter = new EventsAdapter(eventsArr);
        binding.eventsRecyclerHome.setAdapter(eventsAdapter);

        binding.venuesRecyclerHome.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.venuesRecyclerHome.setAdapter(eventsAdapter);

        // Set click listeners
        binding.seeAllEvents.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AllEventsActivity.class)));
        binding.seeAllVenues.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AllVenuesActivity.class)));

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
//        buttonCloseDrawer = findViewById(R.id.close_menu);


        // Initialize header view
        View headerView = navigationView.getHeaderView(0);
        profileImageView = headerView.findViewById(R.id.imageView);
        usernameTextView = headerView.findViewById(R.id.username);
        buttonCloseDrawer = headerView.findViewById(R.id.close_menu);


        // Set user information
        profileImageView.setImageResource(R.drawable.event_image_1);
        usernameTextView.setText("Inam Ilyas");

        // Set button click listener to open/close drawer
        binding.buttonOpenDrawer.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Set click listener for the close drawer button
        buttonCloseDrawer.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));

        // Set Navigation Item Selected Listener
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.nav_home){
            Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
        } else  if(id == R.id.nav_settings){
            Toast.makeText(this, "Setting", Toast.LENGTH_SHORT).show();
        } else  if(id == R.id.nav_logout){
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
