package com.example.eventplanner.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Feedback implements Serializable {
//    @SerializedName("id")
//    private int id;

    @SerializedName("feedback")
    private String feedback;

    @SerializedName("profile_picture")
    private String profile_picture;

    @SerializedName("username")
    private String username;

    public Feedback(String feedback, String profilePic, String name) {
        this.feedback = feedback;
        this.profile_picture = profilePic;
        this.username = name;
    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
