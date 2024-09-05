package com.example.eventplanner.VenueManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.config.AppConfig;
import com.example.eventplanner.databinding.ActivityEditVenueBinding;
import com.example.eventplanner.models.Venue;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditVenueActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityEditVenueBinding binding;
    private Venue selectedVenue;
    //    private LatLng selectedLocation;
    private Uri selectedImageUri;
    private Bitmap selectedImageBitmap;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    private Marker selectedMarker;
    private LatLng selectedLatLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditVenueBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrieve the Venue object from the intent
        selectedVenue = (Venue) getIntent().getSerializableExtra("selectedVenue");

        // Populate the UI with the venue data
        if (selectedVenue != null) {
            populateVenueData(selectedVenue);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.select_map_container, mapFragment).commit();
        mapFragment.getMapAsync(this);

        // Set up the save button click listener to handle venue update
        binding.saveVenueButton.setOnClickListener(v -> {
            saveVenue();
        });

        // Handle image selection
        binding.btnSelectImage.setOnClickListener(v -> selectImageFromGallery());
    }

    private void populateVenueData(Venue venue) {
        // Set venue details to the UI components
        binding.etVenueName.setText(venue.getName());
        binding.etVenuePhone.setText(venue.getPhone());
        binding.etVenueAbout.setText(venue.getAbout());

        // Set the image using Glide
        String imageUrl = AppConfig.SERVER_URL + venue.getPicture();
        Glide.with(this).load(imageUrl).placeholder(R.drawable.event_image_1).into(binding.ivSelectedImage);

        // Set the location on the map
        selectedLatLng = new LatLng(venue.getLatitude(), venue.getLongitude());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        ;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15));
        selectedMarker = mMap.addMarker(new MarkerOptions().position(selectedLatLng).title("Your Current Location"));

        // Set a map click listener to get the location the user selects
        mMap.setOnMapClickListener(latLng -> {
            // Remove previous marker if any
            if (selectedMarker != null) {
                selectedMarker.remove();
            }

            // Place a marker at the selected location
            selectedMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Venue Location"));
            selectedLatLng = latLng;  // Save the selected lat and long
        });
    }

    private void saveVenue() {
        // Check if an image URI is selected
        File imageFile = null;
        MultipartBody.Part body = null;

        // Convert URI to File if not null
        if (selectedImageUri != null) {
            try {
                imageFile = getFileFromUri(selectedImageUri);
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                body = MultipartBody.Part.createFormData("picture", imageFile.getName(), requestFile);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(EditVenueActivity.this, "Error processing image", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Get input from EditText fields
        String venueName = binding.etVenueName.getText().toString().trim();
        String venuePhone = binding.etVenuePhone.getText().toString().trim();
        String venueAbout = binding.etVenueAbout.getText().toString().trim();

        // Ensure that the selectedLatLng (latitude and longitude) has been set
        if (selectedLatLng == null) {
            Toast.makeText(EditVenueActivity.this, "Please select a location on the map", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate input data
        if (venueName.isEmpty() || venuePhone.isEmpty() || venueAbout.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Collect latitude and longitude
        double latitude = selectedLatLng.latitude;
        double longitude = selectedLatLng.longitude;

        // Create other request bodies
        RequestBody venueNamePart = RequestBody.create(MediaType.parse("multipart/form-data"), venueName);
        RequestBody venuePhonePart = RequestBody.create(MediaType.parse("multipart/form-data"), venuePhone);
        RequestBody venueAboutPart = RequestBody.create(MediaType.parse("multipart/form-data"), venueAbout);
        RequestBody latitudePart = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(latitude));
        RequestBody longitudePart = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(longitude));

        // Initialize ApiService and call
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<Venue>> call = apiService.updateVenue(
                String.valueOf(selectedVenue.getId()), // Pass venueId as a path parameter
                body, // Picture as MultipartBody.Part or null if no image
                venueNamePart, // Venue name part
                venuePhonePart, // Venue phone part
                venueAboutPart, // Venue about part
                latitudePart, // Latitude part
                longitudePart // Longitude part
        );

        call.enqueue(new Callback<ApiResponse<Venue>>() {
            @Override
            public void onResponse(Call<ApiResponse<Venue>> call, Response<ApiResponse<Venue>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditVenueActivity.this, "Venue updated successfully", Toast.LENGTH_SHORT).show();
                    Venue updatedVenue = response.body().getData();
                    startActivity(new Intent(EditVenueActivity.this, DashboardVenueManagerActivity.class));
                    finish();
                } else {
                    Log.d("inaamilyas", "onResponse: " + response.code());
                    Toast.makeText(EditVenueActivity.this, "Failed to update venue", Toast.LENGTH_SHORT).show();
                    binding.saveVenueButton.setText("Update Venue");
                    binding.saveVenueButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Venue>> call, Throwable t) {
                Toast.makeText(EditVenueActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                binding.saveVenueButton.setText("Update Venue");
                binding.saveVenueButton.setEnabled(true);
            }
        });
    }


    private File getFileFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("image", ".jpg", getCacheDir());
        OutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        outputStream.close();

        return tempFile;
    }

    // Method to handle image selection from gallery
    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    // Register activity result launcher for image selection
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            selectedImageUri = result.getData().getData();
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                binding.ivSelectedImage.setImageBitmap(selectedImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(EditVenueActivity.this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    });
}
