package com.example.eventplanner.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.EventsAdapter;
import com.example.eventplanner.adapters.VenuesAdapter;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponseArray;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.config.AppConfig;
import com.example.eventplanner.databinding.FragmentHomeBinding;
import com.example.eventplanner.models.Event;
import com.example.eventplanner.models.Venue;
import com.example.eventplanner.models.WeatherResponse;
import com.example.eventplanner.network.WeatherApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private long LATITUDE, LONGITUDE;
    private VenuesAdapter venuesAdapter;
    private FragmentHomeBinding binding;
    private FusedLocationProviderClient fusedLocationClient;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout using view binding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private Event[] eventsArr = {};

    private ArrayList<Venue> venuesList = new ArrayList<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }


        binding.venuesRecyclerHome.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        venuesAdapter = new VenuesAdapter(venuesList);
        binding.venuesRecyclerHome.setAdapter(venuesAdapter);

        // Initialize RecyclerViews
        binding.eventsRecyclerHome.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        EventsAdapter eventsAdapter = new EventsAdapter(eventsArr);
        binding.eventsRecyclerHome.setAdapter(eventsAdapter);

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
                    ApiService apiService = ApiClient.getClient().create(ApiService.class);
                    // Fetch data from API
                    fetchVenues(apiService, lat, lon);
                }
            }
        });
    }

    private void fetchWeatherData(String lat, String log) {
        String location = lat + "," + log;
        WeatherApiClient.getWeatherService().getWeather(AppConfig.WEATHER_API_KEY, location, 6).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUIWithWeatherData(response.body());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {

            }
        });
    }

    private void updateUIWithWeatherData(WeatherResponse weatherResponse) {
        // Current weather
        binding.temperature.setText(String.format("%s°C", weatherResponse.getCurrent().getTempC()));
        binding.weatherDescription.setText(weatherResponse.getCurrent().getCondition().getText());
        binding.weatherDetails.setText(String.format("Humidity: %s%%\nWind: %s km/h", weatherResponse.getCurrent().getHumidity(), weatherResponse.getCurrent().getWindKph()));

        Glide.with(this).load("https:" + weatherResponse.getCurrent().getCondition().getIcon()).into(binding.weatherIcon);


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
        Glide.with(this).load("https:" + forecastDay.getDay().getCondition().getIcon()).into(dailyWeatherIcon);
        dailyTemperature.setText(String.format("%s°C / %s°C", forecastDay.getDay().getMaxTempC(), forecastDay.getDay().getMinTempC()));

        binding.dailyForecastContainer.addView(dailyForecastView);
    }

    private void fetchVenues(ApiService apiService, String lat, String lon) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("latitude", lat);
        requestBody.put("longitude", lon);
        Call<ApiResponseArray<Venue>> call = apiService.getNearestVenues(requestBody);
        call.enqueue(new Callback<ApiResponseArray<Venue>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ApiResponseArray<Venue>> call, Response<ApiResponseArray<Venue>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // Get the list of Venue from the ApiResponseArray
                    List<Venue> venueList = response.body().getData();
                    if (venueList != null && !venueList.isEmpty()) {
                        // Update the list and notify adapter
                        venuesList.clear(); // Clear the current list
                        venuesList.addAll(venueList); // Add all fetched venues to the list
                        venuesAdapter.notifyDataSetChanged(); // Notify adapter about data change
                    } else {
                        Toast.makeText(getContext(), "No venues found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseArray<Venue>> call, Throwable t) {
                Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                getCurrentLocation();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}
