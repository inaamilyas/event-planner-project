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
import com.example.eventplanner.databinding.ActivityVenueManagerSignupBinding;
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

                // Usage in your Activity or ViewModel
                ApiService apiService = ApiClient.getClient().create(ApiService.class);

                String name = binding.etSignupName.getText().toString();
                String email = binding.etSignupEmail.getText().toString();
                String password = binding.etSignupPassword.getText().toString();
                String confirmPassword = binding.etSignupConfPass.getText().toString();
                String phoneNumber = binding.etSignupPhoneNumber.getText().toString();

                // Regular expression patterns
                String namePattern = "^[a-zA-Z\\s]+$"; // Allows letters and spaces only
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$"; // At least one digit, one lower case, one upper case, one special character, and minimum 8 characters

                // Validate Name
                if (name.isEmpty() || !name.matches(namePattern)) {
                    binding.tvSignupApiError.setText("Name should only contain letters and spaces");
                    return;
                }

                // Validate Email
                if (email.isEmpty() || !email.matches(emailPattern)) {
                    binding.tvSignupApiError.setText("Please enter a valid email address");
                    return;
                }

                // Validate Password
                if (password.isEmpty() || password.length() < 8) {
                    binding.tvSignupApiError.setText("Password must be at least 8 characters long.");
                    return;
                }

                // Validate Confirm Password
                if (!confirmPassword.equals(password)) {
                    binding.tvSignupApiError.setText("Passwords do not match");
                    return;
                }

                // Validate Phone Number
                if (phoneNumber.isEmpty() || phoneNumber.length()<9) {
                    binding.tvSignupApiError.setText("Please enter a valid phone number");
                    return;
                }

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("name", name);
                requestBody.put("confirmPassword", confirmPassword);
                requestBody.put("phone", phoneNumber);
                requestBody.put("email", email);
                requestBody.put("password", password);

                binding.btnSignup.setText("Loading...");

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


                        Call<ApiResponse<VenueManager>> call = apiService.venueManagerSignup(requestBody);

                        call.enqueue(new Callback<ApiResponse<VenueManager>>() {
                            @Override
                            public void onResponse(Call<ApiResponse<VenueManager>> call, Response<ApiResponse<VenueManager>> response) {
                                if (response.isSuccessful()) {
                                    ApiResponse<VenueManager> apiResponse = response.body();
                                    if (apiResponse != null) {
                                        // Handle success
                                        VenueManager venueManager = apiResponse.getData();
                                        venueManager.saveToPreferences(VenueSignupActivity.this);

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
                                binding.btnSignup.setText("Sign Up");
                            }

                            @Override
                            public void onFailure(Call<ApiResponse<VenueManager>> call, Throwable t) {
                                // Handle failure (e.g., no internet connection)
                                binding.tvSignupApiError.setText("Failed to connect. Please check your internet connection.");
                                binding.btnSignup.setEnabled(false);
                                binding.btnSignup.setText("Loading...");
                            }
                        });

                    }
                });



            }
        });

        binding.tvGoToSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VenueSignupActivity.this, VenueLoginActivity.class));
                finish();
            }
        });

    }
}