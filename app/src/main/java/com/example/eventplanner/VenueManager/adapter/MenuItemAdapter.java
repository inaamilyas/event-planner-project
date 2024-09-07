package com.example.eventplanner.VenueManager.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.config.AppConfig;
import com.example.eventplanner.models.MenuItem;

import java.util.ArrayList;

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
                    Toast.makeText(v.getContext(), "edit", Toast.LENGTH_SHORT).show();
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "delete", Toast.LENGTH_SHORT).show();
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
