package com.example.eventplanner.Admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplanner.LoginActivity;
import com.example.eventplanner.VenueManager.VenueLoginActivity;
import com.example.eventplanner.databinding.ActivityAdminLoginBinding;

public class AdminLoginActivity extends AppCompatActivity {
    private ActivityAdminLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnVenueLogin.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                String email = binding.etLoginEmail.getText().toString();
                String password = binding.etLoginPassword.getText().toString();

                binding.btnVenueLogin.setEnabled(false);
                binding.btnVenueLogin.setText("Loading...");
                if (email.equals("admin@gmail.com") && password.equals("admin")) {
                    startActivity(new Intent(AdminLoginActivity.this, AdminDashboardActivity.class));
                    finish();
                } else {
                    binding.btnVenueLogin.setText("Sign In");
                    binding.tvShowError.setText("Incorrect email or password");
                    Toast.makeText(AdminLoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }


//
////                ApiService apiService = ApiClient.getClient().create(ApiService.class);
////                Map<String, Object> requestBody = new HashMap<>();
////                requestBody.put("email", email);
////                requestBody.put("password", password);
////                Call<ApiResponse> call = apiService.adminLogin(requestBody);
////
////                call.enqueue(new Callback<ApiResponse>() {
////
////                    @Override
////                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
////                        if (response.isSuccessful()) {
////                            ApiResponse<VenueManager> apiResponse = response.body();
////                            if (apiResponse != null && apiResponse.getCode() == 200) {
////
////                                Toast.makeText(AdminLoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
//////                                startActivity(new Intent(AdminLoginActivity.this, DashboardVenueManagerActivity.class));
//////                                finish();
////                            } else {
////                                // Handle error based on API response
////                                assert apiResponse != null;
////                                binding.tvShowError.setText(apiResponse.getMessage());
////                            }
////                        } else {
////                            try {
////                                // Parse the error body to get the API response
////                                Gson gson = new Gson();
////                                ApiResponse<?> apiErrorResponse = gson.fromJson(response.errorBody().string(), ApiResponse.class);
////                                if (apiErrorResponse != null) {
////                                    binding.tvShowError.setText(apiErrorResponse.getMessage());
////                                }
////                            } catch (Exception e) {
////                                e.printStackTrace();
////                                binding.tvShowError.setText("An unexpected error occurred.");
////                            }
////                        }
////
////                        binding.btnVenueLogin.setEnabled(true);
////                        binding.btnVenueLogin.setText("Sign in");
////                    }
////
////                    @Override
////                    public void onFailure(Call<ApiResponse> call, Throwable t) {
////                        // Handle failure (e.g., no internet connection)
////                        binding.tvShowError.setText("Failed to connect. Please check your internet connection.");
////                        binding.btnVenueLogin.setEnabled(true);
////                        binding.btnVenueLogin.setText("Sign in");
////                    }
////                });


            }
        });

        binding.goToUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminLoginActivity.this, LoginActivity.class));
                finish();
            }
        });
        
        binding.goToVenueManagerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminLoginActivity.this, VenueLoginActivity.class));
                finish();
            }
        });

    }
}