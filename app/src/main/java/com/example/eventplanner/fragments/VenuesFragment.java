package com.example.eventplanner.fragments;

import static com.example.eventplanner.fragments.HomeFragment.venuesList;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventplanner.adapters.VenuesAdapter;
import com.example.eventplanner.databinding.FragmentVenuesBinding;
import com.example.eventplanner.models.Venue;

import java.util.ArrayList;

public class VenuesFragment extends Fragment {

    private FragmentVenuesBinding binding;
    int eventId;
    private ArrayList<Venue> filteredList;
    VenuesAdapter venuesAdapter;

    public VenuesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Retrieve the arguments passed to the fragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            eventId = bundle.getInt("eventId");
        }

        // Inflate the layout using view binding
        binding = FragmentVenuesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerviewAllVenues.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        venuesAdapter = new VenuesAdapter(venuesList, eventId);
        binding.recyclerviewAllVenues.setAdapter(venuesAdapter);

        // Initialize filteredList with all venues initially
        filteredList = new ArrayList<>(venuesList);

        // Set up search functionality
        binding.searchVenueInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filter the list as the user types
                filterVenues(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    // Filter venues based on user input
    private void filterVenues(String query) {
        filteredList.clear(); // Clear the filtered list

        if (query.isEmpty()) {
            // If the query is empty, show all venues
            filteredList.addAll(venuesList);
        } else {
            // Filter the list based on venue name
            for (Venue venue : venuesList) {
                if (venue.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(venue);
                }
            }
        }

        // Update the adapter with the filtered list
        venuesAdapter.updateVenueList(filteredList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}
