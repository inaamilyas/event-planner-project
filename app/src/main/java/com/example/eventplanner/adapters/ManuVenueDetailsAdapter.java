package com.example.eventplanner.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.config.AppConfig;
import com.example.eventplanner.models.MenuItem;

import java.util.ArrayList;

public class ManuVenueDetailsAdapter extends RecyclerView.Adapter<ManuVenueDetailsAdapter.ViewHolder> {
    ArrayList<MenuItem> menuItems;

    public ManuVenueDetailsAdapter(ArrayList<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    @NonNull
    @Override
    public ManuVenueDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_venue_details, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItem menuItem = menuItems.get(position);
        holder.setView(menuItem);
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView itemPrice;
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            itemName = view.findViewById(R.id.menu_name);
            itemPrice = view.findViewById(R.id.menu_price);
            imageView = view.findViewById(R.id.menu_image);
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
