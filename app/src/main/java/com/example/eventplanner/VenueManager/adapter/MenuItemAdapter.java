package com.example.eventplanner.VenueManager.adapter;

import static com.example.eventplanner.VenueManager.MenuItem.AddMenuActivity.menuItemsRecyclerView;
import static com.example.eventplanner.VenueManager.MenuItem.AddMenuActivity.setRecyclerViewHeight;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.VenueManager.DashboardVenueManagerActivity;
import com.example.eventplanner.VenueManager.EditMenuItemDialogFragment;
import com.example.eventplanner.VenueManager.OnMenuItemUpdatedListener;
import com.example.eventplanner.VenueManager.VenueManagerVenDetailsActivity;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.config.AppConfig;
import com.example.eventplanner.models.MenuItem;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.ViewHolder> {
    ArrayList<MenuItem> menuItems;

    // Constructor to set the listener
    public MenuItemAdapter(ArrayList<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    @NonNull
    @Override
    public MenuItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_manager, parent, false);

        return new MenuItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemAdapter.ViewHolder holder, int position) {
        MenuItem menuItem = menuItems.get(position);
        holder.setView(menuItem);
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        AppCompatButton editBtn;
        AppCompatButton deleteBtn;
        TextView itemPrice;
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            itemName = view.findViewById(R.id.menu_name);
            editBtn = view.findViewById(R.id.add_to_cart);
            deleteBtn = view.findViewById(R.id.remove_to_cart);
            itemPrice = view.findViewById(R.id.menu_price);
            imageView = view.findViewById(R.id.menu_image);

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    MenuItem menuItem = menuItems.get(position);
                    EditMenuItemDialogFragment dialog = EditMenuItemDialogFragment.newInstance(menuItem);

                    // Set the listener to handle the update
                    dialog.setMenuItemUpdateListener(new OnMenuItemUpdatedListener() {
                        @Override
                        public void onMenuItemUpdated(MenuItem updatedMenuItem, int position) {
                            // Update the list and notify the adapter
                            menuItems.set(position, updatedMenuItem);
                            notifyItemChanged(position);  // Notify the adapter of the change
                        }
                    }, position);

                    // Show the dialog
                    dialog.show(((AppCompatActivity) v.getContext()).getSupportFragmentManager(), "EditMenuItemDialog");
                }
            });



            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    MenuItem menuItem = menuItems.get(position);


                    // Show a confirmation dialog to the user
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Delete Venue")
                            .setMessage("Are you sure you want to delete this item?")
                            .setPositiveButton("Confirm", (dialog, which) -> {
                                // User confirmed deletion, proceed with API call
                                ApiService apiService = ApiClient.getClient().create(ApiService.class);

                                apiService.deleteMenuItem(String.valueOf(menuItem.getId())).enqueue(new Callback<ApiResponse>() {
                                    @Override
                                    public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            Toast.makeText(view.getContext(), "menu item deleted successfully", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();

                                            menuItems.remove(position);
                                            notifyItemRemoved(position);
                                            setRecyclerViewHeight(menuItemsRecyclerView);

                                        } else {
                                            Toast.makeText(view.getContext(), "Failed to delete venue", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                                        Toast.makeText(view.getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                // User canceled the deletion
                                dialog.dismiss();
                            })
                            .show();

                }
            });

        }

        @SuppressLint("SetTextI18n")
        public void setView(MenuItem menuItem) {
            this.itemName.setText(menuItem.getName());
            this.itemPrice.setText(menuItem.getPrice() + " PKR");
            String imageUrl = (AppConfig.SERVER_URL + menuItem.getImageUrl()).trim();
            Glide.with(this.imageView.getContext()).load(imageUrl).placeholder(R.drawable.enent_image).into(this.imageView);
        }
    }
}
