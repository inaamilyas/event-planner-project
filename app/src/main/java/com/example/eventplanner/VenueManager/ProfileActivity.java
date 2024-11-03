package com.example.eventplanner.VenueManager;

import static com.example.eventplanner.VenueManager.DashboardVenueManagerActivity.venueManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.config.AppConfig;
import com.example.eventplanner.databinding.ActivityProfileBinding;
import com.example.eventplanner.models.User;
import com.example.eventplanner.models.VenueManager;

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

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private static final int PICK_IMAGE_REQUEST = 12;
    private static final int REQUEST_STORAGE_PERMISSION = 100;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check for storage permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        }


        binding.fullName.setText(venueManager.getName());
        binding.email.setText(venueManager.getEmail());
        if (venueManager.getProfilePic() != null) {
            String imageUrl = AppConfig.SERVER_URL + venueManager.getProfilePic();
            Glide.with(this).load(imageUrl).placeholder(R.drawable.enent_image).into(binding.profileImage);
        }

        // Select Image Button
        binding.selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        // Save Changes Button
        binding.saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.fullName.getText().toString();
                String email = binding.email.getText().toString();
                String password = binding.changePassword.getText().toString();
                String confPassword = binding.confPassword.getText().toString();

                // Validate form
                if (name.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "Name can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.isEmpty() && !password.equals(confPassword)) {
                    Toast.makeText(ProfileActivity.this, "Password and confirm password do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convert URI to File for Image Upload
                File imageFile = null;
                if (imageUri != null) {
                    try {
                        imageFile = getFileFromUri(imageUri);
                    } catch (IOException e) {
                        Toast.makeText(ProfileActivity.this, "Failed to get image file", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // Prepare Multipart Request
                RequestBody fullNamePart = RequestBody.create(MediaType.parse("multipart/form-data"), name);
                RequestBody emailPart = RequestBody.create(MediaType.parse("multipart/form-data"), email);
                RequestBody passwordPart = RequestBody.create(MediaType.parse("multipart/form-data"), password.isEmpty() ? "" : password);

                MultipartBody.Part body = null;
                if (imageFile != null) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                    body = MultipartBody.Part.createFormData("picture", imageFile.getName(), requestFile);
                }

                // API call
                ApiService apiService = ApiClient.getClient().create(ApiService.class);

                Call<ApiResponse<VenueManager>> call = apiService.updateManagerProfile(venueManager.getId(), body, fullNamePart, emailPart, passwordPart);

                call.enqueue(new Callback<ApiResponse<VenueManager>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<VenueManager>> call, Response<ApiResponse<VenueManager>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                            assert response.body() != null;
                            VenueManager user = response.body().getData();
                            venueManager.setName(user.getName());
                            venueManager.setEmail(user.getEmail());
                            venueManager.setProfilePic(user.getProfilePic());
                            user.saveToPreferences(ProfileActivity.this);
                        } else {
                            Toast.makeText(ProfileActivity.this, !response.message().isEmpty() ? response.message() : "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<VenueManager>> call, Throwable t) {
                        Toast.makeText(ProfileActivity.this, "An error occurred! Please check your internet.", Toast.LENGTH_SHORT).show();
                    }
                });
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.profileImage.setImageURI(imageUri); // Set selected image in ImageView
        }
    }

    private File getFileFromUri(Uri uri) throws IOException {
        File tempFile = File.createTempFile("image", ".jpg", this.getCacheDir());

        try (InputStream inputStream = this.getContentResolver().openInputStream(uri); OutputStream outputStream = new FileOutputStream(tempFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    }
}