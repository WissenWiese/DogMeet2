package com.example.dogmeet.entity;

import java.util.ArrayList;

public class Walker {
    String userName, userUri, userUId, latitude, longitude, massage;
    ArrayList<Pet> pets;
    public Walker(){

    }

    public Walker(String userName, String userUri, String userUId,
                  String latitude, String longitude, String massage, ArrayList<Pet> pets) {
        this.userName=userName;
        this.userUId=userUId;
        this.userUri=userUri;
        this.latitude = latitude;
        this.longitude =longitude;
        this.massage=massage;
        this.pets=pets;
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

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    public ArrayList<Pet> getPets() {
        return pets;
    }

    public void setPets(ArrayList<Pet> pets) {
        this.pets = pets;
    }
}
