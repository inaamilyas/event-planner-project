package com.example.eventplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private ArrayList<Venue> venuesList = new ArrayList<>();

    public VenuesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout using view binding
        binding = FragmentVenuesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerviewAllVenues.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        VenuesAdapter venuesAdapter = new VenuesAdapter(venuesList);
        binding.recyclerviewAllVenues.setAdapter(venuesAdapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}
