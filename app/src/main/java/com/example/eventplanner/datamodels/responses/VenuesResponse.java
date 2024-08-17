package com.example.eventplanner.datamodels.responses;

import com.example.eventplanner.models.Venue;

import java.util.List;

public class VenuesResponse {
    private List<Venue> venuesList;

    public VenuesResponse(List<Venue> venuesList) {
        this.venuesList = venuesList;
    }

    public List<Venue> getVenuesList() {
        return venuesList;
    }

    public void setVenuesList(List<Venue> venuesList) {
        this.venuesList = venuesList;
    }
}
