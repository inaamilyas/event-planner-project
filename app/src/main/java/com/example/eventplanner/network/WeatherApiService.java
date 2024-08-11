package com.example.eventplanner.network;

import com.example.eventplanner.models.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {
    @GET("v1/forecast.json")
    Call<WeatherResponse> getWeather(
            @Query("key") String apiKey,
            @Query("q") String location,
            @Query("days") int days
    );
}
