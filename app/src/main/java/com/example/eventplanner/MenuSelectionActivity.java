package com.example.eventplanner;

import static com.example.eventplanner.adapters.MenuAdapter.quantityMap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventplanner.adapters.MenuAdapter;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.databinding.ActivityBookVenueBinding;
import com.example.eventplanner.models.MenuItem;
import com.example.eventplanner.models.Venue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuSelectionActivity extends AppCompatActivity {
    private ActivityBookVenueBinding binding;
    private TextView tvTotalCost;
    private static ArrayList<MenuItem> menuList = new ArrayList<>();
    private double totalCost = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookVenueBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tvTotalCost = binding.totalCost;

        // Retrieve the intent that started this activity
        Intent intent = getIntent();
        Venue selectedVenue = (Venue) intent.getSerializableExtra("selectedVenue");
        assert selectedVenue != null;
        menuList.addAll(selectedVenue.getFoodMenuItems());
        int bookingId = intent.getIntExtra("bookingId", -1);
        Toast.makeText(this, "bookingId" + bookingId, Toast.LENGTH_SHORT).show();



        // Initialize the adapter and set the listener for add-to-cart events
        binding.menuRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        MenuAdapter adapter = new MenuAdapter(menuList, new MenuAdapter.OnAddToCartClickListener() {
            @Override
            public void onAddToCart(double cost) {
                totalCost += cost; // Add the cost to the total cost
                tvTotalCost.setText(totalCost + " PKR"); // Update the total cost text
            }

            public void onRemoveToCart(double cost) {
                totalCost -= cost;
                if (totalCost < 0) totalCost = 0;
                tvTotalCost.setText(totalCost + " PKR");
            }
        });
        binding.menuRecyclerView.setAdapter(adapter);

        binding.orderConfirmButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("booking_id", bookingId);
                requestBody.put("menu_item_ids", quantityMap);

                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                Call<ApiResponse<String>> call = apiService.bookMenuItems(requestBody);
                call.enqueue(new Callback<ApiResponse<String>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MenuSelectionActivity.this, "Ordered your menu", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(MenuSelectionActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                        // Handle failure
                        Log.e("Booking Failure", t.getMessage());
                        Toast.makeText(MenuSelectionActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }
}