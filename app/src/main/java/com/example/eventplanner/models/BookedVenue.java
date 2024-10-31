package com.example.eventplanner.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BookedVenue implements Serializable {
    @SerializedName("id")
    int id;
    @SerializedName("event")
    Event event;
    @SerializedName("venue")
    Venue venue;
    @SerializedName("date")
    String bookingDate;
    @SerializedName("start_time")
    String startTime;
    @SerializedName("end_time")
    String endTime;
    @SerializedName("menu_item")
    MenuItem menuItem;
    @SerializedName("status")
    int status;
    @SerializedName("phone_number")
    String phoneNumber;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
