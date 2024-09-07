package com.example.eventplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.example.eventplanner.config.AppConfig;
import com.example.eventplanner.databinding.ActivityVenueDetailsBinding;
import com.example.eventplanner.models.Venue;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

        binding.gotoBookVenueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VenueDetailsActivity.this, MenuSelectionActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker and move the camera
        LatLng location = new LatLng(selectedVenue.getLatitude(), selectedVenue.getLongitude());
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.addMarker(new MarkerOptions().position(location).title(selectedVenue.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));

    }
}