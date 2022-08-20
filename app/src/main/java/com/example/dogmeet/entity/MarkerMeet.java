package com.example.dogmeet.entity;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

public class MarkerMeet {
    String address, meetsUid, tipe;
    LatLng point;

    public MarkerMeet(){

    }

    public MarkerMeet(String address, String meetsUid, String tipe, LatLng point) {
        this.address=address;
        this.meetsUid=meetsUid;
        this.tipe=tipe;
        this.point=point;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public LatLng getPoint() {
        return point;
    }

    public void setPoint(LatLng point) {
        this.point = point;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMeetsUid() {
        return meetsUid;
    }

    public void setMeetsUid(String meetsUid) {
        this.meetsUid = meetsUid;
    }
}
