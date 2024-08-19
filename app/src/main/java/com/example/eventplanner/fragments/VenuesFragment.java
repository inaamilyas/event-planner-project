package com.example.eventplanner.fragments;

import static com.example.eventplanner.fragments.HomeFragment.venuesList;

import android.os.Bundle;
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

public class VenuesFragment extends Fragment {

    private FragmentVenuesBinding binding;
    int eventId;

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
        VenuesAdapter venuesAdapter = new VenuesAdapter(venuesList, eventId);
        binding.recyclerviewAllVenues.setAdapter(venuesAdapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}
