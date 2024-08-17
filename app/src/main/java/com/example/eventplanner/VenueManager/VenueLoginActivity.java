package com.example.eventplanner.VenueManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.databinding.ActivityVenueLoginBinding;
import com.example.eventplanner.datamodels.requests.LoginRequest;
import com.example.eventplanner.datamodels.responses.LoginResponse;
import com.google.gson.Gson;

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
                LoginRequest request = new LoginRequest(email, password);
                Log.d("inaamilyas", "after request");
                Call<ApiResponse<LoginResponse>> call = apiService.venueManagerLogin(request);

                call.enqueue(new Callback<ApiResponse<LoginResponse>>() {

                    @Override
                    public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                        Log.d("inaamilyas", "inside request");
                        if (response.isSuccessful()) {
                            ApiResponse<LoginResponse> apiResponse = response.body();
                            if (apiResponse != null && apiResponse.getCode() == 200) {

                                Log.d("inaamilyas", "" + apiResponse.getData());

                                // Handle success
                                LoginResponse loginData = apiResponse.getData();

                                // Save user data to SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("EventPlannerPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("venuerManagerUserId", loginData.getUserId());
                                editor.putString("venueManagerToken", loginData.getToken());
                                editor.apply();

                                Toast.makeText(VenueLoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                                // Navigate to MainActivity
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
                    public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                        // Handle failure (e.g., no internet connection)
                        binding.tvShowError.setText("Failed to connect. Please check your internet connection.");
                        binding.btnVenueLogin.setEnabled(true);
                        binding.btnVenueLogin.setText("Sign in");
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