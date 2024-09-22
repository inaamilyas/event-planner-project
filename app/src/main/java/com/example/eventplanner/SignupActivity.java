package com.example.eventplanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.databinding.ActivitySignupBinding;
import com.example.eventplanner.models.User;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        binding.tvGoToSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
            }
        });

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

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("name", name);
                requestBody.put("email", email);
                requestBody.put("password", password);
                requestBody.put("confirmPassword", confirmPassword);
                Call<ApiResponse<User>> call = apiService.signup(requestBody);

                call.enqueue(new Callback<ApiResponse<User>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                        if (response.isSuccessful()) {
                            ApiResponse<User> apiResponse = response.body();
                            if (apiResponse != null && apiResponse.getCode() == 200) {
                                // Handle success
                                User user = apiResponse.getData();
                                user.saveToPreferences(SignupActivity.this);
                                Toast.makeText(SignupActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                // Navigate to MainActivity
                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
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
                                if (apiErrorResponse != null && apiErrorResponse.getCode() == 400) {
                                    // Display the API error message
                                    binding.tvSignupApiError.setText(apiErrorResponse.getMessage());
                                } else {
                                    // Handle other errors
                                    binding.tvSignupApiError.setText("Something went wrong. Please try again.");
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
                    public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                        // Handle failure (e.g., no internet connection)
                        Log.d("inaamilyas", "onFailure: "+ t);
                        binding.tvSignupApiError.setText("Failed to connect. Please check your internet connection.");
                        binding.btnSignup.setEnabled(true);
                        binding.btnSignup.setText("Sign Up");
                    }
                });


            }
        });
    }

    private void setSupportActionBar(Toolbar myToolbar) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login_sign, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_signup_venue_manager) {

            return true;
        } else if (item.getItemId() == R.id.action_login_venue_manager) {

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}