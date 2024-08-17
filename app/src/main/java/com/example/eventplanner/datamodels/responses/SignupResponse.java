package com.example.eventplanner.datamodels.responses;

// SignupResponse.java
public class SignupResponse {
    private String userId;
    private String token;

    // Constructors, Getters, and Setters
    public SignupResponse(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

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
// Getters and Setters
    // ...
}
