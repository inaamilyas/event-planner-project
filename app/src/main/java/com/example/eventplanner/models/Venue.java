package com.example.eventplanner.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Venue implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("phone")
    private String phone;

    @SerializedName("address")
    private String address;

    @SerializedName("about")
    private String about;

    @SerializedName("picture")
    private String picture;

    @SerializedName("distance")
    private String distance;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("owner")
    private VenueManager owner;

    @SerializedName("status")
    private int status;

    @SerializedName("gallery")
    private List<String> gallery;

    @SerializedName("venue_food_menu")
    private List<MenuItem> foodMenuItems;

    @SerializedName("venue_feedbacks")
    private List<Feedback> venueFeedbacks;

    public Venue(int id, String name, double latitude, double longitude, String phone, String address, String about, String image) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.address = address;
        this.about = about;
        this.picture = image;
    }

    public Venue(int id, String name, double latitude, double longitude, String phone, String address, String about, String picture, String distance) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.address = address;
        this.about = about;
        this.picture = picture;
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public VenueManager getOwner() {
        return owner;
    }

    public void setOwner(VenueManager owner) {
        this.owner = owner;
    }

    public List<MenuItem> getFoodMenuItems() {
        return foodMenuItems;
    }

    public void setFoodMenuItems(List<MenuItem> foodMenuItems) {
        this.foodMenuItems = foodMenuItems;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Feedback> getVenueFeedbacks() {
        return venueFeedbacks;
    }

    public void setVenueFeedbacks(List<Feedback> venueFeedbacks) {
        this.venueFeedbacks = venueFeedbacks;
    }

    public List<String> getGallery() {
        return gallery;
    }

    public void setGallery(List<String> gallery) {
        this.gallery = gallery;
    }
}
