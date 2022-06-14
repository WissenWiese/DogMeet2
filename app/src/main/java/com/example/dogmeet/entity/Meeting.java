package com.example.dogmeet.entity;

import androidx.room.Entity;

import java.util.Objects;

@Entity
public class Meeting {
    public String uid, title, address, date, creator, creatorUid, time, nubmerMember, description, tupeDog;

    public Meeting() {}

    public Meeting(String uid, String title, String address, String date, String creator,
                   String creatorUid, String time, String nubmerMember, String description, String tupeDog){
        this.uid=uid;
        this.title=title;
        this.address=address;
        this.date=date;
        this.creator=creator;
        this.creatorUid=creatorUid;
        this.time=time;
        this.nubmerMember=nubmerMember;
        this.description=description;
        this.tupeDog=tupeDog;

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNubmerMember() {
        return nubmerMember;
    }

    public void setNubmerMember(String nubmerMember) {
        this.nubmerMember = nubmerMember;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTupeDog() {
        return tupeDog;
    }

    public void setTupeDog(String tupeDog) {
        this.tupeDog = tupeDog;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meeting meeting = (Meeting) o;
        return title.equals(meeting.title) && address.equals(meeting.address)
                && date.equals(meeting.date) && creator.equals(meeting.creator)
                && creatorUid.equals(meeting.creatorUid)
                && time.equals(meeting.time)
                && nubmerMember.equals(meeting.nubmerMember)
                && description.equals(meeting.description)
                && tupeDog.equals(meeting.tupeDog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, address, date, creator, creatorUid, time, nubmerMember, description, tupeDog);
    }
}
