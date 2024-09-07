package com.example.eventplanner.fragments;

import static com.example.eventplanner.fragments.HomeFragment.eventList;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventplanner.MenuSelectionActivity;
import com.example.eventplanner.R;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.databinding.FragmentBookVenueBinding;
import com.example.eventplanner.models.Event;
import com.example.eventplanner.models.Venue;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookVenueFragment extends BottomSheetDialogFragment {

    private FragmentBookVenueBinding binding;

    private static final String ARG_VENUE = "selectedVenue";
    private static final String ARG_EVENT_ID = "eventId";
    private int selectedEventId;

    private Venue selectedVenue;
    private String eventId;


    public static BookVenueFragment newInstance(Venue venue, String eventId) {
        BookVenueFragment fragment = new BookVenueFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_VENUE, venue);
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBookVenueBinding.inflate(inflater, container, false);
        LinearLayout view = binding.getRoot();

        if (getArguments() != null) {
            selectedVenue = (Venue) getArguments().getSerializable(ARG_VENUE);
            eventId = getArguments().getString(ARG_EVENT_ID);
        }

        // Set listeners using ViewBinding
        binding.etDate.setOnClickListener(v -> showDatePicker());
        binding.etStartTime.setOnClickListener(v -> showTimePicker(binding.etStartTime));
        binding.etEndTime.setOnClickListener(v -> showTimePicker(binding.etEndTime));

        // Setup Spinner with event names
        List<String> events = new ArrayList<>();
        int selectedEventPosition = 0;

        // Loop through the event list to get names
        for (int i = 0; i < eventList.size(); i++) {
            Event event = eventList.get(i);
            events.add(event.getName()); // Add event name to list

            // If the event ID matches, update the selected event position
            if (String.valueOf(event.getId()).equals(eventId)) {
                selectedEventPosition = i;
            }
        }

        // Create an ArrayAdapter using the custom spinner item layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.event_dropdown_item, events);
        binding.spinnerEvents.setAdapter(adapter);

        // Set the spinner selection to the selected event position
        if (selectedEventPosition >= 0 && selectedEventPosition < events.size()) {
            binding.spinnerEvents.setSelection(selectedEventPosition);
        }

        // Handle selection of spinner item
        binding.spinnerEvents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedEventName = (String) parent.getItemAtPosition(position);
                selectedEventId = eventList.get(position).getId();
                Toast.makeText(getContext(), "Selected Event: " + selectedEventId, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if nothing is selected
            }
        });


        binding.btnBook.setOnClickListener(v -> {
            // Collect the data and send it to the backend
            String date = binding.etDate.getText().toString().trim();
            String startTime = binding.etStartTime.getText().toString().trim();
            String endTime = binding.etEndTime.getText().toString().trim();

            if (!date.isEmpty() && !startTime.isEmpty() && !endTime.isEmpty()) {
                // TODO: Send data to backend via API
                sendBookingDataToBackend();
            } else {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            binding.etDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
        }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void showTimePicker(EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, selectedHour, selectedMinute) -> {
            editText.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
        }, hour, minute, true);

        timePickerDialog.updateTime(hour, minute);
        timePickerDialog.show();
    }

    private void sendBookingDataToBackend() {
// Create the request body as a Map
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("date", binding.etDate.getText().toString());
        requestBody.put("start_time", binding.etStartTime.getText().toString());
        requestBody.put("end_time", binding.etEndTime.getText().toString());
        requestBody.put("phone", binding.etPhoneNumber.getText().toString());
        requestBody.put("event_id", selectedEventId);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<String>> call = apiService.bookVenue(String.valueOf(selectedVenue.getId()), requestBody);
        call.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful()) {
                    // Handle success
                    assert response.body() != null;
                    Toast.makeText(getContext(), "Venue booked successfully", Toast.LENGTH_SHORT).show();

                    // Close the fragment
                    dismiss();

                    Intent menuActivityIntent = new Intent(getContext(), MenuSelectionActivity.class);
                    menuActivityIntent.putExtra("selectedVenue", selectedVenue);
                    startActivity(menuActivityIntent);
                } else {
                    // Handle error
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                // Handle failure
                Log.e("Booking Failure", t.getMessage());
                Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupSpinner(List<String> events) {
        List<String> eventNames = new ArrayList<>();
        for (String event : events) {
            eventNames.add(event);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, eventNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerEvents.setAdapter(adapter);

        binding.spinnerEvents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedEvent = (String) parent.getItemAtPosition(position);
                Toast.makeText(getContext(), "Selected Event: " + selectedEvent, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when no item is selected
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
