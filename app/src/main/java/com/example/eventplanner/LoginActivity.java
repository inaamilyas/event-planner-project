package com.example.eventplanner;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplanner.Admin.AdminLoginActivity;
import com.example.eventplanner.VenueManager.VenueLoginActivity;
import com.example.eventplanner.VenueManager.VenueSignupActivity;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.databinding.ActivityLoginBinding;
import com.example.eventplanner.models.User;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
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

                // Create request body with email and password
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("email", email);
                requestBody.put("password", password);

                // Get the FCM token
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("TAG=======", "Fetching FCM registration token failed", task.getException());
                        // Handle failure to get token
//                        binding.tvShowError.setText("Failed to get FCM token.");
                        resetLoginButton();
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
//                    Log.d("TAG=======", "FCM Token: " + token);
                    requestBody.put("fcm_token", token);

                    // Now make the login API call
                    ApiService apiService = ApiClient.getClient().create(ApiService.class);
                    Call<ApiResponse<User>> call = apiService.login(requestBody);

                    call.enqueue(new Callback<ApiResponse<User>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                            if (response.isSuccessful()) {
                                ApiResponse<User> apiResponse = response.body();
                                if (apiResponse != null && apiResponse.getCode() == 200) {
                                    // Handle success
                                    User user = apiResponse.getData();
                                    user.saveToPreferences(LoginActivity.this);

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

                            resetLoginButton();
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                            // Handle failure (e.g., no internet connection)
                            binding.tvShowError.setText("Failed to connect. Please check your internet connection.");
                            resetLoginButton();
                        }
                    });
                });
            }
        });


//        binding.tvGoToForgetPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
//                finish();
//            }
//        });

        binding.tvGoToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
//                finish();
            }
        });

    }

    // Helper method to reset the login button state
    private void resetLoginButton() {
        binding.btnLogin.setEnabled(true);
        binding.btnLogin.setText("Sign In");
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
        } else if (item.getItemId() == R.id.action_login_admin) {
            startActivity(new Intent(LoginActivity.this, AdminLoginActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with displaying notifications
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)}
            }
        }
    }

}