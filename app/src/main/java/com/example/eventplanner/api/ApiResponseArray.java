package com.example.eventplanner.api;

import java.util.List;

public class ApiResponseArray<T> {
    private int code;
    private String status;
    private String message;
    private List<T> data;// Using JsonElement to handle both arrays and single objects

    // Constructors
    public ApiResponseArray(int code, String status, String message, List<T> data) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
