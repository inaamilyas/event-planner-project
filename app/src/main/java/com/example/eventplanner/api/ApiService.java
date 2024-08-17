package com.example.eventplanner.api;

// ApiService.java

import com.example.eventplanner.datamodels.requests.LoginRequest;
import com.example.eventplanner.datamodels.requests.SignupRequest;
import com.example.eventplanner.datamodels.responses.LoginResponse;
import com.example.eventplanner.datamodels.responses.SignupResponse;
import com.example.eventplanner.datamodels.responses.VenuesResponse;
import com.example.eventplanner.models.Venue;

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
    Call<ApiResponse<SignupResponse>> signup(@Body SignupRequest signupRequest);


    @Headers({"Content-Type: application/json"})
    @POST("api/v1/user/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest loginRequest);

    @Headers({"Content-Type: application/json"})
    @POST("api/v1/venue-manager/signup")
    Call<ApiResponse<SignupResponse>> venueManagerSignup(@Body SignupRequest signupRequest);

    @Headers({"Content-Type: application/json"})
    @POST("api/v1/venue-manager/login")
    Call<ApiResponse<LoginResponse>> venueManagerLogin(@Body LoginRequest loginRequest);


    @Multipart
    @POST("api/v1/venues")
    Call<ApiResponse<String>> addVenue(
            @Part MultipartBody.Part picture,
            @Part("name") RequestBody name,
            @Part("phone") RequestBody phone,
            @Part("about") RequestBody about,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude
    );

    @GET("api/v1/venues")
    Call<ApiResponseArray<Venue>> getVenues();


}
