package com.example.dogmeet.entity;

import java.util.Date;

public class Doghanter {
    private String user, message, latitude, longitude, image;
    private long time;

    public Doghanter(String user, String message, String latitude, String longitude, String image) {
        this.user = user;
        this.message = message;
        this.latitude = latitude;
        this.longitude =longitude;
        this.image=image;

        // Initialize to current time
        time = new Date().getTime();
    }

    public Doghanter(){

    }

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getImage() {
        return image;
    }

    public long getTime() {
        return time;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
