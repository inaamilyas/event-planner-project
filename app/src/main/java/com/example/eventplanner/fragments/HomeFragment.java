package com.example.eventplanner.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.EventsAdapter;
import com.example.eventplanner.adapters.VenuesAdapter;
import com.example.eventplanner.databinding.FragmentHomeBinding;
import com.example.eventplanner.models.Event;
import com.example.eventplanner.models.Venue;
import com.example.eventplanner.models.WeatherResponse;
import com.example.eventplanner.network.WeatherApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String WEATHER_API_KEY = "9f28422c6a2644958a162539241108";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private FragmentHomeBinding binding;
    private FusedLocationProviderClient fusedLocationClient;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using view binding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private Event[] eventsArr = {
            new Event("Event 1", "Date 1", "Address 1", "https://media.istockphoto.com/id/974238866/photo/audience-listens-to-the-lecturer-at-the-conference.jpg?s=612x612&w=0&k=20&c=p_BQCJWRQQtZYnQlOtZMzTjeB_csic8OofTCAKLwT0M="),
            new Event("Event 1", "Date 1", "Address 1", "https://media.istockphoto.com/id/974238866/photo/audience-listens-to-the-lecturer-at-the-conference.jpg?s=612x612&w=0&k=20&c=p_BQCJWRQQtZYnQlOtZMzTjeB_csic8OofTCAKLwT0M="),
            new Event("Event 1", "Date 1", "Address 1", "https://media.istockphoto.com/id/974238866/photo/audience-listens-to-the-lecturer-at-the-conference.jpg?s=612x612&w=0&k=20&c=p_BQCJWRQQtZYnQlOtZMzTjeB_csic8OofTCAKLwT0M="),
            new Event("Event 1", "Date 1", "Address 1", "https://media.istockphoto.com/id/974238866/photo/audience-listens-to-the-lecturer-at-the-conference.jpg?s=612x612&w=0&k=20&c=p_BQCJWRQQtZYnQlOtZMzTjeB_csic8OofTCAKLwT0M="),
            new Event("Event 1", "Date 1", "Address 1", "https://media.istockphoto.com/id/974238866/photo/audience-listens-to-the-lecturer-at-the-conference.jpg?s=612x612&w=0&k=20&c=p_BQCJWRQQtZYnQlOtZMzTjeB_csic8OofTCAKLwT0M="),
            new Event("Event 1", "Date 1", "Address 1", "https://media.istockphoto.com/id/974238866/photo/audience-listens-to-the-lecturer-at-the-conference.jpg?s=612x612&w=0&k=20&c=p_BQCJWRQQtZYnQlOtZMzTjeB_csic8OofTCAKLwT0M="),
            new Event("Event 1", "Date 1", "Address 1", "https://media.istockphoto.com/id/974238866/photo/audience-listens-to-the-lecturer-at-the-conference.jpg?s=612x612&w=0&k=20&c=p_BQCJWRQQtZYnQlOtZMzTjeB_csic8OofTCAKLwT0M="),
            // Add more events if needed
    };

    private Venue[] venuesArr = {
            new Venue("Event 1", "Address 1", "https://media.istockphoto.com/id/974238866/photo/audience-listens-to-the-lecturer-at-the-conference.jpg?s=612x612&w=0&k=20&c=p_BQCJWRQQtZYnQlOtZMzTjeB_csic8OofTCAKLwT0M="),
            new Venue("Event 1", "Address 1", "https://media.istockphoto.com/id/974238866/photo/audience-listens-to-the-lecturer-at-the-conference.jpg?s=612x612&w=0&k=20&c=p_BQCJWRQQtZYnQlOtZMzTjeB_csic8OofTCAKLwT0M="),
            new Venue("Event 1", "Address 1", "https://media.istockphoto.com/id/974238866/photo/audience-listens-to-the-lecturer-at-the-conference.jpg?s=612x612&w=0&k=20&c=p_BQCJWRQQtZYnQlOtZMzTjeB_csic8OofTCAKLwT0M="),
            new Venue("Event 1", "Address 1", "https://media.istockphoto.com/id/974238866/photo/audience-listens-to-the-lecturer-at-the-conference.jpg?s=612x612&w=0&k=20&c=p_BQCJWRQQtZYnQlOtZMzTjeB_csic8OofTCAKLwT0M="),
            new Venue("Event 1", "Address 1", "https://media.istockphoto.com/id/974238866/photo/audience-listens-to-the-lecturer-at-the-conference.jpg?s=612x612&w=0&k=20&c=p_BQCJWRQQtZYnQlOtZMzTjeB_csic8OofTCAKLwT0M="),
            new Venue("Event 1", "Address 1", "https://media.istockphoto.com/id/974238866/photo/audience-listens-to-the-lecturer-at-the-conference.jpg?s=612x612&w=0&k=20&c=p_BQCJWRQQtZYnQlOtZMzTjeB_csic8OofTCAKLwT0M="),
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Fetch weather data
        fetchWeatherData("33.63911511690343", "73.07839473857334"); // Replace with the user's location or use GPS to get location

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }


        // Initialize RecyclerViews
        binding.eventsRecyclerHome.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        EventsAdapter eventsAdapter = new EventsAdapter(eventsArr);
        binding.eventsRecyclerHome.setAdapter(eventsAdapter);
        binding.venuesRecyclerHome.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        VenuesAdapter venuesAdapter = new VenuesAdapter(venuesArr);
        binding.venuesRecyclerHome.setAdapter(venuesAdapter);

        // Set click listeners
//        binding.seeAllEvents.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AllEventsActivity.class)));
//        binding.seeAllVenues.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AllVenuesActivity.class)));

    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // Got last known location. In some rare situations this can be null.
                    String lat = String.valueOf(location.getLatitude());
                    String lon = String.valueOf(location.getLongitude());
                    fetchWeatherData(lat, lon);
                }
            }
        });
    }

    private void fetchWeatherData(String lat, String log) {
        String location = lat + "," + log;
        WeatherApiClient.getWeatherService().getWeather(WEATHER_API_KEY, location, 6)
                .enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            updateUIWithWeatherData(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherResponse> call, Throwable t) {

                        // Handle failure
                    }
                });
    }

    private void updateUIWithWeatherData(WeatherResponse weatherResponse) {
        // Current weather
        binding.temperature.setText(String.format("%s°C", weatherResponse.getCurrent().getTempC()));
        binding.weatherDescription.setText(weatherResponse.getCurrent().getCondition().getText());
        binding.weatherDetails.setText(String.format("Humidity: %s%%\nWind: %s km/h",
                weatherResponse.getCurrent().getHumidity(),
                weatherResponse.getCurrent().getWindKph()));

        Glide.with(this)
                .load("https:" + weatherResponse.getCurrent().getCondition().getIcon())
                .into(binding.weatherIcon);


//         Daily forecast - dynamically add forecast items
        binding.dailyForecastContainer.removeAllViews();
        for (WeatherResponse.Forecast.ForecastDay forecastDay : weatherResponse.getForecast().getForecastDays()) {
            addDailyForecast(forecastDay);
        }
    }

    private void addDailyForecast(WeatherResponse.Forecast.ForecastDay forecastDay) {
        View dailyForecastView = LayoutInflater.from(getContext()).inflate(R.layout.item_daily_forecast, binding.dailyForecastContainer, false);

        TextView dayOfWeek = dailyForecastView.findViewById(R.id.day_of_week);
        ImageView dailyWeatherIcon = dailyForecastView.findViewById(R.id.daily_weather_icon);
        TextView dailyTemperature = dailyForecastView.findViewById(R.id.daily_temperature);

        dayOfWeek.setText(forecastDay.getDate());
        Glide.with(this)
                .load("https:" + forecastDay.getDay().getCondition().getIcon())
                .into(dailyWeatherIcon);
        dailyTemperature.setText(String.format("%s°C / %s°C", forecastDay.getDay().getMaxTempC(), forecastDay.getDay().getMinTempC()));

        binding.dailyForecastContainer.addView(dailyForecastView);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                getCurrentLocation();
            } else {
                // Permission denied
                Log.d("inaamilyas5", "Location permission denied");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}
