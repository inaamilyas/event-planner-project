package com.example.eventplanner.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.EventsAdapter;
import com.example.eventplanner.adapters.VenuesAdapter;
import com.example.eventplanner.api.ApiClient;
import com.example.eventplanner.api.ApiResponse;
import com.example.eventplanner.api.ApiService;
import com.example.eventplanner.api.Data;
import com.example.eventplanner.config.AppConfig;
import com.example.eventplanner.databinding.FragmentHomeBinding;
import com.example.eventplanner.models.Event;
import com.example.eventplanner.models.User;
import com.example.eventplanner.models.Venue;
import com.example.eventplanner.models.WeatherResponse;
import com.example.eventplanner.network.WeatherApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private VenuesAdapter venuesAdapter;
    private VenuesAdapter allVenuesAdapter;
    public static EventsAdapter homeEventsAdapter;
//    public static EventsAdapter AllHomeEventsAdapter;
    private FragmentHomeBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    public static ArrayList<Venue> venuesList = new ArrayList<>();
    public static ArrayList<Venue> allVenuesRandomList = new ArrayList<>();
    public static ArrayList<Event> eventList = new ArrayList<>();
//    public static ArrayList<Event> allEventList = new ArrayList<>();
    public static User user = null;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout using view binding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

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

        binding.allVenuesRecyclerHome.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        allVenuesAdapter = new VenuesAdapter(allVenuesRandomList);
        binding.allVenuesRecyclerHome.setAdapter(allVenuesAdapter);

        // Initialize RecyclerViews
        binding.eventsRecyclerHome.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        homeEventsAdapter = new EventsAdapter(eventList);
        binding.eventsRecyclerHome.setAdapter(homeEventsAdapter);

        // all events
//        binding.allEventsRecyclerHome.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        AllHomeEventsAdapter = new EventsAdapter(allEventList);
//        binding.allEventsRecyclerHome.setAdapter(AllHomeEventsAdapter);

        binding.seeAllEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventsFragment fragment = new EventsFragment();
                FragmentManager fragmentManager = getFragmentManager();
                assert fragmentManager != null;
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        binding.seeAllVenues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VenuesFragment fragment = new VenuesFragment();
                FragmentManager fragmentManager = getFragmentManager();
                assert fragmentManager != null;
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        // Set up the refresh listener
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCurrentLocation();
            }
        });

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
                    fetchDetails(apiService, lat, lon);
                    // After the refresh is complete, hide the loading indicator
                    binding.swipeRefreshLayout.setRefreshing(false);
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
        binding.forcastText.setText("Daily Forecast");
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

    private void fetchDetails(ApiService apiService, String lat, String lon) {

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(AppConfig.SHARED_PREF_NAME, MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user", null);
        Gson gson = new Gson();
        user = gson.fromJson(userJson, User.class);
        int userId = user.getId();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("latitude", lat);
        requestBody.put("longitude", lon);
        requestBody.put("user_id", userId);
        Call<ApiResponse<Data>> call = apiService.getCurrentUserInformation(userId, requestBody);
        call.enqueue(new Callback<ApiResponse<Data>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ApiResponse<Data>> call, Response<ApiResponse<Data>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.code() == 200) {
                        user = response.body().getData().getUser();
                        user.saveToPreferences(requireContext());

                        // Get the list of Venue from the ApiResponseArray
                        List<Venue> venueList = response.body().getData().getVenues();
                        if (venueList != null && !venueList.isEmpty()) {
                            // Update the list and notify adapter
                            venuesList.clear();
                            venuesList.addAll(venueList);
                            venuesAdapter.notifyDataSetChanged();
                        }

                        // Get the list of random Venue from the ApiResponseArray
                        List<Venue> randomVenuList = response.body().getData().getAllRandomVenues();
                        if (venueList != null && !venueList.isEmpty()) {
                            // Update the list and notify adapter
                            allVenuesRandomList.clear();
                            allVenuesRandomList.addAll(randomVenuList);
                            allVenuesAdapter.notifyDataSetChanged();
                        }

                        List<Event> allEvents = response.body().getData().getEvents();
                        if (allEvents != null && !allEvents.isEmpty()) {
                            // Update the list and notify adapter
                            eventList.clear();
                            eventList.addAll(allEvents);
                            homeEventsAdapter.notifyDataSetChanged();

                        } else {
                            // Show the "No Events" TextView when there are no events
                            binding.noEvents.setVisibility(View.VISIBLE);
                        }

//                        List<Event> allUserEvents = response.body().getData().getAllEvents();
//                        if (allEvents != null && !allEvents.isEmpty()) {
//                            // Update the list and notify adapter
//                            allEventList.clear();
//                            allEventList.addAll(allUserEvents);
//                            AllHomeEventsAdapter.notifyDataSetChanged();
//
//                        } else {
//                            // Show the "No Events" TextView when there are no events
//                            binding.noAllEvents.setVisibility(View.VISIBLE);
//                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getContext(), "Check Internet connection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Data>> call, Throwable t) {
                Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}
