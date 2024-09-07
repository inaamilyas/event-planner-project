package com.example.eventplanner;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventplanner.adapters.MenuAdapter;
import com.example.eventplanner.databinding.ActivityBookVenueBinding;
import com.example.eventplanner.models.MenuItem;

import java.util.ArrayList;

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

        menuList.add(new MenuItem(1, "Pizza", "No description", 12, "oijfdso;ijfoasj"));
        menuList.add(new MenuItem(2, "Pizza", "No description", 12, "oijfdso;ijfoasj"));
        menuList.add(new MenuItem(3, "Pizza", "No description", 12, "oijfdso;ijfoasj"));
        menuList.add(new MenuItem(4, "Pizza", "No description", 12, "oijfdso;ijfoasj"));
        menuList.add(new MenuItem(5, "Pizza", "No description", 12, "oijfdso;ijfoasj"));
        menuList.add(new MenuItem(6, "Pizza", "No description", 12, "oijfdso;ijfoasj"));

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
                Toast.makeText(MenuSelectionActivity.this, "Ordered your menu", Toast.LENGTH_SHORT).show();
            }
        });


    }
}