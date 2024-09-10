package com.example.eventplanner.api;

// ApiService.java

import com.example.eventplanner.models.Event;
import com.example.eventplanner.models.User;
import com.example.eventplanner.models.Venue;
import com.example.eventplanner.models.VenueManager;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

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

    @Multipart
    @POST("api/v1/events")
    Call<ApiResponse<Event>> addEvent(@Part MultipartBody.Part picture, @Part("name") RequestBody name, @Part("date") RequestBody date, @Part("time") RequestBody time, @Part("budget") RequestBody budget, @Part("no_of_guests") RequestBody noOfGuests, @Part("about") RequestBody about);

    @Multipart
    @PUT("api/v1/events/{event_id}")
    Call<ApiResponse<String>> updateEvent(@Path("event_id") String event_id, @Part MultipartBody.Part picture, @Part("name") RequestBody name, @Part("date") RequestBody date, @Part("time") RequestBody time, @Part("budget") RequestBody budget, @Part("no_of_guests") RequestBody noOfGuests, @Part("about") RequestBody about);

    @FormUrlEncoded
    @PUT("api/v1/events/{event_id}")
    Call<ApiResponse<String>> updateEventWithoutImage(@Path("event_id") String event_id, @Field("name") String name, @Field("date") String date, @Field("time") String time, @Field("budget") String budget, @Field("no_of_guests") String noOfGuests, @Field("about") String about);

    @Headers({"Content-Type: application/json"})
    @DELETE("api/v1/events/{event_id}")
    Call<ApiResponse> deleteEvent(@Path("event_id") String event_id);

    @GET("api/v1/venues")
    Call<ApiResponseArray<Venue>> getVenues();

    @Headers({"Content-Type: application/json"})
    @POST("api/v1/venues/suggest/nearest")
    Call<ApiResponseArray<Venue>> getNearestVenues(@Body Map<String, Object> requestBody);

    @Headers({"Content-Type: application/json"})
    @POST("api/v1/user/info")
    Call<ApiResponse<Data>> getCurrentUserInformation(@Header("user_id") int userId, @Body Map<String, Object> requestBody);

    @Headers({"Content-Type: application/json"})
    @POST("api/v1/venues/booking/{venue_id}")
    Call<ApiResponse<String>> bookVenue(@Path("venue_id") String venueId, @Body Map<String, Object> requestBody);

    @Multipart
    @PUT("api/v1/venues/{venue_id}")
    Call<ApiResponse<Venue>> updateVenue(
            @Path("venue_id") String venueId,                   // Path parameter for the venue ID
            @Part MultipartBody.Part picture,                   // Part for the picture (image)
            @Part("name") RequestBody name,                     // Part for the venue name
            @Part("phone") RequestBody phone,                   // Part for the phone number
            @Part("about") RequestBody about,                   // Part for the description
            @Part("latitude") RequestBody latitude,             // Part for latitude
            @Part("longitude") RequestBody longitude            // Part for longitude
    );

    @Headers({"Content-Type: application/json"})
    @DELETE("api/v1/venues/{venue_id}")
    Call<ApiResponse> deleteVenue(@Path("venue_id") String venue_id);


    @Headers({"Content-Type: application/json"})
    @GET("api/v1/venues/{venue_id}")
    Call<ApiResponse> getMenuItems(@Path("venue_id") String venue_id);

    @Headers({"Content-Type: application/json"})
    @DELETE("api/v1/venues/{venue_id}")
    Call<ApiResponse> deleteMenuItem(@Path("venue_id") String venue_id);

    @Multipart
    @POST("api/v1/venues/{venue_id}")
    Call<ApiResponse<Venue>> addMenuItems(
            @Path("venue_id") String venueId,
            @Part MultipartBody.Part picture,
            @Part("name") RequestBody name,
            @Part("price") RequestBody price
    );

    @Multipart
    @POST("api/v1/venues/{venue_id}")
    Call<ApiResponse<Venue>> updateMenuItems(
            @Path("venue_id") String venueId,
            @Part MultipartBody.Part picture,
            @Part("name") RequestBody name,
            @Part("price") RequestBody price
    );

    @Headers({"Content-Type: application/json"})
    @POST("api/v1/venues/booking/{venue_id}")
    Call<ApiResponse<String>> bookMenuItems(@Body Map<String, Object> requestBody);


}
