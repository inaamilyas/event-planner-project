package com.example.eventplanner.VenueManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.databinding.ActivityVenueLoginBinding;
import com.example.eventplanner.models.VenueManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VenueLoginActivity extends AppCompatActivity {

    private ActivityVenueLoginBinding binding;
    private static final int STORAGE_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVenueLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        setSupportActionBar(binding.myToolbar);

        binding.btnVenueLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.etLoginEmail.getText().toString();
                String password = binding.etLoginPassword.getText().toString();

                binding.btnVenueLogin.setEnabled(false);
                binding.btnVenueLogin.setText("Loading...");

                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("email", email);
                requestBody.put("password", password);
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG=====", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        requestBody.put("fcm_token", token);

                        Call<ApiResponse<VenueManager>> call = apiService.venueManagerLogin(requestBody);

                        call.enqueue(new Callback<ApiResponse<VenueManager>>() {

                            @Override
                            public void onResponse(Call<ApiResponse<VenueManager>> call, Response<ApiResponse<VenueManager>> response) {
                                if (response.isSuccessful()) {
                                    ApiResponse<VenueManager> apiResponse = response.body();
                                    if (apiResponse != null && apiResponse.getCode() == 200) {

                                        // Handle success
                                        VenueManager venueManager = apiResponse.getData();
                                        venueManager.saveToPreferences(VenueLoginActivity.this);
                                        Toast.makeText(VenueLoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(VenueLoginActivity.this, DashboardVenueManagerActivity.class));
                                        finish();
                                    } else {
                                        // Handle error based on API response
                                        assert apiResponse != null;
                                        binding.tvShowError.setText(apiResponse.getMessage());
                                    }
                                } else {
                                    try {
                                        // Parse the error body to get the API response
                                        Gson gson = new Gson();
                                        ApiResponse<?> apiErrorResponse = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                                        if (apiErrorResponse != null) {
                                            binding.tvShowError.setText(apiErrorResponse.getMessage());
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        binding.tvShowError.setText("An unexpected error occurred.");
                                    }
                                }

                                binding.btnVenueLogin.setEnabled(true);
                                binding.btnVenueLogin.setText("Sign in");
                            }

                            @Override
                            public void onFailure(Call<ApiResponse<VenueManager>> call, Throwable t) {
                                // Handle failure (e.g., no internet connection)
                                binding.tvShowError.setText("Failed to connect. Please check your internet connection.");
                                binding.btnVenueLogin.setEnabled(true);
                                binding.btnVenueLogin.setText("Sign in");
                            }
                        });

                    }
                });


            }
        });

        binding.tvGoToVenueSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VenueLoginActivity.this, VenueSignupActivity.class));
                finish();
            }
        });
    }
}