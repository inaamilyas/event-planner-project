package com.example.eventplanner.VenueManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.eventplanner.R;
import com.example.eventplanner.VenueManager.MenuItem.AddMenuActivity;
import com.example.eventplanner.VenueManager.MenuItem.MenuItemsActivity;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.databinding.ActivityAddVenueBinding;
import com.example.eventplanner.models.Venue;
import com.example.eventplanner.models.VenueManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

public class AddVenueActivity extends FragmentActivity implements OnMapReadyCallback {

    private ActivityAddVenueBinding binding;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    private Marker selectedMarker;
    private LatLng selectedLatLng;

    private static final int PICK_IMAGE_REQUEST = 12;
    private static final int REQUEST_STORAGE_PERMISSION = 120;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddVenueBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check for storage permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
          ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        }

        // Set a custom title for the toolbar
        setTitle("");

        // Initialize fusedLocationClient to get user's location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.select_map_container, mapFragment).commit();
        mapFragment.getMapAsync(this);

        binding.btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });


        binding.saveVenueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.saveVenueButton.setText("Loading...");
                binding.saveVenueButton.setEnabled(false);

                // Ensure permissions are granted before proceeding
//                if (ContextCompat.checkSelfPermission(AddVenueActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // Your code to handle the button click
                    try {
                        saveVenue();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
//                } else {
//                    Toast.makeText(AddVenueActivity.this, "Storage permission is required to select an image", Toast.LENGTH_SHORT).show();
////                    ActivityCompat.requestPermissions(, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
//                }
            }
        });

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

        // Get the user's current location and move the camera
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

                // Place a marker at the user's current location
                selectedLatLng = userLocation;
                selectedMarker = mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Current Location"));
            } else {
                Toast.makeText(AddVenueActivity.this, "Unable to get your location", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(AddVenueActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show());

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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(mMap);
            } else {
                Toast.makeText(this, "Location Permission denied", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            }
        }
    }

    private void saveVenue() throws IOException {
        // Check if the image is selected
        if (imageUri == null) {
            Toast.makeText(AddVenueActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
            binding.saveVenueButton.setText("Add Venue");
            return;
        }

        // Convert URI to File and proceed with the API call
        File imageFile = getFileFromUri(imageUri);

        // Check if the file is valid after conversion from the URI
        if (imageFile == null || !imageFile.exists()) {
            Toast.makeText(AddVenueActivity.this, "Please select a valid image", Toast.LENGTH_SHORT).show();
            binding.saveVenueButton.setText("Add Venue");
            return;
        }


        // Get input from EditText fields
        String venueName = binding.etVenueName.getText().toString().trim();
        String venuePhone = binding.etVenuePhone.getText().toString().trim();
        String venueAbout = binding.etVenueAbout.getText().toString().trim();

        // Validation checks for the fields
        if (venueName.isEmpty()) {
            Toast.makeText(AddVenueActivity.this, "Venue name is required", Toast.LENGTH_SHORT).show();
            binding.saveVenueButton.setText("Add Venue");
            return;
        }

        if (venuePhone.isEmpty()) {
            Toast.makeText(AddVenueActivity.this, "Venue phone number is required", Toast.LENGTH_SHORT).show();
            binding.saveVenueButton.setText("Add Venue");
            return;
        }

        if (venueAbout.isEmpty() || venueAbout.length() < 20) {
            Toast.makeText(AddVenueActivity.this, "About section must be at least 20 characters long", Toast.LENGTH_SHORT).show();
            binding.saveVenueButton.setText("Add Venue");
            return;
        }

        // Ensure that the selectedLatLng (latitude and longitude) has been set
        if (selectedLatLng == null) {
            Toast.makeText(AddVenueActivity.this, "Please select a location on the map", Toast.LENGTH_SHORT).show();
            binding.saveVenueButton.setText("Add Venue");
            return;
        }

        // Ensure that the selectedLatLng (latitude and longitude) has been set
        if (selectedLatLng == null) {
            Toast.makeText(AddVenueActivity.this, "Please select a location on the map", Toast.LENGTH_SHORT).show();
//                    resetButtonState();
            return;
        }

        // Collect latitude and longitude
        double latitude = selectedLatLng.latitude;
        double longitude = selectedLatLng.longitude;

        // Create RequestBody for the image file
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);

        // Create MultipartBody.Part using file request-body, file name, and part name
        MultipartBody.Part body = MultipartBody.Part.createFormData("picture", imageFile.getName(), requestFile);

        // Create other request bodies
        RequestBody venueNamePart = RequestBody.create(MediaType.parse("multipart/form-data"), venueName);
        RequestBody venuePhonePart = RequestBody.create(MediaType.parse("multipart/form-data"), venuePhone);
        RequestBody venueAboutPart = RequestBody.create(MediaType.parse("multipart/form-data"), venueAbout);
        RequestBody latitudePart = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(latitude));
        RequestBody longitudePart = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(longitude));

        int managerId = VenueManager.getFromPreferences(this).getId();
        RequestBody managerIdPart = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(managerId));

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<Venue>> call = apiService.addVenue(body, venueNamePart, venuePhonePart, venueAboutPart, latitudePart, longitudePart,managerIdPart);
        call.enqueue(new Callback<ApiResponse<Venue>>() {
            @Override
            public void onResponse(Call<ApiResponse<Venue>> call, Response<ApiResponse<Venue>> response) {
                // Handle success
                if (response.isSuccessful()) {
                    // Do something with the response
                    Toast.makeText(AddVenueActivity.this, "Venue created successfully", Toast.LENGTH_SHORT).show();
                    assert response.body() != null;
                    Venue venue = (Venue) response.body().getData();
                    Intent intent = new Intent(AddVenueActivity.this, AddMenuActivity.class);
                    intent.putExtra("selectedVenue", venue);
                    startActivity(intent);
                    finish();
                } else {
                    // Handle error
                    Toast.makeText(AddVenueActivity.this, "Failed to create venue", Toast.LENGTH_SHORT).show();
                    binding.saveVenueButton.setText("Add Venue");
                    binding.saveVenueButton.isEnabled();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Venue>> call, Throwable t) {
                // Handle failur
                Toast.makeText(AddVenueActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                binding.saveVenueButton.setText("Add Venue");
                binding.saveVenueButton.isEnabled();
            }
        });

    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.ivSelectedImage.setImageURI(imageUri);
        }
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


}
