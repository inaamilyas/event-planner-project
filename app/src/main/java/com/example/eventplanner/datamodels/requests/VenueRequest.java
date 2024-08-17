package com.example.eventplanner.datamodels.requests;

import android.net.Uri;

public class VenueRequest {
    private String name;
    private String phone;
    private double latitude;

    private double longitude;
    private String about;
    private Uri picture;

    public VenueRequest(String name, String phone, String about, double latitude, double longitude, Uri imageUri) {
        this.name = name;
        this.phone = phone;
        this.about = about;
        this.latitude = latitude;
        this.longitude = longitude;
        this.picture = imageUri;
    }
}