package com.example.eventplanner.fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
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

import com.example.eventplanner.VenueManager.MenuItem.AddMenuActivity;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.databinding.FragmentProfileBinding;
import com.example.eventplanner.models.MenuItem;
import com.example.eventplanner.models.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        }

        binding.selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

//        binding.saveChanges.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String name = binding.fullName.getText().toString();
//                String email = binding.email.getText().toString();
//                String password = binding.changePassword.getText().toString();
//                String confPassword = binding.confPassword.getText().toString();
//
//                if(name.isEmpty()){
//                    Toast.makeText(getContext(), "Name can't be empty", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if(email.isEmpty()){
//                    Toast.makeText(getContext(), "Email can't be empty", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if(!password.equals(confPassword)){
//                    Toast.makeText(getContext(), "Password and confirm password are different", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                // Convert URI to File and proceed with the API call
//                File imageFile = null;
//                try {
//                    imageFile = getFileFromUri(imageUri);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//
//                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
//                MultipartBody.Part body = MultipartBody.Part.createFormData("picture", imageFile.getName(), requestFile);
//
//                RequestBody fullNamePart = RequestBody.create(MediaType.parse("multipart/form-data"), name);
//                RequestBody emailPart = RequestBody.create(MediaType.parse("multipart/form-data"), email);
//                RequestBody passwordPart = RequestBody.create(MediaType.parse("multipart/form-data"), password);
//                RequestBody confPasswordPart = RequestBody.create(MediaType.parse("multipart/form-data"), confPassword);
//
//
//                ApiService apiService = ApiClient.getClient().create(ApiService.class);
//                User user = User.getFromPreferences(requireContext());
//
//                Call<ApiResponse<String>> call = apiService.updateUserProfile(user.getId(),body, fullNamePart, emailPart, passwordPart, confPasswordPart);
//
//                call.enqueue(new Callback<ApiResponse<String>>() {
//                    @Override
//                    public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
//                        // Handle success
//                        if (response.isSuccessful()) {
//                            // Do something with the response
////                            binding.saveChanges.setText("Save Changes");
//                            Toast.makeText(requireContext(), "Profile added successfully", Toast.LENGTH_SHORT).show();
//                            assert response.body() != null;
//                        } else {
//                            // Handle error
////                            binding.saveChanges.setText("Save Changes");
//                            Toast.makeText(requireContext(), "Failed to add venue", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
//                        // Handle failure
//                        Toast.makeText(requireContext(), "An error occurred", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//
//            }
//        });



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
            binding.profileImage.setImageURI(imageUri);
        }
    }

    private String getFileFromUri(Uri uri) throws IOException {
//        InputStream inputStream = getContentResolver().openInputStream(uri);
//        File tempFile = File.createTempFile("image", ".jpg", getCacheDir());
//        OutputStream outputStream = new FileOutputStream(tempFile);
//
//        byte[] buffer = new byte[1024];
//        int bytesRead;
//        while ((bytesRead = inputStream.read(buffer)) != -1) {
//            outputStream.write(buffer, 0, bytesRead);
//        }
//        inputStream.close();
//        outputStream.close();
//
//        return tempFile;

        return "filename";
    }


}
