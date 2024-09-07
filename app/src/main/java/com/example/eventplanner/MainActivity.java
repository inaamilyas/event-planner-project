package com.example.eventplanner;

import static com.example.eventplanner.fragments.HomeFragment.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.eventplanner.config.AppConfig;
import com.example.eventplanner.databinding.ActivityMainBinding;
import com.example.eventplanner.fragments.AddEventFragment;
import com.example.eventplanner.fragments.EventsFragment;
import com.example.eventplanner.fragments.HomeFragment;
import com.example.eventplanner.fragments.ProfileFragment;
import com.example.eventplanner.fragments.SettingFragment;
import com.example.eventplanner.fragments.VenuesFragment;
import com.example.eventplanner.models.User;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityMainBinding binding;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView profileImageView;
    private TextView usernameTextView;
    private ImageView buttonCloseDrawer;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbar);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
//        buttonCloseDrawer = findViewById(R.id.close_menu);

        // Set default fragment
        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.nav_home);
        }

        // Initialize header view
        View headerView = navigationView.getHeaderView(0);
        profileImageView = headerView.findViewById(R.id.imageView);
        usernameTextView = headerView.findViewById(R.id.username);
        buttonCloseDrawer = headerView.findViewById(R.id.close_menu);



        User user = User.getFromPreferences(this);
//        Toast.makeText(this, user.getProfilePic(), Toast.LENGTH_SHORT).show();
        // Set user information
        profileImageView.setImageResource(R.drawable.event_image_1);
        usernameTextView.setText(user.getName());

        // Set button click listener to open/close drawer
        binding.buttonOpenDrawer.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Set click listener for the close drawer button
        buttonCloseDrawer.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));

        // Set Navigation Item Selected Listener
        navigationView.setNavigationItemSelectedListener(this);

        binding.buttonAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AddEventFragment fragment = new AddEventFragment();
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.fragment_container, fragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();

                Intent intent = new Intent(MainActivity.this, MenuSelectionActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment selectedFragment = null;
        if (id == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (id == R.id.nav_events) {
            selectedFragment = new EventsFragment();
        } else if (id == R.id.nav_venues) {
            selectedFragment = new VenuesFragment();
        } else if (id == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
        } else if (id == R.id.nav_logout) {
            // Handle logout here
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
            // Remove specific user data from SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences(AppConfig.SHARED_PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("user");
            editor.apply();

            // redirect the user to the login screen
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close the current activity
        }

        if (selectedFragment != null) {
            replaceFragment(selectedFragment);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
