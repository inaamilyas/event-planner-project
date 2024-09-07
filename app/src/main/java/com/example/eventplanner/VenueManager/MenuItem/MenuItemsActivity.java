package com.example.eventplanner.VenueManager.MenuItem;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventplanner.VenueManager.adapter.MenuItemAdapter;
import com.example.eventplanner.databinding.ActivityMenuItemsBinding;
import com.example.eventplanner.models.MenuItem;

import java.util.ArrayList;

public class MenuItemsActivity extends AppCompatActivity {
    ActivityMenuItemsBinding binding;
    private ArrayList<MenuItem> menuItemsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        menuItemsList.add(new MenuItem(1, "Pizza", "No description", 12, "oijfdso;ijfoasj"));
        menuItemsList.add(new MenuItem(2, "Pizza", "No description", 12, "oijfdso;ijfoasj"));
        menuItemsList.add(new MenuItem(3, "Pizza", "No description", 12, "oijfdso;ijfoasj"));
        menuItemsList.add(new MenuItem(4, "Pizza", "No description", 12, "oijfdso;ijfoasj"));
        menuItemsList.add(new MenuItem(5, "Pizza", "No description", 12, "oijfdso;ijfoasj"));
        menuItemsList.add(new MenuItem(6, "Pizza", "No description", 12, "oijfdso;ijfoasj"));
        // Initialize RecyclerView and Adapter
        binding.menuItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        MenuItemAdapter menuItemAdapter = new MenuItemAdapter(menuItemsList);
        binding.menuItemsRecyclerView.setAdapter(menuItemAdapter);

    }
}