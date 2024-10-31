package com.example.eventplanner.VenueManager.adapter;


import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.models.BookedVenue;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookedVenueAdapter extends RecyclerView.Adapter<BookedVenueAdapter.ViewHolder> {

    ArrayList<BookedVenue> bookedVenues;

    public BookedVenueAdapter(ArrayList<BookedVenue> bookedVenues) {
        this.bookedVenues = bookedVenues;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.manager_order_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookedVenue bookedVenue = bookedVenues.get(position);
        holder.setView(bookedVenue);
    }

    public int getItemCount() {
        return bookedVenues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventName;
        TextView venueName, date, time;
        LinearLayout venueBookedContainer;
        Button cancelButton, acceptButton;

        public ViewHolder(View view) {
            super(view);
            this.eventName = (TextView) view.findViewById(R.id.booked_event_title);
            this.venueName = (TextView) view.findViewById(R.id.booked_venue_title);
            this.date = (TextView) view.findViewById(R.id.booked_venue_date);
            this.time = (TextView) view.findViewById(R.id.booked_venue_time);
            this.acceptButton = (Button) view.findViewById(R.id.btn_approve_order);
            this.cancelButton = (Button) view.findViewById(R.id.btn_cancel_order);
            this.venueBookedContainer = (LinearLayout) view.findViewById(R.id.booked_venue_view_container);

            this.acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    BookedVenue bookedVenue = bookedVenues.get(position);
                    ApiService apiService = ApiClient.getClient().create(ApiService.class);
                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("booking_id", bookedVenue.getId());
                    requestBody.put("status", 1);
                    Call<ApiResponse<String>> call = apiService.changeBookingOrderStatus(requestBody);

                    call.enqueue(new Callback<ApiResponse<String>>() {

                        @Override
                        public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                            if (response.isSuccessful()) {
                                ApiResponse<String> apiResponse = response.body();
                                if (apiResponse != null && apiResponse.getCode() == 200) {
                                    // Handle success
                                    Toast.makeText(itemView.getContext(), "Order accepted successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Handle error based on API response
                                    assert apiResponse != null;
                                    Toast.makeText(itemView.getContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                try {
                                    // Parse the error body to get the API response
                                    Gson gson = new Gson();
                                    ApiResponse<?> apiErrorResponse = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                                    if (apiErrorResponse != null) {
                                        Toast.makeText(itemView.getContext(), apiErrorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(itemView.getContext(), "An unexpected error occurred.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                            // Handle failure (e.g., no internet connection)
                            Toast.makeText(itemView.getContext(), "Failed to connect. Please check your internet connection.", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });

            this.cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    BookedVenue bookedVenue = bookedVenues.get(position);
                    ApiService apiService = ApiClient.getClient().create(ApiService.class);
                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("booking_id", bookedVenue.getId());
                    requestBody.put("status", 2);
                    Call<ApiResponse<String>> call = apiService.changeBookingOrderStatus(requestBody);

                    call.enqueue(new Callback<ApiResponse<String>>() {

                        @Override
                        public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                            if (response.isSuccessful()) {
                                ApiResponse<String> apiResponse = response.body();
                                if (apiResponse != null && apiResponse.getCode() == 200) {
                                    // Handle success
                                    Toast.makeText(itemView.getContext(), "Order cancled successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Handle error based on API response
                                    assert apiResponse != null;
                                    Toast.makeText(itemView.getContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                try {
                                    // Parse the error body to get the API response
                                    Gson gson = new Gson();
                                    ApiResponse<?> apiErrorResponse = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                                    if (apiErrorResponse != null) {
                                        Toast.makeText(itemView.getContext(), apiErrorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(itemView.getContext(), "An unexpected error occurred.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                            // Handle failure (e.g., no internet connection)
                            Toast.makeText(itemView.getContext(), "Failed to connect. Please check your internet connection.", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        }

        @SuppressLint("SetTextI18n")
        public void setView(BookedVenue bookedVenue) {
            this.eventName.setText(bookedVenue.getEvent().getName());
            this.venueName.setText(bookedVenue.getVenue().getName());
            this.date.setText(bookedVenue.getBookingDate().substring(0, 10));
            this.time.setText(bookedVenue.getStartTime() + " - " + bookedVenue.getEndTime());

            if (bookedVenue.getStatus() == 1) {
                // Approved (Blue Shade)
                venueBookedContainer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.blue_shade)));
            } else if (bookedVenue.getStatus() == 0) {
                // Pending (Gray Shade)
                venueBookedContainer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.gray_shade)));
            } else if (bookedVenue.getStatus() == 2) {
                // Canceled (Red Shade)
                venueBookedContainer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.red_shade)));
            }
        }
    }
}
