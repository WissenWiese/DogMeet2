package com.example.dogmeet.entity;

import android.content.Intent;
import android.net.Uri;

import androidx.room.Entity;

import com.example.dogmeet.Activity.MeetingActivity;
import com.example.dogmeet.Constant;

import java.util.ArrayList;
import java.util.Objects;

@Entity
public class Meeting {
    public String uid, title, address, creatorUid, description, urlImage;
    public int numberMember, numberComments;
    public long date;
    public Meeting() {}

    public Meeting(String uid, String title, String address, long date,
                   String creatorUid, int numberMember, int numberComments,
                   String description, String urlImage){
        this.uid=uid;
        this.title=title;
        this.address=address;
        this.date=date;
        this.creatorUid=creatorUid;
        this.numberMember=numberMember;
        this.numberComments=numberComments;
        this.description=description;
        this.urlImage= urlImage;

    }

    public String getUid(){
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getCreatorUid() {
        return creatorUid;
    }

    public void setCreatorUid(String creatorUid) {
        this.creatorUid = creatorUid;
    }

    public int getNumberMember() {
        return numberMember;
    }

    public void setNumberMember(int numberMember) {
        this.numberMember = numberMember;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public int getNumberComments() {
        return numberComments;
    }

    public void setNumberComments(int numberComments) {
        this.numberComments = numberComments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meeting meeting = (Meeting) o;
        return title.equals(meeting.title) && address.equals(meeting.address)
                && creatorUid.equals(meeting.creatorUid)
                && description.equals(meeting.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, address, date, creatorUid, numberMember, description);
    }
}
