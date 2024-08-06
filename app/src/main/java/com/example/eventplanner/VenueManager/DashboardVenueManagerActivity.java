package com.example.eventplanner.VenueManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventplanner.adapters.VenuesAdapter;
import com.example.eventplanner.databinding.ActivityDashboardVenueManagerBinding;
import com.example.eventplanner.models.Venue;

public class DashboardVenueManagerActivity extends AppCompatActivity {

    private ActivityDashboardVenueManagerBinding binding;

    private Venue[] venuesArr = {
            new Venue("Event 1", "Address 1", "https://media.istockphoto.com/id/974238866/photo/audience-listens-to-the-lecturer-at-the-conference.jpg?s=612x612&w=0&k=20&c=p_BQCJWRQQtZYnQlOtZMzTjeB_csic8OofTCAKLwT0M="),
            new Venue("Event 1", "Address 1", "https://media.istockphoto.com/id/974238866/photo/audience-listens-to-the-lecturer-at-the-conference.jpg?s=612x612&w=0&k=20&c=p_BQCJWRQQtZYnQlOtZMzTjeB_csic8OofTCAKLwT0M="),
            new Venue("Event 1", "Address 1", "https://media.istockphoto.com/id/974238866/photo/audience-listens-to-the-lecturer-at-the-conference.jpg?s=612x612&w=0&k=20&c=p_BQCJWRQQtZYnQlOtZMzTjeB_csic8OofTCAKLwT0M="),
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardVenueManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        setSupportActionBar(binding.myToolbar);

        binding.venuesRecyclerHome.setLayoutManager(new LinearLayoutManager(DashboardVenueManagerActivity.this, LinearLayoutManager.VERTICAL, false));
        VenuesAdapter venuesAdapter = new VenuesAdapter(venuesArr);
        binding.venuesRecyclerHome.setAdapter(venuesAdapter);

        binding.fabAddVenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardVenueManagerActivity.this, AddVenueActivity.class));
            }
        });
        binding.fabViewVenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardVenueManagerActivity.this, VenueManagerVenDetailsActivity.class));
            }
        });

    }
}