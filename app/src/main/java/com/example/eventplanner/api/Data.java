package com.example.eventplanner.api;

import com.example.eventplanner.models.Event;
import com.example.eventplanner.models.User;
import com.example.eventplanner.models.Venue;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Data {
    @SerializedName("user")
    private User user;

    @SerializedName("events")
    private List<Event> events;

    @SerializedName("venues")
    private List<Venue> venues;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<Venue> getVenues() {
        return venues;
    }

    public void setVenues(List<Venue> venues) {
        this.venues = venues;
    }
}