package com.example.eventplanner.Admin.adapter;

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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.Admin.AdminVenueDetailsActivity;
import com.example.eventplanner.R;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.config.AppConfig;
import com.example.eventplanner.models.Venue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminVenueAdapter extends RecyclerView.Adapter<AdminVenueAdapter.ViewHolder> {
    private ArrayList<Venue> venuesList;

    public AdminVenueAdapter(ArrayList<Venue> venuesList) {
        this.venuesList = venuesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.admin_venue_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Venue venue = venuesList.get(position);
        holder.setView(venue);
    }

    @Override
    public int getItemCount() {
        return venuesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView venueName;
        TextView venueAddress;
        //        TextView event;
        ImageView venueImage;

        public ViewHolder(View view) {
            super(view);
            this.venueName = (TextView) view.findViewById(R.id.venue_list_item_title);

            this.venueAddress = (TextView) view.findViewById(R.id.venue_list_item_address);

            this.venueImage = (ImageView) view.findViewById(R.id.venue_list_item_image);


            // Define click listener for the ViewHolder's View
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "Clicked on item", Toast.LENGTH_SHORT).show();
                    int position = getAdapterPosition();
                    Venue selectedVenue = venuesList.get(position);

                    Intent intent = new Intent(view.getContext(), AdminVenueDetailsActivity.class);
                    intent.putExtra("selectedVenue", selectedVenue);
                    view.getContext().startActivity(intent);
                }
            });

//            edit venue details
            view.findViewById(R.id.venue_list_item_edit_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Venue selectedVenue = venuesList.get(position);
                    Toast.makeText(view.getContext(), "Approving....", Toast.LENGTH_SHORT).show();

                    String title = "Approve Venue";
                    String message = "Are you sure you want to approve this venue?";
                    new AlertDialog.Builder(view.getContext()).setTitle(title).setMessage(message).setPositiveButton("Confirm", (dialog, which) -> {

                        Map<String, Object> requestBody = new HashMap<>();
                        requestBody.put("venue_id", selectedVenue.getId());
                        requestBody.put("status", 1);

                        ApiService apiService = ApiClient.getClient().create(ApiService.class);
                        Call<ApiResponse> call = apiService.changeVenueStatus(requestBody);
                        call.enqueue(new Callback<ApiResponse>() {
                            @Override
                            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(view.getContext(), "Venue approved successfully", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(view.getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiResponse> call, Throwable t) {
                                // Handle failure
                                Log.e("Booking Failure", t.getMessage());
                                Toast.makeText(view.getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).setNegativeButton("Cancel", (dialog, which) -> {
                        // User canceled the deletion
                        dialog.dismiss();
                    }).show();
                }
            });

            view.findViewById(R.id.venue_list_item_view_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(view.getContext(), "Clicked on item", Toast.LENGTH_SHORT).show();
                    int position = getAdapterPosition();
                    Venue selectedVenue = venuesList.get(position);

                    Intent intent = new Intent(view.getContext(), AdminVenueDetailsActivity.class);
                    intent.putExtra("selectedVenue", selectedVenue);
                    intent.putExtra("position", position);
                    view.getContext().startActivity(intent);
                }
            });
        }

        public void setView(Venue venue) {
            this.venueName.setText(venue.getName());
            this.venueAddress.setText(venue.getAddress());
            Glide.with(this.venueImage.getContext()).load(AppConfig.SERVER_URL + venue.getPicture()).placeholder(R.drawable.enent_image).into(this.venueImage);
            this.venueImage.setImageResource(R.drawable.enent_image);
        }
    }

    private void changeVenueStatus(int venueId, int status) {


    }

}
