package com.example.eventplanner.fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.config.AppConfig;
import com.example.eventplanner.databinding.FragmentProfileBinding;
import com.example.eventplanner.models.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private static final int PICK_IMAGE_REQUEST = 12;
    private static final int REQUEST_STORAGE_PERMISSION = 100;
    private Uri imageUri;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using view binding
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Check for storage permissions
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        }

        User user = User.getFromPreferences(requireContext());

        binding.fullName.setText(user.getName());
        binding.email.setText(user.getEmail());
        if (user.getProfilePic() != null) {
            String imageUrl = AppConfig.SERVER_URL + user.getProfilePic();
            Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.enent_image)
                    .into(binding.profileImage);
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
                    Toast.makeText(getContext(), "Name can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.isEmpty() && !password.equals(confPassword)) {
                    Toast.makeText(getContext(), "Password and confirm password do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convert URI to File for Image Upload
                File imageFile = null;
                if (imageUri != null) {
                    try {
                        imageFile = getFileFromUri(imageUri);
                    } catch (IOException e) {
                        Toast.makeText(getContext(), "Failed to get image file", Toast.LENGTH_SHORT).show();
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

                Call<ApiResponse<User>> call = apiService.updateUserProfile(user.getId(), body, fullNamePart, emailPart, passwordPart);

                call.enqueue(new Callback<ApiResponse<User>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();

                            assert response.body() != null;
                            User user = response.body().getData();
                            user.saveToPreferences(requireContext());
                        } else {
                            Toast.makeText(requireContext(), !response.message().isEmpty() ? response.message() : "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                        Toast.makeText(requireContext(), "An error occurred! Please check your internet.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
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
        File tempFile = File.createTempFile("image", ".jpg", requireContext().getCacheDir());

        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(tempFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    }
}
