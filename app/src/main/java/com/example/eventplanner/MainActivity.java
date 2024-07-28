package com.example.eventplanner;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.adapters.EventsAdapter;
import com.example.eventplanner.databinding.ActivityMainBinding;
import com.example.eventplanner.models.Event;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private RecyclerView eventsRecyclerHome;
    private Event[] eventsArr = {
            new Event("Event 1", "Date 1", "Address 1","https://media.istockphoto.com/id/974238866/photo/audience-listens-to-the-lecturer-at-the-conference.jpg?s=612x612&w=0&k=20&c=p_BQCJWRQQtZYnQlOtZMzTjeB_csic8OofTCAKLwT0M="),
            new Event("Event 1", "Date 1", "Address 1","https://media.istockphoto.com/id/974238866/photo/audience-listens-to-the-lecturer-at-the-conference.jpg?s=612x612&w=0&k=20&c=p_BQCJWRQQtZYnQlOtZMzTjeB_csic8OofTCAKLwT0M="),
            new Event("Event 1", "Date 1", "Address 1","https://media.istockphoto.com/id/974238866/photo/audience-listens-to-the-lecturer-at-the-conference.jpg?s=612x612&w=0&k=20&c=p_BQCJWRQQtZYnQlOtZMzTjeB_csic8OofTCAKLwT0M="),
            new Event("Event 1", "Date 1", "Address 1","https://media.istockphoto.com/id/974238866/photo/audience-listens-to-the-lecturer-at-the-conference.jpg?s=612x612&w=0&k=20&c=p_BQCJWRQQtZYnQlOtZMzTjeB_csic8OofTCAKLwT0M="),
            new Event("Event 1", "Date 1", "Address 1","https://media.istockphoto.com/id/974238866/photo/audience-listens-to-the-lecturer-at-the-conference.jpg?s=612x612&w=0&k=20&c=p_BQCJWRQQtZYnQlOtZMzTjeB_csic8OofTCAKLwT0M="),

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.eventsRecyclerHome.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        EventsAdapter eventsAdapter = new EventsAdapter(eventsArr);
        binding.eventsRecyclerHome.setAdapter(eventsAdapter);

        binding.venuesRecyclerHome.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.venuesRecyclerHome.setAdapter(eventsAdapter);

    }
}