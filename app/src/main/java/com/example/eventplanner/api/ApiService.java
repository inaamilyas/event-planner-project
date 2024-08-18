package com.example.eventplanner.api;

// ApiService.java

import com.example.eventplanner.models.User;
import com.example.eventplanner.models.Venue;
import com.example.eventplanner.models.VenueManager;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @Headers({"Content-Type: application/json"})
    @POST("api/v1/user/signup")
    Call<ApiResponse<User>> signup(@Body Map<String, Object> requestBody);

    @Headers({"Content-Type: application/json"})
    @POST("api/v1/user/login")
    Call<ApiResponse<User>> login(@Body Map<String, Object> requestBody);

    @Headers({"Content-Type: application/json"})
    @POST("api/v1/venue-manager/signup")
    Call<ApiResponse<VenueManager>> venueManagerSignup(@Body Map<String, Object> requestBody);

    @Headers({"Content-Type: application/json"})
    @POST("api/v1/venue-manager/login")
    Call<ApiResponse<VenueManager>> venueManagerLogin(@Body Map<String, Object> requestBody);

    @Multipart
    @POST("api/v1/venues")
    Call<ApiResponse<String>> addVenue(@Part MultipartBody.Part picture, @Part("name") RequestBody name, @Part("phone") RequestBody phone, @Part("about") RequestBody about, @Part("latitude") RequestBody latitude, @Part("longitude") RequestBody longitude);

    @GET("api/v1/venues")
    Call<ApiResponseArray<Venue>> getVenues();

    @Headers({"Content-Type: application/json"})
    @POST("api/v1/venues/suggest/nearest")
    Call<ApiResponseArray<Venue>> getNearestVenues(@Body Map<String, Object> requestBody);

}
