package com.example.dogmeet;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;
import java.util.Objects;

@Entity
public class Meeting {
    public String title, address, date, creator, creatorUid;

    public Meeting() {}

    public Meeting(String title, String address, String date, String creator, String creatorUid){
        this.title=title;
        this.address=address;
        this.date=date;
        this.creator=creator;
        this.creatorUid=creatorUid;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatorUid() {
        return creatorUid;
    }

    public void setCreatorUid(String creatorUid) {
        this.creatorUid = creatorUid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meeting meeting = (Meeting) o;
        return title.equals(meeting.title) && address.equals(meeting.address) && date.equals(meeting.date) && creator.equals(meeting.creator) && creatorUid.equals(meeting.creatorUid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, address, date, creator, creatorUid);
    }
}
