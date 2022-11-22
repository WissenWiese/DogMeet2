package com.example.dogmeet.entity;

public class Place {
    private String address, image, type, contact, openHours, rating, name, uid;
    private Double latitude, longitude;

    public Place(String address, Double latitude, Double longitude, String image, String type,
                 String contact, String openHours, String rating, String name, String uid) {
        this.address = address;
        this.latitude = latitude;
        this.longitude =longitude;
        this.image=image;
        this.type=type;
        this.contact=contact;
        this.openHours=openHours;
        this.rating=rating;
        this.name=name;
        this.uid=uid;
    }

    public Place(){

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getOpenHours() {
        return openHours;
    }

    public void setOpenHours(String openHours) {
        this.openHours = openHours;
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
