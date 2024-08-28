package com.example.eventplanner.fragments;

import static com.example.eventplanner.fragments.HomeFragment.eventList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.EventsAdapter;
import com.example.eventplanner.databinding.FragmentEventsBinding;
import com.example.eventplanner.models.Event;

public class EventsFragment extends Fragment {

    private FragmentEventsBinding binding;

    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using view binding
        binding = FragmentEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerViewAllEvents.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        EventsAdapter eventsAdapter = new EventsAdapter(eventList);
        binding.recyclerViewAllEvents.setAdapter(eventsAdapter);

        binding.addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new instance of AddEventFragment
                AddEventFragment addEventFragment = new AddEventFragment();

                // Get the FragmentManager and start a transaction
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the current fragment with AddEventFragment
                fragmentTransaction.replace(R.id.fragment_container, addEventFragment);
                fragmentTransaction.addToBackStack(null); // Optional: Add to back stack to enable back navigation

                // Commit the transaction
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}
