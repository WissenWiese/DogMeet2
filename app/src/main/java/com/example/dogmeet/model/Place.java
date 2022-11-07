package com.example.dogmeet.model;

import java.util.Date;

public class Place {
    private String address, image, type;
    private Double latitude, longitude;

    public Place(String address, Double latitude, Double longitude, String image) {
        this.address = address;
        this.latitude = latitude;
        this.longitude =longitude;
        this.image=image;
    }

    public Place(){

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getImage() {
        return image;
    }


    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
