package com.example.eventplanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventplanner.VenueManager.VenueLoginActivity;
import com.example.eventplanner.VenueManager.VenueSignupActivity;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.databinding.ActivityLoginBinding;
import com.example.eventplanner.datamodels.requests.LoginRequest;
import com.example.eventplanner.datamodels.requests.SignupRequest;
import com.example.eventplanner.datamodels.responses.LoginResponse;
import com.example.eventplanner.datamodels.responses.SignupResponse;
import com.google.gson.Gson;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.btnLogin.setEnabled(false);
                binding.btnLogin.setText("Loading...");

                String email = binding.etLoginEmail.getText().toString();
                String password = binding.etLoginPassword.getText().toString();

                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                LoginRequest request = new LoginRequest(email, password);
                Call<ApiResponse<LoginResponse>> call = apiService.login(request);

                call.enqueue(new Callback<ApiResponse<LoginResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                        if (response.isSuccessful()) {
                            ApiResponse<LoginResponse> apiResponse = response.body();
                            if (apiResponse != null && apiResponse.getCode() == 200) {

                                Log.d("inaamilyas", ""+ apiResponse.getData());

                                // Handle success
                                LoginResponse loginData = apiResponse.getData();

                                // Save user data to SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("EventPlannerPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("userId", loginData.getUserId());
                                editor.putString("token", loginData.getToken());
                                editor.apply();

                                Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                                // Navigate to MainActivity
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
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

                        binding.btnLogin.setEnabled(true);
                        binding.btnLogin.setText("Sign Up");
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                        // Handle failure (e.g., no internet connection)
                        binding.tvShowError.setText("Failed to connect. Please check your internet connection.");
                        binding.btnLogin.setEnabled(true);
                        binding.btnLogin.setText("Sign Up");
                    }
                });

            }
        });

        binding.tvGoToForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
                finish();
            }
        });

        binding.tvGoToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                finish();
            }
        });

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
            startActivity(new Intent(LoginActivity.this, VenueSignupActivity.class));
            return true;
        } else if (item.getItemId() == R.id.action_login_venue_manager) {
            startActivity(new Intent(LoginActivity.this, VenueLoginActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}