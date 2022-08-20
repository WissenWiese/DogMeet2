package com.example.dogmeet.entity;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class Walker {
    String userName, userUri, userUId, latitude, longitude;

    public Walker(){

    }

    public Walker(String userName, String userUri, String userUId, String latitude, String longitude) {
        this.userName=userName;
        this.userUId=userUId;
        this.userUri=userUri;
        this.latitude = latitude;
        this.longitude =longitude;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserUri() {
        return userUri;
    }

    public void setUserUri(String userUri) {
        this.userUri = userUri;
    }

    public String getUserUId() {
        return userUId;
    }

    public void setUserUId(String userUId) {
        this.userUId = userUId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
