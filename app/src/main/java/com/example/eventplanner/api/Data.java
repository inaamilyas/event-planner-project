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

    @SerializedName("all_events")
    private List<Event> allEvents;

    @SerializedName("venues")
    private List<Venue> venues;

    @SerializedName("all_venues")
    private List<Venue> allRandomVenues;

    public List<Venue> getAllRandomVenues() {
        return allRandomVenues;
    }

    public void setAllRandomVenues(List<Venue> allRandomVenues) {
        this.allRandomVenues = allRandomVenues;
    }

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

    public List<Event> getAllEvents() {
        return allEvents;
    }

    public void setAllEvents(List<Event> allEvents) {
        this.allEvents = allEvents;
    }
}