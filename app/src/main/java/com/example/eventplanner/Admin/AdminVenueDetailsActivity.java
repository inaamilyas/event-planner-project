package com.example.eventplanner.Admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.ManuVenueDetailsAdapter;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.config.AppConfig;
import com.example.eventplanner.databinding.ActivityAdminVenueDetailsBinding;
import com.example.eventplanner.models.MenuItem;
import com.example.eventplanner.models.Venue;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminVenueDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ActivityAdminVenueDetailsBinding binding;
    private GoogleMap mMap;
    Venue selectedVenue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminVenueDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrieve the Venue object
        selectedVenue = (Venue) getIntent().getSerializableExtra("selectedVenue");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.admin_map_container, mapFragment).commit();
        mapFragment.getMapAsync(this);

        // Use the Venue object
        if (selectedVenue != null) {
            binding.venueName.setText(selectedVenue.getName());
            binding.venueAddress.setText(selectedVenue.getAddress());
            binding.venueAbout.setText(selectedVenue.getAbout());
            binding.venueOwnName.setText(selectedVenue.getOwner().getName());
            binding.venueOwnerPhone.setText(selectedVenue.getOwner().getPhone());
            binding.venueContact.setText(selectedVenue.getPhone());

//            Setting image
            String imageUrl = AppConfig.SERVER_URL + selectedVenue.getPicture();
            Glide.with(this).load(imageUrl).placeholder(R.drawable.event_image_1).into(binding.venueImage);

//            set menu
            ManuVenueDetailsAdapter adapter = new ManuVenueDetailsAdapter((ArrayList<MenuItem>) selectedVenue.getFoodMenuItems());
            binding.menuRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            binding.menuRecyclerView.setAdapter(adapter);
        }


        binding.btnVenueApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVenueStatus(selectedVenue.getId(), 1);
            }
        });

        binding.btnVenueReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle delete button click
                changeVenueStatus(selectedVenue.getId(), 2);
            }
        });
    }

    private void changeVenueStatus(int venueId, int status) {

        // Show a confirmation dialog to the user
        String title = status == 1 ? "Approve Venue" : "Reject Venue";
        String message = status == 1 ? "Are you sure you want to approve this venue?" : "Are you sure you want to reject this venue?";
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setPositiveButton("Confirm", (dialog, which) -> {

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("venue_id", venueId);
            requestBody.put("status", status);

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<ApiResponse> call = apiService.changeVenueStatus(requestBody);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AdminVenueDetailsActivity.this, title + " successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(AdminVenueDetailsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    // Handle failure
                    Log.e("Booking Failure", t.getMessage());
                    Toast.makeText(AdminVenueDetailsActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                }
            });
        }).setNegativeButton("Cancel", (dialog, which) -> {
            // User canceled the deletion
            dialog.dismiss();
        }).show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker and move the camera
        LatLng location = new LatLng(selectedVenue.getLatitude(), selectedVenue.getLongitude());
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.addMarker(new MarkerOptions().position(location).title(selectedVenue.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));

    }
}