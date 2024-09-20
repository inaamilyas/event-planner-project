package com.example.eventplanner.VenueManager;

import com.example.eventplanner.models.MenuItem;

public interface OnMenuItemUpdatedListener {
    void onMenuItemUpdated(MenuItem updatedMenuItem, int position);
}

