package com.example.eventplanner.VenueManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplanner.MainActivity;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.databinding.ActivityVenueManagerSignupBinding;
import com.example.eventplanner.datamodels.requests.SignupRequest;
import com.example.eventplanner.datamodels.responses.SignupResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VenueSignupActivity extends AppCompatActivity {

    private ActivityVenueManagerSignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityVenueManagerSignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        setSupportActionBar(binding.myToolbar);

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.btnSignup.setEnabled(false);
                binding.btnSignup.setText("Loading...");

                // Usage in your Activity or ViewModel
                ApiService apiService = ApiClient.getClient().create(ApiService.class);

                String name = binding.etSignupName.getText().toString();
                String email = binding.etSignupEmail.getText().toString();
                String password = binding.etSignupPassword.getText().toString();
                String confirmPassword = binding.etSignupConfPass.getText().toString();
                String phoneNumber = binding.etSignupPhoneNumber.getText().toString();

                SignupRequest request = new SignupRequest(name, email, phoneNumber, password, confirmPassword);

                Call<ApiResponse<SignupResponse>> call = apiService.venueManagerSignup(request);

                call.enqueue(new Callback<ApiResponse<SignupResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<SignupResponse>> call, Response<ApiResponse<SignupResponse>> response) {
                        if (response.isSuccessful()) {
                            ApiResponse<SignupResponse> apiResponse = response.body();
                            if (apiResponse != null) {
                                // Handle success
                                SignupResponse signupData = apiResponse.getData();

                                // Save user data to SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("EventPlannerPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("venueManagerUserId", signupData.getUserId());
                                editor.putString("venueManagerToken", signupData.getToken());
                                editor.apply();

                                Toast.makeText(VenueSignupActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                // Navigate to MainActivity
                                startActivity(new Intent(VenueSignupActivity.this, DashboardVenueManagerActivity.class));
                                finish();
                            } else {
                                // Handle error based on API response
                                assert apiResponse != null;
                                binding.tvSignupApiError.setText(apiResponse.getMessage());
                            }
                        } else {
                            try {
                                // Parse the error body to get the API response
                                Gson gson = new Gson();
                                ApiResponse<?> apiErrorResponse = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                                if (apiErrorResponse != null) {
                                    // Display the API error message
                                    binding.tvSignupApiError.setText(apiErrorResponse.getMessage());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                binding.tvSignupApiError.setText("An unexpected error occurred.");
                            }
                        }

                        binding.btnSignup.setEnabled(true);
                        binding.btnSignup.setText("Sign Up");
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<SignupResponse>> call, Throwable t) {
                        // Handle failure (e.g., no internet connection)
                        binding.tvSignupApiError.setText("Failed to connect. Please check your internet connection.");
                        binding.btnSignup.setEnabled(false);
                        binding.btnSignup.setText("Loading...");
                    }
                });


            }
        });


    }
}