package com.example.eventplanner.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.eventplanner.config.AppConfig;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("profile_pic")
    private String profilePic;

    private static final String PREFS_NAME = AppConfig.SHARED_PREF_NAME;
    private static final String USER_KEY = "user";

    public User(int id, String name, String email, String profilePic) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profilePic = profilePic;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    // Save user object to SharedPreferences
    public void saveToPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String userJson = gson.toJson(this);
        editor.putString(USER_KEY, userJson);
        editor.apply();
    }

    // Retrieve user object from SharedPreferences
    public static User getFromPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String userJson = sharedPreferences.getString(USER_KEY, null);
        return gson.fromJson(userJson, User.class);
    }

    // Clear user object from SharedPreferences
    public static void clearPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(USER_KEY);
        editor.apply();
    }

}
