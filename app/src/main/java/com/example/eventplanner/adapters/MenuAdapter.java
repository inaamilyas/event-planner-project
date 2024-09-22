package com.example.eventplanner.adapters;

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
import java.util.HashMap;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    ArrayList<MenuItem> menuItems;
    private static OnAddToCartClickListener listener;
    public static HashMap<Integer, Integer> quantityMap = new HashMap<>();
//    MenuItem selectedMenuItem;

    // Interface for handling add-to-cart clicks
    public interface OnAddToCartClickListener {
        void onAddToCart(double cost);

        void onRemoveToCart(double cost);
    }

    // Constructor to set the listener
    public MenuAdapter(ArrayList<MenuItem> menuItems, OnAddToCartClickListener listener) {
        this.menuItems = menuItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_user, parent, false);

        return new MenuAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.ViewHolder holder, int position) {
        MenuItem menuItem = menuItems.get(position);
        holder.setView(menuItem);
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        AppCompatButton addToCartBtn;
        AppCompatButton removeToCartBtn;
        TextView itemPrice;
        TextView quantity;
        TextView increment;
        TextView decrement;
        ImageView imageView;
        double price;
        MenuItem selectedMenuItem;

        public ViewHolder(View view) {
            super(view);
            itemName = view.findViewById(R.id.menu_name);
            addToCartBtn = view.findViewById(R.id.add_to_cart);
            removeToCartBtn = view.findViewById(R.id.remove_to_cart);
            itemPrice = view.findViewById(R.id.menu_price);
            quantity = view.findViewById(R.id.tv_quantity);
            imageView = view.findViewById(R.id.menu_image);
            increment = view.findViewById(R.id.increment);
            decrement = view.findViewById(R.id.decrement);

            increment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantityValue = Integer.parseInt(quantity.getText().toString());
                    quantityValue++;
                    quantity.setText(String.valueOf(quantityValue));
                }
            });

            decrement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantityValue = Integer.parseInt(quantity.getText().toString());
                    quantityValue--;
                    if (quantityValue < 0) {
                        quantityValue = 0;
                    }
                    quantity.setText(String.valueOf(quantityValue));
                }
            });

            addToCartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantityValue = Integer.parseInt(quantity.getText().toString());
                    double cost = quantityValue * price;
                    Toast.makeText(v.getContext(), "Added to cart", Toast.LENGTH_SHORT).show();

                    // Check if the item already exists in the cart
                    if (quantityMap.containsKey(selectedMenuItem.getId())) {
                        // Update the quantity by adding the new quantity
                        int existingQuantity = quantityMap.get(selectedMenuItem.getId());
                        quantityMap.put(selectedMenuItem.getId(), existingQuantity + quantityValue);
                    } else {
                        // Add new item with the quantity
                        quantityMap.put(selectedMenuItem.getId(), quantityValue);
                    }

                    // Notify the listener with the cost
                    if (listener != null) {
                        listener.onAddToCart(cost); // Send the cost to the activity
                    }
                }
            });

            removeToCartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantityValue = Integer.parseInt(quantity.getText().toString());
                    double cost = quantityValue * price;

                    // Check if the item exists in the cart
                    if (quantityMap.containsKey(selectedMenuItem.getId())) {
                        int existingQuantity = quantityMap.get(selectedMenuItem.getId());

                        // Reduce the quantity
                        int newQuantity = existingQuantity - quantityValue;

                        // If the new quantity is greater than zero, update it
                        if (newQuantity > 0) {
                            quantityMap.put(selectedMenuItem.getId(), newQuantity);
                        } else {
                            // If quantity becomes zero or less, remove the item from the cart
                            quantityMap.remove(selectedMenuItem.getId());
                        }

                        Toast.makeText(v.getContext(), "Removed from cart", Toast.LENGTH_SHORT).show();

                        // Notify the listener with the cost
                        if (listener != null) {
                            listener.onRemoveToCart(cost); // Send the cost to the activity
                        }
                    }
                }
            });

        }

        @SuppressLint("SetTextI18n")
        public void setView(MenuItem menuItem) {
            this.itemName.setText(menuItem.getName());
//            this.description.setText(menuItem.getDescription());
            this.itemPrice.setText(menuItem.getPrice() + " PKR");
            this.price = menuItem.getPrice();
            this.selectedMenuItem = menuItem;
            String imageUrl = (AppConfig.SERVER_URL + menuItem.getImageUrl()).trim();

            Glide.with(this.imageView.getContext()).load(imageUrl).placeholder(R.drawable.enent_image).into(this.imageView);
        }
    }
}
