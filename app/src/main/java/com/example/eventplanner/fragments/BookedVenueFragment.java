package com.example.eventplanner.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventplanner.LoginActivity;
import com.example.eventplanner.MainActivity;
import com.example.eventplanner.adapters.BookedVenueAdapter;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.databinding.FragmentBookedVenueBinding;
import com.example.eventplanner.models.BookedVenue;
import com.example.eventplanner.models.User;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookedVenueFragment extends Fragment {

    private FragmentBookedVenueBinding binding;
    private ArrayList<BookedVenue> bookedVenuesList = new ArrayList<>();
    private BookedVenueAdapter bookedVenueAdapter;

    public BookedVenueFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout using view binding
        binding = FragmentBookedVenueBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the RecyclerView
        binding.bookedVenueRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        bookedVenueAdapter = new BookedVenueAdapter(bookedVenuesList);
        binding.bookedVenueRecyclerView.setAdapter(bookedVenueAdapter);

        // Load the initial data
        loadBookedVenues();

        // Handle swipe-to-refresh action
        // Refresh the list
        binding.swipeRefreshLayout.setOnRefreshListener(this::refreshBookedVenues);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadBookedVenues() {
        User user = User.getFromPreferences(getContext());
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<ArrayList<BookedVenue>>> call = apiService.getCurrentUserBookings(user.getId());

        call.enqueue(new Callback<ApiResponse<ArrayList<BookedVenue>>>() {
            @Override
            public void onResponse(Call<ApiResponse<ArrayList<BookedVenue>>> call, Response<ApiResponse<ArrayList<BookedVenue>>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Booked venues data retrieved successfully", Toast.LENGTH_SHORT).show();
                    ApiResponse<ArrayList<BookedVenue>> apiResponse = response.body();

                    if (apiResponse != null && apiResponse.getCode() == 200) {
                        // Clear the list and add the new data
                        bookedVenuesList.clear();
                        if (apiResponse.getData() != null) {
                            bookedVenuesList.addAll(apiResponse.getData());
                        }
                        // Notify the adapter of the data change
                        bookedVenueAdapter.notifyDataSetChanged();
                    } else {
                        // Show the error message from the API response
                        assert apiResponse != null;
                        Toast.makeText(getContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        // Parse the error body
                        Gson gson = new Gson();
                        ApiResponse<?> apiErrorResponse = gson.fromJson(response.errorBody().string(), ApiResponse.class);
                        if (apiErrorResponse != null) {
                            Toast.makeText(getContext(), apiErrorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Something went wrong while getting data", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ArrayList<BookedVenue>>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load booked venues", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @SuppressLint("NotifyDataSetChanged")
    private void refreshBookedVenues() {
        // Simulate a data refresh (you can call an API here if needed)
        bookedVenuesList.clear();
        loadBookedVenues();

        // Notify the adapter and stop the refreshing animation
        bookedVenueAdapter.notifyDataSetChanged();
        binding.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
