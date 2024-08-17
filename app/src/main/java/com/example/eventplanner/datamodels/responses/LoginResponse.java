package com.example.eventplanner.datamodels.responses;

public class LoginResponse {
    private String userId;
    private String token;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // Constructors, Getters, and Setters
    public LoginResponse(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }
}
