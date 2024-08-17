package com.example.eventplanner.datamodels.requests;

// SignupRequest.java
public class SignupRequest {
    private String fullname;
    private String email;
    private String password;
    private String confirmPassword;
    private String phone;

    // Constructors, Getters, and Setters
    public SignupRequest(String fullname, String email, String password, String confirmPassword) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public SignupRequest(String fullname, String email, String phone, String password, String confirmPassword) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}

